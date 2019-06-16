package com.ryanair.ryanairflights.interconnections;

import java.util.ArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class Interconnection
{
	private String stops;
	private ArrayList<Leg> legs;

	public ArrayList<Leg> getLegs() {
		return legs;
	}

	public void setLegs(ArrayList<Leg> legs) {
		this.legs = legs;
	}

	public String getStops() {
		return stops;
	}

	public void setStops(String stops) {
		this.stops = stops;
	}
	
	

    
}