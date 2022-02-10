package com.pooven.middleware.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.pooven.middleware.model.MiddlewareReq;
import com.pooven.middleware.model.MiddlewareResponse;

import reactor.core.publisher.Mono;

@Service
public class MiddlewareClient {

	@Autowired
	private WebClient webClient;

	/**
	 * Post the middleware request object to middleware API
	 * 
	 * @param middlewareReqVO
	 * @return
	 */
	public Mono<MiddlewareResponse> getMiddleware(String id) {
		String uri = String.format("%s/api/v1/users/%s", "http://localhost:9091", id);
		return webClient.get().uri(uri).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.exchange().doOnError(Exception.class, exception -> {
				}).flatMap(clientResponse -> {
					return clientResponse.bodyToMono(MiddlewareResponse.class);
				}).doOnError(Exception.class, exp -> System.out.println("Error")).onErrorReturn(null);
	}

	public Mono<String> saveMiddleware(MiddlewareReq middlewareReqVO) {
		String uri = String.format("%s/api/v1/saveuser", "http://localhost:9091");
		return webClient.post().uri(uri).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.body(Mono.just(middlewareReqVO), MiddlewareReq.class).exchange()
				.doOnError(Exception.class, exception -> {
				}).flatMap(clientResponse -> {
					return clientResponse.bodyToMono(String.class);
				});
	}

	public Mono<MiddlewareResponse.Organization> getOrganization(String id) {
		String uri = String.format("%s/api/v1/organization/%s", "http://localhost:9091", id);
		return webClient.get().uri(uri).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.exchange().doOnError(Exception.class, exception -> {
				}).flatMap(clientResponse -> {
					return clientResponse.bodyToMono(MiddlewareResponse.Organization.class);
				}).doOnError(Exception.class, exp -> System.out.println("Error")).onErrorReturn(null);
	}

	public Mono<String> saveOrganization(MiddlewareResponse.Organization organization) {
		String uri = String.format("%s/api/v1/saveorganization", "http://localhost:9091");
		return webClient.post().uri(uri).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.body(Mono.just(organization), MiddlewareResponse.Organization.class).exchange()
				.doOnError(Exception.class, exception -> {
				}).flatMap(clientResponse -> {
					return clientResponse.bodyToMono(String.class);
				});
	}

}
