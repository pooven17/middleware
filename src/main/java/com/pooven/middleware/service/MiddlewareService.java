package com.pooven.middleware.service;

import org.springframework.stereotype.Service;

import com.pooven.middleware.model.EventData;
import com.pooven.middleware.model.Middleware;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class MiddlewareService {

	private MiddlewareClient middlewareClient;

	public MiddlewareService(MiddlewareClient middlewareClient) {
		this.middlewareClient = middlewareClient;
	}

	public Mono<EventData> trigger(EventData eventData) {
		log.info("*****************Trigger*****************");
		log.info("{}", eventData);
		if (isValid(eventData)) {
			log.info("*****************Valid*****************");
			// @formatter:off
			return middlewareClient.getAccessToken(eventData.getId())
					.flatMap(token -> middlewareClient.getMiddleware(token, eventData.getId())
					.flatMap(middlewareResp -> buildMiddleware(middlewareResp, eventData))
					.flatMap(newMiddleware -> middlewareClient.saveMiddleware(token, newMiddleware)))
					.flatMap(x -> returnEventData(x, eventData));
			// @formatter:on
		}
		return Mono.just(eventData);
	}

	private Mono<Middleware> buildMiddleware(Middleware middlewareResp, EventData eventData) {
		log.info("*****************buildMiddleware-{}*****************", (middlewareResp == null));
		boolean patch = false;
		if(middlewareResp != null && middlewareResp.getId() != null) {
			patch = true;
			return Mono.just(new Middleware(eventData.getId(), eventData.getName(), null, patch));
		}
		return Mono.just(new Middleware(eventData.getId(), eventData.getName(), null, patch));
	}

	private Mono<EventData> returnEventData(Middleware middlewareResp, EventData eventData) {
		log.info("*****************returnEventData-{}*****************",middlewareResp);
		return Mono.just(eventData);
	}

	private boolean isValid(EventData eventData) {
		log.info("*****************isValid*****************");
		if (eventData.getId() == null) {
			return false;
		}
		return true;
	}
}
