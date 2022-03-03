package com.pooven.middleware;

import java.util.function.Consumer;

import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

import com.pooven.middleware.model.EventData;
import com.pooven.middleware.service.MiddlewareService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class MiddlewareConfig {

	/*
	 * @Bean public Function<String, String> convertToUppercase() { return (value)
	 * -> { log.info("Received {}", value); String upperCaseValue =
	 * value.toUpperCase(); log.info("Sending {}", upperCaseValue); return
	 * upperCaseValue; }; }
	 */

	// @Bean
	public Consumer<String> tmp() {
		return (message) -> {
			log.info("*****************Received the value {} in Consumer*****************", message);
		};
	}

	@Bean
	public Consumer<Flux<Message<EventData>>> onReceive(MiddlewareService middlewareService) {
		log.info("*****************onReceive Config*****************");
		// @formatter:off
		return flx -> flx.flatMap(message -> setRequestID(message)
				.flatMap(msg -> logMessage(msg))
				.map(Message::getPayload).flatMap(eventData -> middlewareService.trigger(eventData))
				.doOnNext(s -> ack(message))
				.doOnError(e -> reject(message, e)))
				.subscribe();
		// @formatter:on
	}

	private Mono<EventData> trigger(EventData eventData) {
		log.info("*****************Trigger*****************");
		return Mono.just(eventData);
	}

	private Mono<Message<EventData>> logMessage(Message<EventData> msg) {
		log.info("*****************logMessage*****************");
		return Mono.just(msg);
	}

	private Mono<Message<EventData>> setRequestID(Message<EventData> msg) {
		log.info("*****************setRequestID*****************");
		return Mono.just(msg);
	}

	private void ack(Message<EventData> msg) {
		log.info("*****************ack*****************");
	}

	private void reject(Message<EventData> msg, Throwable e) {
		log.info("*****************reject*****************");
		e.printStackTrace();
	}

	@Bean
	public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http) {
		http.csrf().disable();
		return http.authorizeExchange().anyExchange().permitAll().and().build();
	}

	@Bean
	WebClient webClient() {
		return WebClient.builder().build();
	}

	@Bean
	public Validator getValidator() {
		return Validation.buildDefaultValidatorFactory().getValidator();
	}

}
