package com.ryanair.ryanairflights.interconnections;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
	public @ResponseBody List<Route> findAllInterconnections(@RequestParam String departure,
			@RequestParam String arrival,
			@RequestParam("departureDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime,
			@RequestParam("arrivalDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arrivalDateTime) {
		System.out.println(departureDateTime.toString());
		System.out.println(arrivalDateTime.toString());
		System.out.println();
		System.out.println();
		
		List<Route> routes = routeService.findAllRoutes();
		routes = routeService.findRoutesByconnectingAirportAndOperator(routes);
		List<Route> routesByDepartureAndArrival = routeService.findRoutesByDepartureAndArrival(routes, departure, arrival);
		List<Route> routesByDeparture = routeService.findRoutesByDeparture(routes, departure);
		List<Route> routesByArrival = routeService.findRoutesByDeparture(routes, arrival);
		
		
		Schedule schedule = scheduleService.findScheduleNoStops(departure, arrival, departureDateTime.getYear(), departureDateTime.getMonthValue());	
		List<Day> days = Arrays.asList(schedule.getDays());
		
		Day day = days.stream()                        
                .filter(x -> String.valueOf(departureDateTime.getDayOfMonth()).equals(x.getDay()))        
                .findAny()                                     
                .orElse(null); 
		
		Interconnection interconnection = new Interconnection();
		interconnection.setStops("0");
		Leg leg = new Leg();
		leg.setArrivalAirport(arrival);
		List<Flight> flights = null;
		if(day != null) {
			flights = Arrays.asList(day.getFlights());
		}
		else {
			throw new FlightNotFoundException("No flights found");
		}
		
		
		
		for(Flight flight : flights) {
	
			
			LocalTime flightDepartureTime = LocalTime.parse(flight.getDepartureTime());
			System.out.println("flightDepartureTime: " + flightDepartureTime);
			//departureDateTime.get
			
			//LocalDateTime localDateTime = new LocalDateTime(null, flightDepartureTime);
			//if(flightDepartureTime)
			
			//localDateTime.of(departureDateTime.getYear(), departureDateTime.getMonthValue(), departureDateTime.getDayOfMonth(), flight.g, minute);
			
			//if(flight.getDepartureTime())
		}
		
		//departureDateTime.get
		
		
		//leg.setArrivalDateTime(day.getFlights());
		
		
		
		
		
		
		
		return routes;
	}

}
