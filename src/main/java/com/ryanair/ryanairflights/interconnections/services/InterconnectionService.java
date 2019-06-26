package com.ryanair.ryanairflights.interconnections.services;

import java.time.LocalDateTime;
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
	
	private static final String ONE_STOP = "1";

	private static final String NON_STOP = "0";

	@Autowired
	private ScheduleService scheduleService;
	
	@Autowired
	private LegsScheduleValidator legsScheduleValidator;
	
	@Autowired
	private LegService legService;
	
	@Autowired
	private LegsCombiner legsCombiner;
	
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
	
		findNonConnectingFlights(departure, arrival, requestedDepartureDateTime, requestedArrivalDateTime,
				interconnections);
		
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

	private void findNonConnectingFlights(String departure, String arrival, LocalDateTime requestedDepartureDateTime,
			LocalDateTime requestedArrivalDateTime, List<Interconnection> interconnections) {
		
		List<Route> directRoutes = routeRepository.findAllByAirportFromAndAirportTo(departure, arrival);
		Schedule routeSchedule = scheduleService.findSchedule(departure,arrival, requestedFlight);
		if(directRoutes != null && !directRoutes.isEmpty()) {
			List<Leg> directLegs = legService.createLegList(directRoutes.get(0), routeSchedule, requestedFlight);
			for(Leg leg : directLegs) {
				if(legsScheduleValidator.validateLegSchedule(requestedDepartureDateTime, requestedArrivalDateTime, interconnections, leg)) {
					createDirectInterconnection(interconnections, leg);
				};
			}	
		}
	}
	
	private void createDirectInterconnection(List<Interconnection> interconnections, Leg leg) {
		ArrayList<Leg> legs = new ArrayList<Leg>();
		legs.add(leg);
		Interconnection interconnectedFlight = new Interconnection();
		interconnectedFlight.setLegs(legs);
		interconnectedFlight.setStops(NON_STOP);
		interconnections.add(interconnectedFlight);
	}



	private void createInterconnetion(LocalDateTime requestedDepartureDateTime, LocalDateTime requestedArrivalDateTime, 
			List<Interconnection> interconnections, Route firstLegRoute, Route secondLegRoute) {
		
		Schedule firstLegRouteSchedule = scheduleService.findSchedule(firstLegRoute.getAirportFrom(),firstLegRoute.getAirportTo(), requestedFlight);
		
		Schedule secondLegRouteSchedule = scheduleService.findSchedule(secondLegRoute.getAirportFrom(),secondLegRoute.getAirportTo(), requestedFlight);

		List<Leg> possibleFirstLegs = legService.createLegList(firstLegRoute, firstLegRouteSchedule, requestedFlight);
		List<Leg> possibleSecondLegs = legService.createLegList(secondLegRoute, secondLegRouteSchedule, requestedFlight);
		
		legsCombiner.combineLegs(requestedDepartureDateTime, requestedArrivalDateTime, interconnections, possibleFirstLegs, possibleSecondLegs);
	}



	void createInterconnetion(List<Interconnection> interconnections, Leg firstLeg, Leg secondLeg) {
		ArrayList<Leg> legs = new ArrayList<Leg>();
		legs.add(firstLeg);
		legs.add(secondLeg);

		Interconnection interconnectedFlight = new Interconnection();
		interconnectedFlight.setLegs(legs);
		interconnectedFlight.setStops(ONE_STOP);
		interconnections.add(interconnectedFlight);
	}

	
	

	

}
