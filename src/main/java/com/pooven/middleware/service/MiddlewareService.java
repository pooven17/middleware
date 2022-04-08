package com.pooven.middleware.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Validator;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.pooven.middleware.model.ErrorVO;
import com.pooven.middleware.model.EventData;
import com.pooven.middleware.model.MiddlewareRequest;
import com.pooven.middleware.model.MiddlewareResponse;

import reactor.core.publisher.Mono;

@Service
public class MiddlewareService {

	private MiddlewareClient middlewareClient;

	private LoggerService logService;

	private Validator validator;

	public MiddlewareService(MiddlewareClient middlewareClient, LoggerService logService, Validator validator) {
		this.middlewareClient = middlewareClient;
		this.logService = logService;
		this.validator = validator;
	}

	public Mono<EventData> trigger(EventData eventData) {
		logService.builder("*****************Trigger*****************").log();
		if (isValid(eventData)) {
			logService.builder("*****************Valid*****************").log();
			// @formatter:off
			return middlewareClient.getAccessToken()
					.flatMap(token -> middlewareClient.getMiddleware(token, eventData.getId())
					.flatMap(middlewareResp -> buildMiddleware(middlewareResp, eventData))
					.flatMap(newMiddleware -> middlewareClient.saveMiddleware(token, newMiddleware)))
					.flatMap(x -> returnEventData(x, eventData));
			// @formatter:on
		}
		return Mono.justOrEmpty(eventData);
	}

	private Mono<MiddlewareRequest> buildMiddleware(MiddlewareResponse middlewareResp, EventData eventData) {
		logService.builder("*****************buildMiddleware-{}*****************").log();
		boolean patch = false;
		if (middlewareResp != null && middlewareResp.getId() != null) {
			patch = true;
			return Mono.just(new MiddlewareRequest(eventData.getId(), eventData.getName(), null, null, patch));
		}
		return Mono.just(new MiddlewareRequest(eventData.getId(), eventData.getName(), null, null, patch));
	}

	private Mono<EventData> returnEventData(MiddlewareResponse middlewareResp, EventData eventData) {
		logService.builder("*****************returnEventData*****************").log();
		return Mono.just(eventData);
	}

	private boolean isValid(EventData eventData) {
		logService.builder("*****************isValid*****************").log();
		if (eventData == null) {
			return false;
		}
		List<ErrorVO> errorList = Optional.ofNullable(validator.validate(eventData))
				.map(constraintViolations -> constraintViolations.stream()
						.map(violation -> ErrorVO.builder().fieldName(violation.getPropertyPath().toString())
								.message(violation.getMessage()).build())
						.collect(Collectors.toList()))
				.orElseGet(() -> new ArrayList<>());
		if (CollectionUtils.isEmpty(errorList)) {
			return true;
		}
		String errorMsg = errorList.stream().map(ErrorVO::getMessage).collect(Collectors.joining(", "));
		Map<String, Object> map = new HashMap<>();
		map.put("errorMsg", errorMsg);
		logService.builder("*****************errorMsg*****************").data(map).log();
		return false;
	}
}
