package com.ryanair.ryanairflights.schedule;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class ScheduleService {
	
	RestTemplate restTemplate = new RestTemplate();	
	public Schedule findScheduleNoStops(String departure, String arrival, int year, int month) {
		
		return this.restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/{departure}/{arrival}/years/{year}/months/{month}", Schedule.class, 
				departure, arrival, year, month);
	}

}
