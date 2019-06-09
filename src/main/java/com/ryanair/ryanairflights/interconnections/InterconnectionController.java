package com.ryanair.ryanairflights.interconnections;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ryanair.ryanairflights.routes.Route;
import com.ryanair.ryanairflights.routes.RouteService;
import com.ryanair.ryanairflights.schedule.Day;
import com.ryanair.ryanairflights.schedule.Flight;
import com.ryanair.ryanairflights.schedule.FlightNotFoundException;
import com.ryanair.ryanairflights.schedule.Schedule;
import com.ryanair.ryanairflights.schedule.ScheduleService;

@RestController
public class InterconnectionController {
	
	@Autowired
	private RouteService routeService;
	
	@Autowired
	private ScheduleService scheduleService;
	
	/*http://localhost:8080/somevalidcontext/interconnections?departure=DUB&arrival=WRO&departureDateTime=2019-07-01T07:00&arrivalDateTime=2019-07-03T21:00*/
		
	@RequestMapping(value = "/somevalidcontext/interconnections", method = RequestMethod.GET)
	public @ResponseBody List<Interconnection> findAllInterconnections(@RequestParam String departure,
			@RequestParam String arrival,
			@RequestParam("departureDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestedDepartureDateTime,
			@RequestParam("arrivalDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestedArrivalDateTime) {

		List<Interconnection> interconnections = new ArrayList<Interconnection>();
		List<Route> routes = routeService.findAllRoutes();
		routes = routeService.findRoutesByconnectingAirportAndOperator(routes);
		List<Route> routesByDepartureAndArrival = routeService.findRoutesByDepartureAndArrival(routes, departure, arrival);
		List<Route> routesByDeparture = routeService.findRoutesByDeparture(routes, departure);
		List<Route> routesByArrival = routeService.findRoutesByDeparture(routes, arrival);
		
		//////////
		for(Route route1 : routesByDeparture) {
			for(Route route2 : routesByArrival) {
				if(route1.getAirportTo().equals(route2.getAirportFrom())) {
					Schedule route1Schedule = scheduleService.findSchedule(route1.getAirportFrom(), route1.getAirportTo(), 
							requestedDepartureDateTime.getYear(), requestedDepartureDateTime.getMonthValue());
					
					Schedule route2Schedule = scheduleService.findSchedule(route2.getAirportFrom(), route2.getAirportTo(), 
							requestedDepartureDateTime.getYear(), requestedDepartureDateTime.getMonthValue());
					

				}
			}
		}
		
		
		
		///////////////////
		Schedule schedule = scheduleService.findScheduleNoStops(departure, arrival, requestedDepartureDateTime.getYear(), requestedDepartureDateTime.getMonthValue());	
		List<Day> days = Arrays.asList(schedule.getDays());
		
		Day day = days.stream()                        
                .filter(x -> String.valueOf(requestedDepartureDateTime.getDayOfMonth()).equals(x.getDay()))        
                .findAny()                                     
                .orElse(null); 
		
		List<Flight> flights = null;
		if(day != null) {
			flights = Arrays.asList(day.getFlights());
		}
		else {
			throw new FlightNotFoundException("No flights found");
		}
		
		
		
		for(Flight flight : flights) {
	
			
			LocalTime flightDepartureTime = LocalTime.parse(flight.getDepartureTime());
			LocalTime flightArrivalTime = LocalTime.parse(flight.getArrivalTime());
			
			
			if(flightDepartureTime.isAfter(requestedDepartureDateTime.toLocalTime())) {
				if(flightArrivalTime.isBefore(requestedArrivalDateTime.toLocalTime())) {
					
					
					LocalDateTime flightDepartureDateTime = LocalDateTime.of(requestedDepartureDateTime.getYear(), requestedDepartureDateTime.getMonth(), 
							Integer.valueOf(day.getDay()), 
							flightDepartureTime.getHour(), flightDepartureTime.getMinute());
					
					LocalDateTime flightArrivalDateTime = LocalDateTime.of(requestedArrivalDateTime.getYear(), requestedArrivalDateTime.getMonth(), 
							Integer.valueOf(day.getDay()), 
							flightArrivalTime.getHour(), flightArrivalTime.getMinute());
					
					Interconnection interconnection = new Interconnection();
					interconnection.setStops("0");
					Leg leg = new Leg();
					leg.setDepartureAirport(departure);
					leg.setArrivalAirport(arrival);
					leg.setDepartureDateTime(flightDepartureDateTime.toString());
					leg.setArrivalDateTime(flightArrivalDateTime.toString());
					ArrayList<Leg> legs = new ArrayList<Leg>(); 
					legs.add(leg);
					interconnection.setLegs(legs);
					interconnections.add(interconnection);
				}
			}
		}
		return interconnections;
	}

}
