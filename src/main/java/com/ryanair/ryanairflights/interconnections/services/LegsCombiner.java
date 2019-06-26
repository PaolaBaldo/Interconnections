package com.ryanair.ryanairflights.interconnections.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ryanair.ryanairflights.interconnections.Interconnection;
import com.ryanair.ryanairflights.interconnections.Leg;

@Service
public class LegsCombiner {
	
	@Autowired
	TimeBetweenLegsValidator timeBetweenLegsValidator;
	
	void combineLegs(LocalDateTime requestedDepartureDateTime, LocalDateTime requestedArrivalDateTime,
			List<Interconnection> interconnections, List<Leg> possibleFirstLegs, List<Leg> possibleSecondLegs) {
		for(Leg firstLeg : possibleFirstLegs) {
			for(Leg secondLeg : possibleSecondLegs) {
				
				LocalDateTime firstLegDepartureDateTime = LocalDateTime.parse(firstLeg.getDepartureDateTime());
				LocalDateTime firstLegArrivalDateTime = LocalDateTime.parse(firstLeg.getArrivalDateTime());
				
				LocalDateTime secondLegDepartureDateTime = LocalDateTime.parse(secondLeg.getDepartureDateTime());
				LocalDateTime secondLegArrivalDateTime = LocalDateTime.parse(secondLeg.getArrivalDateTime());
				
				timeBetweenLegsValidator.validateTimeBetweenLegs(requestedDepartureDateTime, requestedArrivalDateTime, interconnections,
						firstLeg, secondLeg, firstLegDepartureDateTime, firstLegArrivalDateTime,
						secondLegDepartureDateTime, secondLegArrivalDateTime);
			}
		}
	}

}
