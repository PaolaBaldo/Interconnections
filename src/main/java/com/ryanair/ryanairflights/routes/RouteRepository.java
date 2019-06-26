package com.ryanair.ryanairflights.routes;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RouteRepository extends JpaRepository<Route, Long> {
	
	
	@Query("select r from Route r where r.airportFrom = :airportFrom")
	List<Route> findAllByAirportFrom(@Param("airportFrom") String airportFrom);

	@Query("select r from Route r where r.airportTo = :airportTo")
	List<Route> findAllByAirportTo( @Param("airportTo") String airportTo);

	@Query("select r from Route r where r.airportFrom = :airportFrom and r.airportTo =:airportTo")
	List<Route> findAllByAirportFromAndAirportTo(@Param("airportFrom") String airportFrom, @Param("airportTo") String airportTo);

}
