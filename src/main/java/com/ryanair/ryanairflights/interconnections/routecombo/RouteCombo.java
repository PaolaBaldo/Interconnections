package com.ryanair.ryanairflights.interconnections.routecombo;

import java.util.List;

import com.ryanair.ryanairflights.interconnections.leg.Leg;



public class RouteCombo {
	
	private List<Leg> possibleFirstLegs;
	private List<Leg> possibleSecondLegs;
	
	
	
	public List<Leg> getPossibleFirstLegs() {
		return possibleFirstLegs;
	}



	public void setPossibleFirstLegs(List<Leg> possibleFirstLegs) {
		this.possibleFirstLegs = possibleFirstLegs;
	}



	public List<Leg> getPossibleSecondLegs() {
		return possibleSecondLegs;
	}



	public void setPossibleSecondLegs(List<Leg> possibleSecondLegs) {
		this.possibleSecondLegs = possibleSecondLegs;
	}



	public RouteCombo(List<Leg> possibleFirstLegs, List<Leg> possibleSecondLegs) {
		super();
		this.possibleFirstLegs = possibleFirstLegs;
		this.possibleSecondLegs = possibleSecondLegs;
	}
	
	

}
