package com.ryanair.ryanairflights.schedule;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


@Service
public class ScheduleService {
	
	RestTemplate restTemplate = new RestTemplate();	
	public Schedule findScheduleNoStops(String departure, String arrival, int year, int month) {
		
		return this.restTemplate.getForObject("https://services-api.ryanair.com/timtbl/3/schedules/{departure}/{arrival}/years/{year}/months/{month}", Schedule.class, 
				departure, arrival, year, month);
	}
	
	public Schedule findSchedule(String departure, String arrival, int year, int month) {
		try {
			return this.restTemplate.getForObject(
					"https://services-api.ryanair.com/timtbl/3/schedules/{departure}/{arrival}/years/{year}/months/{month}",
					Schedule.class, departure, arrival, year, month);
		} catch (final HttpClientErrorException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getResponseBodyAsString());
		}
		return null;
	}

}
