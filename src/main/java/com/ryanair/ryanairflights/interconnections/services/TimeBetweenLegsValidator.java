package com.ryanair.ryanairflights.interconnections.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.ryanair.ryanairflights.interconnections.Interconnection;
import com.ryanair.ryanairflights.interconnections.Leg;

public class TimeBetweenLegsValidator {
	
	@Autowired
	private InterconnectionService interconnectionService;
	
	
	Boolean validateTimeBetweenLegs(LocalDateTime requestedDepartureDateTime,
			LocalDateTime requestedArrivalDateTime, List<Interconnection> interconnections, Leg firstLeg, Leg secondLeg,
			LocalDateTime firstLegDepartureDateTime, LocalDateTime firstLegArrivalDateTime,
			LocalDateTime secondLegDepartureDateTime, LocalDateTime secondLegArrivalDateTime) {
		
		if(firstLegDepartureDateTime.isBefore(secondLegArrivalDateTime)) {
			if(firstLegDepartureDateTime.isAfter(requestedDepartureDateTime) && secondLegArrivalDateTime.isBefore(requestedArrivalDateTime)) {
				long hours = ChronoUnit.HOURS.between(firstLegArrivalDateTime, secondLegDepartureDateTime);
				if (hours >= 2) {
					interconnectionService.createInterconnetion(interconnections, firstLeg, secondLeg);
					return true;
				}
			}
		}
		return false;
	}

}
