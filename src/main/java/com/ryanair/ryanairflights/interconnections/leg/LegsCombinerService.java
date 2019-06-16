package com.ryanair.ryanairflights.interconnections.leg;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ryanair.ryanairflights.interconnections.Interconnection;
import com.ryanair.ryanairflights.interconnections.routecombo.RouteCombo;

@Service
public class LegsCombinerService {

	

	

	public Interconnection combineLegs(RouteCombo routeCombo, LocalDateTime requestedDepartureDateTime, LocalDateTime  requestedArrivalDateTime) {
		Interconnection interconnection = null;
		for(Leg firstLeg : routeCombo.getPossibleFirstLegs()) {
			for(Leg secondLeg : routeCombo.getPossibleSecondLegs()) {
				interconnection = this.verifyTimeDifferenceBetweenLegs(firstLeg, secondLeg, requestedDepartureDateTime,requestedArrivalDateTime);
			}
		}
		return interconnection;
	}
	
	public Interconnection verifyTimeDifferenceBetweenLegs(Leg firstLeg, Leg secondLeg, LocalDateTime requestedDepartureDateTime, LocalDateTime  requestedArrivalDateTime) {
		LocalDateTime firstLegDepartureDateTime = LocalDateTime.parse(firstLeg.getDepartureDateTime());
		LocalDateTime firstLegArrivalDateTime = LocalDateTime.parse(firstLeg.getArrivalDateTime());
		LocalDateTime secondLegDepartureDateTime = LocalDateTime.parse(secondLeg.getDepartureDateTime());
		LocalDateTime secondLegArrivalDateTime = LocalDateTime.parse(secondLeg.getArrivalDateTime());
		Interconnection interconnection = null;
		if(firstLegDepartureDateTime.isBefore(secondLegArrivalDateTime)) {
			if(firstLegDepartureDateTime.isAfter(requestedDepartureDateTime) && secondLegArrivalDateTime.isBefore(requestedArrivalDateTime)) {
				long hours = ChronoUnit.HOURS.between(firstLegArrivalDateTime, secondLegDepartureDateTime);
				if (hours >= 2) {
					List<Leg> legs = new ArrayList<Leg>();
					legs.add(firstLeg);
					legs.add(secondLeg);
					interconnection = new Interconnection("1", legs);
				}
			}
		}
		return interconnection;
	}

}
