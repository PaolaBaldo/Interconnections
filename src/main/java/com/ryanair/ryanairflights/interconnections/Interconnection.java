package com.ryanair.ryanairflights.interconnections;
import java.util.List;

import com.ryanair.ryanairflights.interconnections.leg.Leg;



public class Interconnection
{

	private String stops;
	private List<Leg> legs;
	

	public Interconnection(String stops, List<Leg> legs) {
		super();
		this.stops = stops;
		this.legs = legs;
	}

	public List<Leg> getLegs() {
		return legs;
	}

	public void setLegs(List<Leg> legs2) {
		this.legs = legs2;
	}

	public String getStops() {
		return stops;
	}

	public void setStops(String stops) {
		this.stops = stops;
	}
	
	

    
}