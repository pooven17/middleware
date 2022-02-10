package com.pooven.middleware;

import java.util.UUID;
import java.util.function.Consumer;

import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

import com.pooven.middleware.model.EventData;
import com.pooven.middleware.service.MiddlewareService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class MiddlewareConfig {

	@Bean
	public Consumer<Flux<Message<EventData>>> sink(MiddlewareService service) {
		return msg -> msg.flatMap(message -> setRequestID(message)
				.flatMap(mess -> logMessage(mess))
				.map(Message::getPayload)
				.flatMap(x -> service.trigger(message))
				.doOnNext(s -> ack(message))
				.doOnError(e -> reject(message, e)));

	}

	private Mono<Message<EventData>> logMessage(Message<EventData> msg) {
		return Mono.just(msg);
	}

	private Mono<Message<EventData>> setRequestID(Message<EventData> msg) {
		String reqId = UUID.randomUUID().toString();
		msg.getHeaders().put("ReqID", reqId);
		return Mono.just(msg);
	}

	private void ack(Message<EventData> message) {
		// Impl to acknowledge
	}

	private void reject(Message<EventData> message, Throwable e) {
		// Impl to reject due to error
	}

	@Bean
	WebClient webClient(ReactiveClientRegistrationRepository clientRegistrations,
			ServerOAuth2AuthorizedClientRepository authorizedClients) {
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				clientRegistrations, authorizedClients);
		oauth.setDefaultClientRegistrationId("middleware");
		return WebClient.builder().filter(oauth).build();
	}

	@Bean
	public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http) {
		return http.authorizeExchange().anyExchange().permitAll().and().build();
	}

	@Bean
	public Validator getValidator() {
		return Validation.buildDefaultValidatorFactory().getValidator();
	}

}
