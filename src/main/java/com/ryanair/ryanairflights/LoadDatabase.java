package com.ryanair.ryanairflights;


import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ryanair.ryanairflights.routes.Route;
import com.ryanair.ryanairflights.routes.RouteRepository;
import com.ryanair.ryanairflights.routes.RouteService;

@Configuration
@Slf4j
class LoadDatabase {

	@Autowired
	RouteService routeService;

	@Bean
	CommandLineRunner initDatabase(RouteRepository repository) {
		return args -> {
			List<Route> routeList = routeService.callRoutesAPI();
			routeList = routeService.findRoutesByconnectingAirportAndOperator(routeList);
			repository.saveAll(routeList);
		};
	}
}
