package com.ryanair.ryanairflights.routes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;



@Service
public class RouteService {
	
	private static final String RYANAIR_ROUTES_API = "https://services-api.ryanair.com/locate/3/routes";
	RestTemplate restTemplate = new RestTemplate();
	
	public List<Route> callRoutesAPI() {
		ResponseEntity<Route[]> responseEntity = restTemplate.getForEntity(RYANAIR_ROUTES_API, Route[].class);
		Route[] list = responseEntity.getBody();
		return Arrays.asList(list);
	}
	

	public List<Route> findRoutesByconnectingAirportAndOperator(List<Route> routes) {
		return routes.stream()
                .filter(d -> d.getOperator().equals("RYANAIR") && d.getConnectingAirport() == null)
                .collect(Collectors.toList());
	}

	public List<Route> findRoutesByDeparture(List<Route> routes, String departure) {
		return routes.stream()
                .filter(d -> d.getAirportFrom().equals(departure))
                .collect(Collectors.toList());
	}
	
	public List<Route> findRoutesByArrival(List<Route> routes, String arrival) {
		return routes.stream()
                .filter(d -> d.getAirportTo().equals(arrival))
                .collect(Collectors.toList());
	}

	public List<Route> findRoutesByDepartureAndArrival(List<Route> routes, String departure, String arrival) {
		return routes.stream()
                .filter(d -> d.getAirportFrom().equals(departure) && d.getAirportTo().equals(arrival))
                .collect(Collectors.toList());
	}

}
