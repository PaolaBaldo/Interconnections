package com.ryanair.ryanairflights.schedule;

import java.util.ArrayList;

import lombok.Data;

@Data
public class Day
{

	private Flight[] flights;

	private String day;

	public Flight[] getFlights() {
		return flights;
	}

	public void setFlights(Flight[] flights) {
		this.flights = flights;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	



}