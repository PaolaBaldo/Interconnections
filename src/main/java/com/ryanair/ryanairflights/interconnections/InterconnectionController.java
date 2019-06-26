package com.ryanair.ryanairflights.interconnections;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ryanair.ryanairflights.interconnections.services.InterconnectionService;



@RestController
public class InterconnectionController {
	
	@Autowired
	private InterconnectionService interconnectionService;

	@RequestMapping(value = "/somevalidcontext/interconnections", method = RequestMethod.GET)
	public @ResponseBody List<Interconnection> findAllInterconnections(@RequestParam String departure,
			@RequestParam String arrival,
			@RequestParam("departureDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestedDepartureDateTime,
			@RequestParam("arrivalDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestedArrivalDateTime) {
		
		List<Interconnection> interconnections  = interconnectionService.findInterconnections(departure, arrival, requestedDepartureDateTime, requestedArrivalDateTime);

		return interconnections;
	}


	
	


}
