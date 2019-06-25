package com.ryanair.ryanairflights.interconnections.leg;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ryanair.ryanairflights.Utils;
import com.ryanair.ryanairflights.interconnections.Interconnection;
import com.ryanair.ryanairflights.routes.Route;
import com.ryanair.ryanairflights.schedule.Day;
import com.ryanair.ryanairflights.schedule.Flight;
import com.ryanair.ryanairflights.schedule.Schedule;

@Service
public class LegService {

	



	public List<Leg> createLegList(Route route, Schedule schedule, LocalDateTime requestedDepartureDateTime, LocalDateTime requestedArrivalDateTime, int year, int month) {
		ArrayList<Leg> legList = new ArrayList<Leg>();
		if(schedule != null) {
			for(Day day : schedule.getDays()) {
				if(Utils.between(Integer.parseInt(day.getDay()), requestedDepartureDateTime.getDayOfMonth(), requestedArrivalDateTime.getDayOfMonth())) {
					List<Flight> flights = new ArrayList<Flight>(Arrays.asList(day.getFlights()));
					validateFlightSchedule(route, requestedDepartureDateTime, requestedArrivalDateTime, year, month,
							legList, day, flights);
				}
			}
		}
		return legList;
	}


	


	private void validateFlightSchedule(Route route, LocalDateTime requestedDepartureDateTime,
			LocalDateTime requestedArrivalDateTime, int year, int month, ArrayList<Leg> legList, Day day, List<Flight> flights) {
		for(Flight flight : flights) {
			LocalTime flightDepartureTime = LocalTime.parse(flight.getDepartureTime());
			LocalTime flightArrivalTime = LocalTime.parse(flight.getArrivalTime());
			if (flightDepartureTime.isAfter(requestedDepartureDateTime.toLocalTime()) && flightArrivalTime.isBefore(requestedArrivalDateTime.toLocalTime())) {
				Leg leg = this.createLeg(flightDepartureTime, flightArrivalTime, route, day, year,  month);
				legList.add(leg);
			}
		}
	}
	

	public Leg createLeg(LocalTime flightDepartureTime, LocalTime flightArrivalTime, Route route, Day day, int year, int month) {
		LocalDateTime flightDepartureDateTime = LocalDateTime.of(year, month, Integer.valueOf(day.getDay()),
				flightDepartureTime.getHour(), flightDepartureTime.getMinute());
		LocalDateTime flightArrivalDateTime = LocalDateTime.of(year, month, Integer.valueOf(day.getDay()),
				flightArrivalTime.getHour(), flightArrivalTime.getMinute());
		Leg leg = new Leg(route.getAirportFrom(), route.getAirportTo(), flightDepartureDateTime.toString(),
				flightArrivalDateTime.toString());
		return leg;
	}
	
	
	

}
