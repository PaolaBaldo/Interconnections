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
	
	int month;
	int year;
	
	
	/*http://localhost:8080/somevalidcontext/interconnections?departure=DUB&arrival=WRO&departureDateTime=2019-07-01T07:00&arrivalDateTime=2019-07-03T21:00*/
		
	@RequestMapping(value = "/somevalidcontext/interconnections", method = RequestMethod.GET)
	public @ResponseBody List<Interconnection> findAllInterconnections(@RequestParam String departure,
			@RequestParam String arrival,
			@RequestParam("departureDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestedDepartureDateTime,
			@RequestParam("arrivalDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestedArrivalDateTime) {
		
		year = requestedArrivalDateTime.getYear();
		
		if(requestedDepartureDateTime.getMonthValue()==requestedArrivalDateTime.getMonthValue()) {
			month = requestedDepartureDateTime.getMonthValue();
		}

		List<Interconnection> interconnections = new ArrayList<Interconnection>();
		List<Route> routes = routeService.findAllRoutes();
		routes = routeService.findRoutesByconnectingAirportAndOperator(routes);
		List<Route> routesByDepartureAndArrival = routeService.findRoutesByDepartureAndArrival(routes, departure, arrival);
		List<Route> routesByDeparture = routeService.findRoutesByDeparture(routes, departure);
		List<Route> routesByArrival = routeService.findRoutesByArrival(routes, arrival);
		List<Route> interconnecteds = new ArrayList();
		List<Interconnection> interconnections_oneStop = new ArrayList();
		//////////
		for(Route route1 : routesByDeparture) {
			for(Route route2 : routesByArrival) {
				if(route1.getAirportTo().equals(route2.getAirportFrom()) && !route1.getAirportTo().equals(arrival)) {
					Interconnection interconnection = new Interconnection();
					Leg leg1 = this.createLeg(route1.getAirportFrom(), route1.getAirportTo(), requestedDepartureDateTime, requestedArrivalDateTime);
					Leg leg2 = this.createLeg(route2.getAirportFrom(), route2.getAirportTo(), requestedDepartureDateTime, requestedArrivalDateTime);
					
					
					//Leg leg2 = new Leg();
					leg2.setDepartureAirport(route2.getAirportFrom());
					leg2.setArrivalAirport(route2.getAirportTo());
					ArrayList<Leg> legs = new ArrayList();
					leg1 = setLegDateTimes(leg1, requestedDepartureDateTime, requestedArrivalDateTime);
					leg2 = setLegDateTimes(leg1, requestedDepartureDateTime, requestedArrivalDateTime);
					legs.add(leg1);
					legs.add(leg2);
					interconnection.setLegs(legs);
					interconnection.setStops("1");
					interconnections.add(interconnection);
					//interconnections_oneStop.add(interconnection);
				}
			}
		}
		
		
		
		
		/////////////////// no  stop
		Schedule schedule = scheduleService.findScheduleNoStops(departure, arrival, requestedDepartureDateTime.getYear(), requestedDepartureDateTime.getMonthValue());
		List<Flight> flights = findFlightsBetweenDates(schedule, requestedArrivalDateTime, requestedArrivalDateTime);
			
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
	}/////0 stop
	
	
	private Leg createLeg(String airportFrom, String airportTo, LocalDateTime requestedDepartureDateTime, LocalDateTime requestedArrivalDateTime) {
		Leg leg = new Leg();
		leg.setDepartureAirport(airportFrom);
		leg.setArrivalAirport(airportTo);
		Schedule schedule = scheduleService.findSchedule(leg.getDepartureAirport(), leg.getArrivalAirport(), year, month);
		List<Flight> flightList = findFlightsBetweenDates(schedule, requestedDepartureDateTime, requestedArrivalDateTime);
		for(Flight flight : flightList) {
			Leg leg = new Leg();
			leg.setDepartureAirport(airportFrom);
			leg.setArrivalAirport(airportTo);
			requestedDepartureDateTime.se
			leg.setDepartureDateTime(departureDateTime);
			
		}
		return null;
	}


	public Leg setLegDateTimes (Leg leg,  LocalDateTime requestedDepartureDateTime, LocalDateTime requestedArrivalDateTime) {
		Schedule schedule = scheduleService.findSchedule(leg.getDepartureAirport(), leg.getArrivalAirport(), requestedDepartureDateTime.getYear(), requestedDepartureDateTime.getMonthValue());
		List<Flight> flightsList = findFlightsBetweenDates(schedule, requestedDepartureDateTime, requestedArrivalDateTime);
		
	

		/*LocalTime flightDepartureTime = LocalTime.parse(flightsList.get(0).getDepartureTime());
		LocalTime flightArrivalTime = LocalTime.parse(flightsList.get(0).getArrivalTime());

		LocalDateTime flightDepartureDateTime = LocalDateTime.of(requestedDepartureDateTime.getYear(), requestedDepartureDateTime.getMonth(), 
				Integer.valueOf(day.getDay()), 
				flightDepartureTime.getHour(), flightDepartureTime.getMinute());

		LocalDateTime flightArrivalDateTime = LocalDateTime.of(requestedDepartureDateTime.getYear(), requestedDepartureDateTime.getMonth(), 
				Integer.valueOf(day.getDay()), 
				flightArrivalTime.getHour(), flightArrivalTime.getMinute());

		leg.setArrivalDateTime(flightArrivalDateTime.toString());
		leg.setDepartureDateTime(flightDepartureDateTime.toString());*/
		return leg;
	}
	
	/*private void verifySecondLeg(Route route2, LocalDateTime requestedDepartureDateTime) {
		Schedule route2Schedule = scheduleService.findSchedule(route2.getAirportFrom(), route2.getAirportTo(), 
				requestedDepartureDateTime.getYear(), requestedDepartureDateTime.getMonthValue());
		
		Day day = findDayinSchedule(route2Schedule, requestedDepartureDateTime);
		List<Flight> flights = verifyFlights(day);
		for(Flight flight : flights) {
			LocalTime flightDepartureTime = LocalTime.parse(flight.getDepartureTime());
			if(flightDepartureTime.isAfter(requestedDepartureDateTime.toLocalTime())) {
				this.verifySecondLeg(route2, requestedDepartureDateTime);
				
			}
			LocalTime flightArrivalTime = LocalTime.parse(flight.getArrivalTime());
		}
		
		
		
	}*/


	private List<Flight> findFlightsBetweenDates(Schedule schedule, LocalDateTime requestedDepartureDateTime, LocalDateTime requestedArrivalDateTime) {
		List<Flight> flights = new ArrayList();
		for(int day=requestedDepartureDateTime.getDayOfMonth(); day<=requestedArrivalDateTime.getDayOfMonth(); day++) {
			Day foundDay = findDayinSchedule(schedule, day);
			new ArrayList<Flight>(Arrays.asList(foundDay.getFlights()));
			flights = new ArrayList<Flight>(Arrays.asList(foundDay.getFlights()));
		}
		return flights;
	}


	public Day findDayinSchedule(Schedule schedule, int requestedDay) {
		List<Day> days = Arrays.asList(schedule.getDays());
		Day day = days.stream()
				.filter(x -> String.valueOf(requestedDay).equals(x.getDay())).findAny()
				.orElse(null);
		return day;
	}
	
	public List<Flight> verifyFlights(Day day) {
		List<Flight> flightsList = null;
		if(day != null) {
			flightsList = Arrays.asList(day.getFlights());
		}
		else {
			throw new FlightNotFoundException("No flights found");
		}
		return flightsList;
	}

}
