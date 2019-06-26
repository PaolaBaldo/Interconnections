package com.ryanair.ryanairflights.interconnections.services;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ryanair.ryanairflights.interconnections.Leg;
import com.ryanair.ryanairflights.interconnections.RequestedFlight;
import com.ryanair.ryanairflights.routes.Route;
import com.ryanair.ryanairflights.schedule.Day;
import com.ryanair.ryanairflights.schedule.Flight;
import com.ryanair.ryanairflights.schedule.Schedule;

@Service
public class LegService {
	

	public List<Leg> createLegList(Route route, Schedule schedule, RequestedFlight requestedFlight) {
		ArrayList<Leg> legList = new ArrayList<Leg>();
		if(schedule != null) {
			legList = verifyDayFlights(route, schedule, requestedFlight.getRequestedDepartureDateTime(), legList, requestedFlight);
		}
		return legList;
	}
	
	private ArrayList<Leg> verifyDayFlights(Route route, Schedule schedule, LocalDateTime requestedDepartureDateTime,
			ArrayList<Leg> legList, RequestedFlight requestedFlight) {
		for (int i = requestedFlight.getRequestedDepartureDateTime().getDayOfMonth(); i <= requestedFlight.getRequestedArrivalDateTime()
				.getDayOfMonth(); i++) {
			Day day = findDayinSchedule(schedule, i);
			if(day != null)
			{
				legList = processDayFlights(route, legList, day, requestedFlight);
			}		
		}
		return legList;
	}
	
	
	private ArrayList<Leg> processDayFlights(Route route, ArrayList<Leg> legList, Day day, RequestedFlight requestedFlight) {
		List<Flight> flights = new ArrayList<Flight>(Arrays.asList(day.getFlights()));
		for (Flight flight : flights) {
			LocalTime flightDepartureTime = LocalTime.parse(flight.getDepartureTime());
			LocalTime flightArrivalTime = LocalTime.parse(flight.getArrivalTime());
			LocalDateTime flightDepartureDateTime = createLocalDateTime(day, flightDepartureTime, requestedFlight);
			LocalDateTime flightArrivalDateTime = createLocalDateTime(day, flightArrivalTime, requestedFlight);
			Leg leg = new Leg(route.getAirportFrom(), route.getAirportTo(), flightDepartureDateTime.toString(),
			flightArrivalDateTime.toString());
			legList.add(leg);
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
	
	private LocalDateTime createLocalDateTime(Day day, LocalTime localTime, RequestedFlight requestedFlight) {
		return LocalDateTime.of(requestedFlight.getYear(), requestedFlight.getMonth(), Integer.valueOf(day.getDay()),
				localTime.getHour(), localTime.getMinute());
	}

}
