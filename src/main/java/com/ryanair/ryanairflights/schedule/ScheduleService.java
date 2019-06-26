package com.ryanair.ryanairflights.schedule;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ryanair.ryanairflights.interconnections.RequestedFlight;


@Service
public class ScheduleService {
	
	private static final String SCHEDULE_API_RYANAIR = "https://services-api.ryanair.com/timtbl/3/schedules/{departure}/{arrival}/years/{year}/months/{month}";
	RestTemplate restTemplate = new RestTemplate();	

	
	public Schedule findSchedule(String departure, String arrival, RequestedFlight requestedFlight) {
		try {
			return this.restTemplate.getForObject(
					SCHEDULE_API_RYANAIR,
					Schedule.class, departure, arrival, requestedFlight.getYear(), requestedFlight.getMonth());
		} catch (final HttpClientErrorException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getResponseBodyAsString());
		}
		return null;
	}

}
