package com.ryanair.ryanairflights;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ryanair.ryanairflights.routes.Route;
import com.ryanair.ryanairflights.routes.RouteService;

@Configuration
@Slf4j
class LoadDatabase {
	
	@Autowired
	RouteService routeService;

  @Bean
  CommandLineRunner initDatabase(RouteRepository repository) {
    return args -> {
    	List<Route> stateList = routeService.findAllRoutes();
    	repository.saveAll(stateList);
    	List<Route> list2 = repository.findAllByAirportFrom("DUB");
    	System.out.println("Routes list saved successfully");
    };
  }
}
