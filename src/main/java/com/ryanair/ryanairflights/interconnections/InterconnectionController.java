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
import com.ryanair.ryanairflights.routes.RouteRepository;
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
	
	private final RouteRepository repository;

	InterconnectionController(RouteRepository repository) {
	    this.repository = repository;
	  }
	
	int month;
	int year;
	LocalDateTime requestedDepartureDateTime;
	LocalDateTime requestedArrivalDateTime;
	
	
	/*http://localhost:8080/somevalidcontext/interconnections?departure=DUB&arrival=WRO&departureDateTime=2019-07-01T07:00&arrivalDateTime=2019-07-03T21:00*/
		
	@RequestMapping(value = "/somevalidcontext/interconnections", method = RequestMethod.GET)
	public @ResponseBody List<Interconnection> findAllInterconnections(@RequestParam String departure,
			@RequestParam String arrival,
			@RequestParam("departureDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestedDepartureDateTime,
			@RequestParam("arrivalDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestedArrivalDateTime) {

		this.requestedDepartureDateTime = requestedDepartureDateTime;
		this.requestedArrivalDateTime = requestedArrivalDateTime;
		year = requestedArrivalDateTime.getYear();

		if (requestedDepartureDateTime.getMonthValue() == requestedArrivalDateTime.getMonthValue()) {
			month = requestedDepartureDateTime.getMonthValue();
		}

		List<Interconnection> interconnections = new ArrayList<Interconnection>();
		repository.findAllByAirportFrom(departure);
		
	
		List<Route> routesByDeparture = repository.findAllByAirportFrom(departure);
		List<Route> routesByArrival = repository.findAllByAirportTo(arrival);
	
		/////direct routes
		List<Route> directRoutes = repository.findAllByAirportFromAndAirportTo(departure, arrival);
		Schedule routeSchedule = scheduleService.findSchedule(departure,arrival, year, month);
		if(directRoutes != null && !directRoutes.isEmpty()) {
			List<Leg> directLegs = createLegList(directRoutes.get(0), routeSchedule, requestedDepartureDateTime,
					requestedArrivalDateTime);
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
		
		Schedule firstLegRouteSchedule = scheduleService.findSchedule(firstLegRoute.getAirportFrom(),firstLegRoute.getAirportTo(), year, month);
		
		Schedule secondLegRouteSchedule = scheduleService.findSchedule(secondLegRoute.getAirportFrom(),secondLegRoute.getAirportTo(), year, month);

		List<Leg> possibleFirstLegs = createLegList(firstLegRoute, firstLegRouteSchedule, requestedDepartureDateTime,
				requestedArrivalDateTime);
		List<Leg> possibleSecondLegs = createLegList(secondLegRoute, secondLegRouteSchedule, requestedDepartureDateTime,
				requestedArrivalDateTime);
		
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
					ArrayList<Leg> legs = new ArrayList<Leg>();
					legs.add(firstLeg);
					legs.add(secondLeg);

					Interconnection interconnectedFlight = new Interconnection();
					interconnectedFlight.setLegs(legs);
					interconnectedFlight.setStops("1");
					interconnections.add(interconnectedFlight);
				}
			}
		}
	}


	private List<Leg> createLegList(Route route, Schedule schedule, LocalDateTime requestedDepartureDateTime,
			LocalDateTime requestedArrivalDateTime) {
		ArrayList<Leg> legList = new ArrayList<Leg>();
		if(schedule != null) {
			verifyDayFlights(route, schedule, requestedDepartureDateTime, legList);
		}
		
		return legList;
	}


	private void verifyDayFlights(Route route, Schedule schedule, LocalDateTime requestedDepartureDateTime,
			ArrayList<Leg> legList) {
		for (int i = this.requestedDepartureDateTime.getDayOfMonth(); i <= this.requestedArrivalDateTime
				.getDayOfMonth(); i++) {
			Day day = findDayinSchedule(schedule, i);
			List<Flight> flights = new ArrayList<Flight>(Arrays.asList(day.getFlights()));
			for (Flight flight : flights) {
				LocalTime flightDepartureTime = LocalTime.parse(flight.getDepartureTime());
				LocalTime flightArrivalTime = LocalTime.parse(flight.getArrivalTime());
				if (flightDepartureTime.isAfter(requestedDepartureDateTime.toLocalTime())) {
					LocalDateTime flightDepartureDateTime = createLocalDateTime(day, flightDepartureTime);
					LocalDateTime flightArrivalDateTime = createLocalDateTime(day, flightArrivalTime);
					Leg leg = new Leg(route.getAirportFrom(), route.getAirportTo(), flightDepartureDateTime.toString(),
							flightArrivalDateTime.toString());
					legList.add(leg);
				}
			}
		}
	}


	private LocalDateTime createLocalDateTime(Day day, LocalTime localTime) {
		return LocalDateTime.of(year, month, Integer.valueOf(day.getDay()),
				localTime.getHour(), localTime.getMinute());
	}
	
	
	public static boolean between(int i, int minValueInclusive, int maxValueInclusive) {
	    return (i >= minValueInclusive && i <= maxValueInclusive);
	}
	

	public Day findDayinSchedule(Schedule schedule, int requestedDay) {
		List<Day> days = Arrays.asList(schedule.getDays());
		Day day = days.stream()
				.filter(x -> String.valueOf(requestedDay).equals(x.getDay())).findAny()
				.orElse(null);
		return day;
	}
	


}
