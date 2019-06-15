package com.ryanair.ryanairflights.interconnections;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


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

		if (requestedDepartureDateTime.getMonthValue() == requestedArrivalDateTime.getMonthValue()) {
			month = requestedDepartureDateTime.getMonthValue();
		}

		List<Interconnection> interconnections = new ArrayList<Interconnection>();
		List<Route> routes = routeService.findAllRoutes();
		routes = routeService.findRoutesByconnectingAirportAndOperator(routes);
	
		List<Route> routesByDeparture = routeService.findRoutesByDeparture(routes, departure);
		List<Route> routesByArrival = routeService.findRoutesByArrival(routes, arrival);
	
		/////direct routes
		List<Route> directRoutes = routeService.findRoutesByDepartureAndArrival(routes, departure, arrival);
		Schedule routeSchedule = scheduleService.findSchedule(departure,arrival, year, month);

		List<Leg> directLegs = createLegList(directRoutes.get(0), routeSchedule, requestedDepartureDateTime,
				requestedArrivalDateTime);
		for(Leg leg : directLegs) {
			LocalDateTime legDepartureDateTime = LocalDateTime.parse(leg.getDepartureDateTime());
			LocalDateTime legArrivalDateTime = LocalDateTime.parse(leg.getArrivalDateTime());
				if(legDepartureDateTime.isAfter(requestedDepartureDateTime) && legArrivalDateTime.isBefore(requestedArrivalDateTime)) {
						ArrayList<Leg> legs = new ArrayList<Leg>();
						legs.add(leg);
						Interconnection interconnectedFlight = new Interconnection();
						interconnectedFlight.setLegs(legs);
						interconnectedFlight.setStops("0");
						interconnections.add(interconnectedFlight);
				}
		}
		
		
	
		//////////end direct routes
		
		for (Route route1 : routesByDeparture) {
			for (Route route2 : routesByArrival) {
				if (route1.getAirportTo().equals(route2.getAirportFrom()) && !route1.getAirportTo().equals(arrival)) {
				
					Schedule firtsRouteSchedule = scheduleService.findSchedule(route1.getAirportFrom(),route1.getAirportTo(), year, month);
					
					Schedule secondRouteSchedule = scheduleService.findSchedule(route2.getAirportFrom(),route2.getAirportTo(), year, month);

					List<Leg> leg1List = createLegList(route1, firtsRouteSchedule, requestedDepartureDateTime,
							requestedArrivalDateTime);
					List<Leg> leg2List = createLegList(route2, secondRouteSchedule, requestedDepartureDateTime,
							requestedArrivalDateTime);
					
					for(Leg leg1 : leg1List) {
						for(Leg leg2 : leg2List) {
							
							LocalDateTime leg1DepartureDateTime = LocalDateTime.parse(leg1.getDepartureDateTime());
							LocalDateTime leg1ArrivalDateTime = LocalDateTime.parse(leg1.getArrivalDateTime());
							
							LocalDateTime leg2DepartureDateTime = LocalDateTime.parse(leg2.getDepartureDateTime());
							LocalDateTime leg2ArrivalDateTime = LocalDateTime.parse(leg2.getArrivalDateTime());
							
							if(leg1DepartureDateTime.isBefore(leg2ArrivalDateTime)) {
								if(leg1DepartureDateTime.isAfter(requestedDepartureDateTime) && leg2ArrivalDateTime.isBefore(requestedArrivalDateTime)) {
									long hours = ChronoUnit.HOURS.between(leg1ArrivalDateTime, leg2DepartureDateTime);
									if (hours >= 2) {
										ArrayList<Leg> legs = new ArrayList<Leg>();
										legs.add(leg1);
										legs.add(leg2);

										Interconnection interconnectedFlight = new Interconnection();
										interconnectedFlight.setLegs(legs);
										interconnectedFlight.setStops("1");
										interconnections.add(interconnectedFlight);
									}
								}
							}
						}
					}
				}
			}
		}

		/////////////////// no stop

		/*Schedule schedule = scheduleService.findScheduleNoStops(departure, arrival,
				requestedDepartureDateTime.getYear(), requestedDepartureDateTime.getMonthValue());
		List<Flight> flights = findFlightsBetweenDates(schedule, requestedArrivalDateTime, requestedArrivalDateTime);

		for (Flight flight : flights) {

			LocalTime flightDepartureTime = LocalTime.parse(flight.getDepartureTime());
			LocalTime flightArrivalTime = LocalTime.parse(flight.getArrivalTime());

			if (flightDepartureTime.isAfter(requestedDepartureDateTime.toLocalTime())) {
				if (flightArrivalTime.isBefore(requestedArrivalDateTime.toLocalTime())) {

					LocalDateTime flightDepartureDateTime = LocalDateTime.of(requestedDepartureDateTime.getYear(),
							requestedDepartureDateTime.getMonth(), Integer.valueOf(day.getDay()),
							flightDepartureTime.getHour(), flightDepartureTime.getMinute());

					LocalDateTime flightArrivalDateTime = LocalDateTime.of(requestedArrivalDateTime.getYear(),
							requestedArrivalDateTime.getMonth(), Integer.valueOf(day.getDay()),
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
		}*/

		return interconnections;
	}///// 0 stop


	private List<Leg> createLegList(Route route, Schedule schedule, LocalDateTime requestedDepartureDateTime,
			LocalDateTime requestedArrivalDateTime) {
		ArrayList<Leg> legList = new ArrayList<Leg>();
		if(schedule != null) {
			for (Day day : schedule.getDays()) {

				List<Flight> flights = new ArrayList<Flight>(Arrays.asList(day.getFlights()));
				for(Flight flight : flights) {
					LocalTime flightDepartureTime = LocalTime.parse(flight.getDepartureTime());
					LocalTime flightArrivalTime = LocalTime.parse(flight.getArrivalTime());
					if (flightDepartureTime.isAfter(requestedDepartureDateTime.toLocalTime())) {
						LocalDateTime flightDepartureDateTime = LocalDateTime.of(year, month, Integer.valueOf(day.getDay()),
								flightDepartureTime.getHour(), flightDepartureTime.getMinute());
						LocalDateTime flightArrivalDateTime = LocalDateTime.of(year, month, Integer.valueOf(day.getDay()),
								flightArrivalTime.getHour(), flightArrivalTime.getMinute());
						Leg leg = new Leg(route.getAirportFrom(), route.getAirportTo(), flightDepartureDateTime.toString(),
								flightArrivalDateTime.toString());
						legList.add(leg);
					}
				}
			}
			
		}
		
		return legList;
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
