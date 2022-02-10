package com.pooven.middleware.service;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pooven.middleware.model.EventData;

@RestController
public class MiddlewareController {

	private final MiddlewareService service;

	public MiddlewareController(MiddlewareService service) {
		this.service = service;
	}

	@GetMapping("/trigger")
	public String trigger() {

		EventData data = new EventData("12345", "John", null);
		Message<EventData> reqData = MessageBuilder.withPayload(data).build();
		service.trigger(reqData);
		return "SUCCESS";
	}

}
