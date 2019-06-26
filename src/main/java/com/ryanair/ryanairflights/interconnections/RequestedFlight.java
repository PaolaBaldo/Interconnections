package com.ryanair.ryanairflights.interconnections;

import java.time.LocalDateTime;

public class RequestedFlight {
	
	private String departure;
	private String arrival;
	private int month;
	private int year;
	private LocalDateTime requestedDepartureDateTime;
	private LocalDateTime requestedArrivalDateTime;
	
	

	

	public RequestedFlight(String departure, String arrival, int month, int year,
			LocalDateTime requestedDepartureDateTime, LocalDateTime requestedArrivalDateTime) {
		super();
		this.departure = departure;
		this.arrival = arrival;
		this.month = month;
		this.year = year;
		this.requestedDepartureDateTime = requestedDepartureDateTime;
		this.requestedArrivalDateTime = requestedArrivalDateTime;
	}

	public RequestedFlight() {
	}
	
	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public String getArrival() {
		return arrival;
	}

	public void setArrival(String arrival) {
		this.arrival = arrival;
	}


	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public LocalDateTime getRequestedDepartureDateTime() {
		return requestedDepartureDateTime;
	}

	public void setRequestedDepartureDateTime(LocalDateTime requestedDepartureDateTime) {
		this.requestedDepartureDateTime = requestedDepartureDateTime;
	}

	public LocalDateTime getRequestedArrivalDateTime() {
		return requestedArrivalDateTime;
	}

	public void setRequestedArrivalDateTime(LocalDateTime requestedArrivalDateTime) {
		this.requestedArrivalDateTime = requestedArrivalDateTime;
	}
	
	
}