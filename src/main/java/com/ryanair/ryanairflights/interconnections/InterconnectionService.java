package com.ryanair.ryanairflights.interconnections;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ryanair.ryanairflights.interconnections.leg.Leg;
import com.ryanair.ryanairflights.interconnections.leg.LegService;
import com.ryanair.ryanairflights.interconnections.leg.LegsCombinerService;
import com.ryanair.ryanairflights.interconnections.routecombo.RouteCombo;
import com.ryanair.ryanairflights.interconnections.routecombo.RoutesCombinerService;
import com.ryanair.ryanairflights.routes.Route;
import com.ryanair.ryanairflights.routes.RouteService;
import com.ryanair.ryanairflights.schedule.Schedule;
import com.ryanair.ryanairflights.schedule.ScheduleService;

@Service
public class InterconnectionService {
	
	@Autowired
	private RouteService routeService;
	
	@Autowired
	private ScheduleService scheduleService;
	
	@Autowired
	private RoutesCombinerService routesCombinerService;
	
	@Autowired
	private LegService legService;
	
	@Autowired
	private LegsCombinerService legsCombinerService;
	
	int month;
	int year;
	
	LocalDateTime requestedDepartureDateTime;
	LocalDateTime requestedArrivalDateTime;
	List<Route> routes = new ArrayList<Route>();
	
	List<Interconnection> interconnections = new ArrayList<Interconnection>();

	public List<Interconnection> findInterconnections(String departure, String arrival,
			LocalDateTime requestedDepartureDateTime, LocalDateTime requestedArrivalDateTime) {
		this.requestedDepartureDateTime = requestedDepartureDateTime;
		this.requestedArrivalDateTime = requestedArrivalDateTime;
		this.year = requestedArrivalDateTime.getYear();
		this.month = requestedDepartureDateTime.getMonthValue();
		this.routes = routeService.findRoutesByConnectingAirportAndOperator();
	
	
		/////direct routes
		List<Route> directRoutes = routeService.findRoutesByDepartureAndArrival(this.routes, departure, arrival);
		Schedule routeSchedule = scheduleService.findSchedule(departure,arrival, this.year, this.month);
		if(directRoutes != null && !directRoutes.isEmpty()) {
			List<Leg> directLegs = legService.createLegList(directRoutes.get(0), routeSchedule, requestedDepartureDateTime, requestedArrivalDateTime, year, month);
			for(Leg leg : directLegs) {
				LocalDateTime legDepartureDateTime = LocalDateTime.parse(leg.getDepartureDateTime());
				LocalDateTime legArrivalDateTime = LocalDateTime.parse(leg.getArrivalDateTime());
					if(legDepartureDateTime.isAfter(requestedDepartureDateTime) && legArrivalDateTime.isBefore(requestedArrivalDateTime)) {
							ArrayList<Leg> legs = new ArrayList<Leg>();
							legs.add(leg);
							Interconnection interconnectedFlight = new Interconnection("0", legs);
							this.interconnections.add(interconnectedFlight);
					}
			}
		}
		
		//////////end direct routes
		
		this.interconnections.addAll(this.findInterconnectedFlights(routes, departure, arrival));
		return interconnections;
		
	}
	
	
	public List<Interconnection> findInterconnectedFlights(List<Route> routes, String departure, String arrivalAirport){
		List<Route> routesByDeparture = routeService.findRoutesByDeparture(routes, departure);
		List<Route> routesByArrival = routeService.findRoutesByArrival(routes, arrivalAirport);
		
		Interconnection interconnection = this.findInterconnectedFlight(routesByDeparture, routesByArrival,  arrivalAirport);
		this.interconnections.add(interconnection);
		
		return this.interconnections;
	}
	
	public Interconnection findInterconnectedFlight(List<Route> routesByDeparture, List<Route> routesByArrival, String arrivalAirport) {
		
		RouteCombo routeCombo = routesCombinerService.combineRoutes(routesByDeparture, routesByArrival, arrivalAirport, year, month, requestedDepartureDateTime, requestedArrivalDateTime );
		Interconnection interconnection = legsCombinerService.combineLegs(routeCombo, requestedArrivalDateTime, requestedArrivalDateTime);

		return interconnection;
	}
	
	
	
	

}
