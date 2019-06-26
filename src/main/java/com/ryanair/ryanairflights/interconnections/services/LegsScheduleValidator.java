package com.ryanair.ryanairflights.interconnections.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ryanair.ryanairflights.interconnections.Interconnection;
import com.ryanair.ryanairflights.interconnections.Leg;

@Service
public class LegsScheduleValidator {
	
	Boolean validateLegSchedule(LocalDateTime requestedDepartureDateTime, LocalDateTime requestedArrivalDateTime,
			List<Interconnection> interconnections, Leg leg) {
		LocalDateTime legDepartureDateTime = LocalDateTime.parse(leg.getDepartureDateTime());
		LocalDateTime legArrivalDateTime = LocalDateTime.parse(leg.getArrivalDateTime());
			if(legDepartureDateTime.isAfter(requestedDepartureDateTime) && legArrivalDateTime.isBefore(requestedArrivalDateTime)) {
					return true;
			}
			return false;
	}

}
