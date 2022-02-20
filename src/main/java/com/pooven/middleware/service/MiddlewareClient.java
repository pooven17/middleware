package com.pooven.middleware.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.pooven.middleware.model.Middleware;
import com.pooven.middleware.model.Token;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class MiddlewareClient {

	private WebClient webClient;

	public MiddlewareClient(WebClient webClient) {
		this.webClient = webClient;
	}

	public Mono<Token> getAccessToken(String id) {
		log.info("*****************getAccessToken*****************");
		String uri = String.format("%s/accessToken", "http://localhost:9090");
		// @formatter:off
		return webClient.get()
				.uri(uri)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.exchange()
				.doOnError(Exception.class, exp -> {
					log.info("*****************getAccessToken-Exception*****************");
					exp.printStackTrace();
				})
				.flatMap(clientResponse -> {
					log.info("*****************getAccessToken-clientResponse*****************");
					return clientResponse.bodyToMono(Token.class);
				})
				.onErrorReturn(null);
		// @formatter:on
		// return Mono.just(new Token(id));
	}

	public Mono<Middleware> getMiddleware(Token token, String id) {
		log.info("*****************getMiddleware*****************");
		String uri = String.format("%s/getMiddleware/%s", "http://localhost:9090", id);
		// @formatter:off
		return webClient.get()
				.uri(uri)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.header(HttpHeaders.AUTHORIZATION, token.getAccessToken())
				.exchange()
				.doOnError(Exception.class, exp -> {
					log.info("*****************getMiddleware-Exception*****************");
					exp.printStackTrace();
				})
				.flatMap(clientResponse -> {
					log.info("*****************getMiddleware-clientResponse*****************");
					//if(clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) 
					//	return Mono.empty();
					
					return clientResponse.bodyToMono(Middleware.class);
				})
				.onErrorReturn(null);
		// @formatter:on
		// return Mono.just(new Middleware(id, id, null));
	}
	
	public Mono<Middleware> saveMiddleware(Token token, Middleware middleware) {
		log.info("*****************saveMiddleware-{}*****************", middleware);
		String uri = String.format("%s/saveMiddleware", "http://localhost:9090" );
		HttpMethod httpMethod = middleware.isPatch() ? HttpMethod.PATCH : HttpMethod.POST;
		// @formatter:off
		return webClient.method(httpMethod)
				.uri(uri)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				//.header(HttpHeaders.AUTHORIZATION, token.getAccessToken())
				.body(Mono.just(middleware), Middleware.class)
				.exchange()
				.doOnError(Exception.class, exp -> {
					log.info("*****************saveMiddleware-Exception*****************");
					exp.printStackTrace();
				})
				.flatMap(clientResponse -> {
					log.info("*****************saveMiddleware-clientResponse*****************");
					return clientResponse.bodyToMono(Middleware.class);
				})
				.onErrorReturn(null);
		// @formatter:on
		// return Mono.just(new Middleware(id, id, null));
	}

}
