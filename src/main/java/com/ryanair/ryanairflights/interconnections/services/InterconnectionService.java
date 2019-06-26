package com.ryanair.ryanairflights.interconnections.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ryanair.ryanairflights.interconnections.Interconnection;
import com.ryanair.ryanairflights.interconnections.Leg;
import com.ryanair.ryanairflights.interconnections.RequestedFlight;
import com.ryanair.ryanairflights.routes.Route;
import com.ryanair.ryanairflights.routes.RouteRepository;
import com.ryanair.ryanairflights.schedule.Schedule;
import com.ryanair.ryanairflights.schedule.ScheduleService;

@Service
public class InterconnectionService {
	
	@Autowired
	private ScheduleService scheduleService;
	
	@Autowired
	private LegService legService;
	
	private final RouteRepository routeRepository;

	InterconnectionService(RouteRepository routeRepository) {
	    this.routeRepository = routeRepository;
	  }
	
	RequestedFlight requestedFlight;

	public List<Interconnection> findInterconnections(String departure, String arrival, LocalDateTime requestedDepartureDateTime, LocalDateTime requestedArrivalDateTime) {
		this.requestedFlight = new RequestedFlight(departure, arrival, requestedDepartureDateTime.getMonthValue(), requestedDepartureDateTime.getYear(), requestedDepartureDateTime, requestedArrivalDateTime);
		

		List<Interconnection> interconnections = new ArrayList<Interconnection>();
		routeRepository.findAllByAirportFrom(departure);
		
	
		List<Route> routesByDeparture = routeRepository.findAllByAirportFrom(departure);
		List<Route> routesByArrival = routeRepository.findAllByAirportTo(arrival);
	
		/////direct routes
		List<Route> directRoutes = routeRepository.findAllByAirportFromAndAirportTo(departure, arrival);
		Schedule routeSchedule = scheduleService.findSchedule(departure,arrival, requestedFlight);
		if(directRoutes != null && !directRoutes.isEmpty()) {
			List<Leg> directLegs = legService.createLegList(directRoutes.get(0), routeSchedule, requestedFlight);
			for(Leg leg : directLegs) {
				if(validateLegSchedule(requestedDepartureDateTime, requestedArrivalDateTime, interconnections, leg)) {
					createDirectInterconnection(interconnections, leg);
				};
			}	
		}

	
		//////////end direct routes
		
		for (Route firstLegRoute : routesByDeparture) {
			for (Route secondLegRoute : routesByArrival) {
				if (firstLegRoute.getAirportTo().equals(secondLegRoute.getAirportFrom()) && !firstLegRoute.getAirportTo().equals(arrival)) {
					createInterconnetion(requestedDepartureDateTime, requestedArrivalDateTime, interconnections, firstLegRoute,
							secondLegRoute);
				}
			}
		}
		return interconnections;
		
	}
	
	private void createDirectInterconnection(List<Interconnection> interconnections, Leg leg) {
		ArrayList<Leg> legs = new ArrayList<Leg>();
		legs.add(leg);
		Interconnection interconnectedFlight = new Interconnection();
		interconnectedFlight.setLegs(legs);
		interconnectedFlight.setStops("0");
		interconnections.add(interconnectedFlight);
	}


	private Boolean validateLegSchedule(LocalDateTime requestedDepartureDateTime, LocalDateTime requestedArrivalDateTime,
			List<Interconnection> interconnections, Leg leg) {
		LocalDateTime legDepartureDateTime = LocalDateTime.parse(leg.getDepartureDateTime());
		LocalDateTime legArrivalDateTime = LocalDateTime.parse(leg.getArrivalDateTime());
			if(legDepartureDateTime.isAfter(requestedDepartureDateTime) && legArrivalDateTime.isBefore(requestedArrivalDateTime)) {
					return true;
			}
			return false;
	}


	private void createInterconnetion(LocalDateTime requestedDepartureDateTime, LocalDateTime requestedArrivalDateTime, 
			List<Interconnection> interconnections, Route firstLegRoute, Route secondLegRoute) {
		
		Schedule firstLegRouteSchedule = scheduleService.findSchedule(firstLegRoute.getAirportFrom(),firstLegRoute.getAirportTo(), requestedFlight);
		
		Schedule secondLegRouteSchedule = scheduleService.findSchedule(secondLegRoute.getAirportFrom(),secondLegRoute.getAirportTo(), requestedFlight);

		List<Leg> possibleFirstLegs = legService.createLegList(firstLegRoute, firstLegRouteSchedule, requestedFlight);
		List<Leg> possibleSecondLegs = legService.createLegList(secondLegRoute, secondLegRouteSchedule, requestedFlight);
		
		combineLegs(requestedDepartureDateTime, requestedArrivalDateTime, interconnections, possibleFirstLegs, possibleSecondLegs);
	}


	private void combineLegs(LocalDateTime requestedDepartureDateTime, LocalDateTime requestedArrivalDateTime,
			List<Interconnection> interconnections, List<Leg> possibleFirstLegs, List<Leg> possibleSecondLegs) {
		for(Leg firstLeg : possibleFirstLegs) {
			for(Leg secondLeg : possibleSecondLegs) {
				
				LocalDateTime firstLegDepartureDateTime = LocalDateTime.parse(firstLeg.getDepartureDateTime());
				LocalDateTime firstLegArrivalDateTime = LocalDateTime.parse(firstLeg.getArrivalDateTime());
				
				LocalDateTime secondLegDepartureDateTime = LocalDateTime.parse(secondLeg.getDepartureDateTime());
				LocalDateTime secondLegArrivalDateTime = LocalDateTime.parse(secondLeg.getArrivalDateTime());
				
				validateTimeBetweenLegs(requestedDepartureDateTime, requestedArrivalDateTime, interconnections,
						firstLeg, secondLeg, firstLegDepartureDateTime, firstLegArrivalDateTime,
						secondLegDepartureDateTime, secondLegArrivalDateTime);
			}
		}
	}


	private void validateTimeBetweenLegs(LocalDateTime requestedDepartureDateTime,
			LocalDateTime requestedArrivalDateTime, List<Interconnection> interconnections, Leg firstLeg, Leg secondLeg,
			LocalDateTime firstLegDepartureDateTime, LocalDateTime firstLegArrivalDateTime,
			LocalDateTime secondLegDepartureDateTime, LocalDateTime secondLegArrivalDateTime) {
		
		if(firstLegDepartureDateTime.isBefore(secondLegArrivalDateTime)) {
			if(firstLegDepartureDateTime.isAfter(requestedDepartureDateTime) && secondLegArrivalDateTime.isBefore(requestedArrivalDateTime)) {
				long hours = ChronoUnit.HOURS.between(firstLegArrivalDateTime, secondLegDepartureDateTime);
				if (hours >= 2) {
					createInterconnetion(interconnections, firstLeg, secondLeg);
				}
			}
		}
	}


	private void createInterconnetion(List<Interconnection> interconnections, Leg firstLeg, Leg secondLeg) {
		ArrayList<Leg> legs = new ArrayList<Leg>();
		legs.add(firstLeg);
		legs.add(secondLeg);

		Interconnection interconnectedFlight = new Interconnection();
		interconnectedFlight.setLegs(legs);
		interconnectedFlight.setStops("1");
		interconnections.add(interconnectedFlight);
	}

	
	

	

}
