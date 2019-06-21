package com.ryanair.ryanairflights;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ryanair.ryanairflights.routes.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {
	
	List<Route> findAllByAirportFrom(String airPortFrom);
	


}
