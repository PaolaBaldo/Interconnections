package com.ryanair.ryanairflights.routes;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Entity
public class Route {
	
	private @Id @GeneratedValue Long id;
	private String airportFrom;
	private String airportTo;
	private String connectingAirport = null;
	private boolean newRoute;
	private boolean seasonalRoute;
	private String operator;


	// Getter Methods

	public String getAirportFrom() {
		return airportFrom;
	}

	public String getAirportTo() {
		return airportTo;
	}

	public String getConnectingAirport() {
		return connectingAirport;
	}

	public boolean getNewRoute() {
		return newRoute;
	}

	public boolean getSeasonalRoute() {
		return seasonalRoute;
	}

	public String getOperator() {
		return operator;
	}


	// Setter Methods

	public void setAirportFrom(String airportFrom) {
		this.airportFrom = airportFrom;
	}

	public void setAirportTo(String airportTo) {
		this.airportTo = airportTo;
	}

	public void setConnectingAirport(String connectingAirport) {
		this.connectingAirport = connectingAirport;
	}

	public void setNewRoute(boolean newRoute) {
		this.newRoute = newRoute;
	}

	public void setSeasonalRoute(boolean seasonalRoute) {
		this.seasonalRoute = seasonalRoute;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}


	
}
