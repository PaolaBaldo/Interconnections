package com.ryanair.ryanairflights.interconnections;

import lombok.Data;

@Data
public class Leg
{
    
    private String departureAirport;

    private String departureDateTime;
    
    private String arrivalDateTime;

    private String arrivalAirport;
    
    
    


	public Leg(String departureAirport, String arrivalAirport, String departureDateTime, String arrivalDateTime) {
		super();
		this.departureAirport = departureAirport;
		this.departureDateTime = departureDateTime;
		this.arrivalDateTime = arrivalDateTime;
		this.arrivalAirport = arrivalAirport;
	}



	public Leg() {
		// TODO Auto-generated constructor stub
	}



	public String getDepartureAirport() {
		return departureAirport;
	}

	public void setDepartureAirport(String departureAirport) {
		this.departureAirport = departureAirport;
	}

	public String getDepartureDateTime() {
		return departureDateTime;
	}

	public void setDepartureDateTime(String departureDateTime) {
		this.departureDateTime = departureDateTime;
	}

	public String getArrivalDateTime() {
		return arrivalDateTime;
	}

	public void setArrivalDateTime(String arrivalDateTime) {
		this.arrivalDateTime = arrivalDateTime;
	}

	public String getArrivalAirport() {
		return arrivalAirport;
	}

	public void setArrivalAirport(String arrivalAirport) {
		this.arrivalAirport = arrivalAirport;
	}
    
    


}

