package com.ryanair.ryanairflights.interconnections.routecombo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ryanair.ryanairflights.interconnections.Interconnection;
import com.ryanair.ryanairflights.interconnections.leg.Leg;
import com.ryanair.ryanairflights.interconnections.leg.LegService;
import com.ryanair.ryanairflights.interconnections.leg.LegsCombinerService;
import com.ryanair.ryanairflights.routes.Route;
import com.ryanair.ryanairflights.schedule.Schedule;
import com.ryanair.ryanairflights.schedule.ScheduleService;


@Service
public class RoutesCombinerService {
	
	@Autowired
	ScheduleService scheduleService;
	
	@Autowired
	LegsCombinerService legsCombinerService;
	
	@Autowired
	LegService legService;
	
	
	
	public List<RouteCombo> combineRoutes(List<Route> routesByDeparture, List<Route> routesByArrival, String arrivalAirport, int year, int month, LocalDateTime requestedDepartureDateTime, LocalDateTime requestedArrivalDateTime){
		RouteCombo routeCombo = null;
		List<RouteCombo> routeComboList = new ArrayList<RouteCombo>();
		for (Route firstRoute : routesByDeparture) {
			for (Route secondRoute : routesByArrival) {
				if (firstRoute.getAirportTo().equals(secondRoute.getAirportFrom()) && !firstRoute.getAirportTo().equals(arrivalAirport)) {
					Schedule firtsRouteSchedule = scheduleService.findSchedule(firstRoute.getAirportFrom(),firstRoute.getAirportTo(), year, month);
					Schedule secondRouteSchedule = scheduleService.findSchedule(secondRoute.getAirportFrom(),secondRoute.getAirportTo(), year, month);
					List<Leg> firstLegList = legService.createLegList(firstRoute, firtsRouteSchedule,   requestedDepartureDateTime,  requestedArrivalDateTime, year, month);
					List<Leg> secondLegList = legService.createLegList(secondRoute, secondRouteSchedule,   requestedDepartureDateTime,  requestedArrivalDateTime, year, month);
					routeCombo = new RouteCombo(firstLegList, secondLegList);
					routeComboList.add(routeCombo);
				}
			}
		}
		return routeComboList;
	}

}
