package com.pooven.middleware.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.pooven.middleware.model.ErrorVO;
import com.pooven.middleware.model.EventData;
import com.pooven.middleware.model.MiddlewareReq;
import com.pooven.middleware.model.MiddlewareResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MiddlewareService {

	@Autowired
	MiddlewareClient middlewareClient;

	@Autowired
	Validator validator;

	/**
	 * Will be triggered for each event
	 * 
	 * @param message
	 * 
	 * @param msg
	 */
	public Mono<String> trigger(Message<EventData> message) {
		EventData data = message.getPayload();
		if (validate(data)) {
			buildMiddlewareReq(data).map(middlewareReq -> middlewareClient.saveMiddleware(middlewareReq));
			return Mono.just("Data pushed successfully to Middleware");
		}
		return Mono.just("Data not pushed to Middleware");
	}

	private Mono<Message<String>> getQueueResp(String msg) {
		return Mono.just(MessageBuilder.withPayload(msg).build());
	}

	/**
	 * Build the Middleware Request Object for each event object
	 * 
	 * @param event
	 * @return MiddlewareReq
	 */
	private Mono<MiddlewareReq> buildMiddlewareReq(EventData data) {
		Mono<MiddlewareResponse> response  = middlewareClient.getMiddleware(data.getId());

		MiddlewareReq.Address address = null;
		if (data.getAddress() != null) {
			address = new MiddlewareReq.Address(data.getAddress().getAddressLine1(),
					data.getAddress().getAddressLine2());
		}
		return Mono.just(new MiddlewareReq(data.getId(), data.getName(), address));
	}

	/**
	 * Perform validation/business logic
	 * 
	 * @param middlewareReq
	 */
	private boolean validate(EventData eventData) {
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
		return false;
	}

}
