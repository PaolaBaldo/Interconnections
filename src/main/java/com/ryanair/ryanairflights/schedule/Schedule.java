package com.ryanair.ryanairflights.schedule;

import java.util.ArrayList;

import lombok.Data;

@Data
public class Schedule
{
    private String month;

    private Day[] days;

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Day[] getDays() {
		return days;
	}

	public void setDays(Day[] days) {
		this.days = days;
	}
    
    


    
    

  
}
