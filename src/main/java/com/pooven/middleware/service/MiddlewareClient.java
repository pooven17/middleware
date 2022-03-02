package com.pooven.middleware.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.pooven.middleware.model.AccessToken;
import com.pooven.middleware.model.MiddlewareRequest;
import com.pooven.middleware.model.MiddlewareResponse;
import com.pooven.middleware.model.Organization;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class MiddlewareClient {

	private WebClient webClient;

	private LoggerService logService;

	public MiddlewareClient(WebClient webClient, LoggerService logService) {
		this.webClient = webClient;
		this.logService = logService;
	}

	public void testing() {
		logService.builder("*****************getAccessToken1*****************").log();
		Map<String, Object> map = new HashMap<>();
		logService.builder("*****************getAccessToken2*****************").data(map).log();
		logService.builder("*****************getAccessToken3*****************").level(Level.INFO).log();
	}

	public Mono<AccessToken> getAccessToken() {
		System.out.println("1");
		logService.builder("*****************getAccessToken*****************").log();
		String url = String.format("%s/accessToken", "http://localhost:9090");
		// @formatter:off
		return webClient.get()
				.uri(url)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.exchange()
				.doOnError(Exception.class, exp -> {
					System.out.println("2");
					logService.builder("*****************getAccessToken-Exception*****************").log();
					//exp.printStackTrace();
				})
				.flatMap(clientResponse -> {
					if(clientResponse.statusCode().value() == 404) {
						System.out.println("3");
						logService.builder("*****************getAccessToken-clientResponse-404*****************").log();
						return Mono.just(new AccessToken());
					}
					System.out.println("4");
					logService.builder("*****************getAccessToken-clientResponse*****************").log();
					return clientResponse.bodyToMono(AccessToken.class);
				}).retryWhen(Retry.backoff(3,  Duration.ofSeconds(3))
						.filter(this::isNot4xxServerError)
						.doBeforeRetry( rtry ->{
							Map<String, Object> retry = new HashMap<>();
							retry.put("retryCount", rtry.totalRetries());
							System.out.println("5");
							logService.builder("*****************getAccessToken-Retry*****************"+rtry.totalRetries()).log();
						})
						);
		// @formatter:on
	}

	private boolean isNot4xxServerError(Throwable th) {
		if (th instanceof WebClientResponseException
				&& ((WebClientResponseException) th).getStatusCode().is4xxClientError()) {
			System.out.println("6");
			logService.builder("*****************isNot4xxServerError*****************-false").log();
			return false;
		}
		System.out.println("7");
		logService.builder("*****************isNot4xxServerError*****************-true").log();
		return true;
	}

	public Mono<MiddlewareResponse> getMiddleware(AccessToken token, String id) {
		logService.builder("*****************getMiddleware*****************").log();
		String uri = String.format("%s/getMiddleware/%s", "http://localhost:9090", id);
		// @formatter:off
		return webClient.get()
				.uri(uri)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.header(HttpHeaders.AUTHORIZATION, token.getAccessToken())
				.exchange()
				.doOnError(Exception.class, exp -> {
					logService.builder("*****************getMiddleware-Exception*****************").log();
					exp.printStackTrace();
				})
				.flatMap(clientResponse -> {
					if(clientResponse.statusCode().value() == 404) {
						logService.builder("*****************getMiddleware-clientResponse-404*****************").log();
						return Mono.just(new MiddlewareResponse());
					}
					logService.builder("*****************getMiddleware-clientResponse*****************").log();
					return clientResponse.bodyToMono(MiddlewareResponse.class);
				}).retryWhen(Retry.backoff(3,  Duration.ofSeconds(3))
						.filter(this::isNot4xxServerError)
						.doBeforeRetry( rtry ->{
							Map<String, Object> retry = new HashMap<>();
							retry.put("retryCount", rtry.totalRetries());
							logService.builder("*****************getMiddleware-Retry*****************"+rtry.totalRetries()).log();
						})
						);
		// @formatter:on
	}

	public Mono<MiddlewareResponse> saveMiddleware(AccessToken token, MiddlewareRequest middlewareRequest) {
		logService.builder("*****************saveMiddleware-{}*****************" + middlewareRequest).log();
		String uri = String.format("%s/saveMiddleware", "http://localhost:9090");
		HttpMethod httpMethod = middlewareRequest.isPatch() ? HttpMethod.PATCH : HttpMethod.POST;
		// @formatter:off
		return webClient.method(httpMethod)
				.uri(uri)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				//.header(HttpHeaders.AUTHORIZATION, token.getAccessToken())
				.body(Mono.just(middlewareRequest), MiddlewareResponse.class)
				.exchange()
				.doOnError(Exception.class, exp -> {
					logService.builder("*****************saveMiddleware-Exception*****************").log();
					exp.printStackTrace();
				})
				.flatMap(clientResponse -> {
					if(clientResponse.statusCode().value() == 404) {
						logService.builder("*****************saveMiddleware-clientResponse-404*****************").log();
						return Mono.just(new MiddlewareResponse());
					}
					if(clientResponse.statusCode().value() == 400) {
						logService.builder("*****************saveMiddleware-clientResponse-400*****************").log();
						return Mono.just(new MiddlewareResponse());
					}
					logService.builder("*****************saveMiddleware-clientResponse*****************").log();
					return clientResponse.bodyToMono(MiddlewareResponse.class);
				}).retryWhen(Retry.backoff(3,  Duration.ofSeconds(3))
						.filter(this::isNot4xxServerError)
						.doBeforeRetry( rtry ->{
							Map<String, Object> retry = new HashMap<>();
							retry.put("retryCount", rtry.totalRetries());
							logService.builder("*****************saveMiddleware-Retry*****************"+rtry.totalRetries()).log();
						})
						);
		// @formatter:on
	}

	public Mono<Organization> getOrganization(AccessToken token, String id) {
		logService.builder("*****************getOrganization*****************").log();
		String uri = String.format("%s/getMiddleware/%s", "http://localhost:9090", id);
		// @formatter:off
		return webClient.get()
				.uri(uri)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.header(HttpHeaders.AUTHORIZATION, token.getAccessToken())
				.exchange()
				.doOnError(Exception.class, exp -> {
					logService.builder("*****************getOrganization-Exception*****************").log();
					exp.printStackTrace();
				})
				.flatMap(clientResponse -> {
					if(clientResponse.statusCode().value() == 404) {
						logService.builder("*****************getOrganization-clientResponse-404*****************").log();
						return Mono.just(new Organization());
					}
					logService.builder("*****************getOrganization-clientResponse*****************").log();
					return clientResponse.bodyToMono(Organization.class);
				}).retryWhen(Retry.backoff(3,  Duration.ofSeconds(3))
						.filter(this::isNot4xxServerError)
						.doBeforeRetry( rtry ->{
							Map<String, Object> retry = new HashMap<>();
							retry.put("retryCount", rtry.totalRetries());
							logService.builder("*****************getOrganization-Retry*****************"+rtry.totalRetries()).log();
						})
						);
		// @formatter:on
	}

}
