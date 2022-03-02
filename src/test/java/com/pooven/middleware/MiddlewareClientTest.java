package com.pooven.middleware;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.logging.Level;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pooven.middleware.model.AccessToken;
import com.pooven.middleware.model.MiddlewareRequest;
import com.pooven.middleware.model.MiddlewareResponse;
import com.pooven.middleware.model.Organization;
import com.pooven.middleware.service.LoggerService;
import com.pooven.middleware.service.MiddlewareClient;

import junit.framework.Assert;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class MiddlewareClientTest {

	@Mock
	private WebClient webClient;

	@Mock
	private LoggerService logService;

	@Mock
	private ExchangeFunction exchangeFunction;

	@Captor
	private ArgumentCaptor<ClientRequest> captor;

	@Mock
	private LoggerService.LogBuilder logBuilder;

	@BeforeEach
	void init() {
		webClient = WebClient.builder().exchangeFunction(exchangeFunction).build();
	}

	@Test
	public void testing() {
		when(logService.builder(anyString())).thenReturn(logBuilder);
		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		MiddlewareClient client = new MiddlewareClient(webClient, logService);
		client.testing();
		verify(logBuilder, times(3)).log();

	}

	@Test
	public void getAccessToken503Exception() {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		WebClientResponseException responseException = ClientResponse.create(HttpStatus.SERVICE_UNAVAILABLE).build()
				.createException().block();
		doThrow(responseException).when(exchangeFunction).exchange(any(ClientRequest.class));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		assertThrows(Exception.class, () -> {
			middlewareClient.getAccessToken().block();
		});
		verify(logBuilder, times(12)).log();
	}

	@Test
	public void getAccessToken404Exception() {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		WebClientResponseException responseException = ClientResponse.create(HttpStatus.NOT_FOUND).build()
				.createException().block();
		doThrow(responseException).when(exchangeFunction).exchange(any(ClientRequest.class));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		assertThrows(Exception.class, () -> {
			middlewareClient.getAccessToken().block();
		});
		verify(logBuilder, times(3)).log();
	}

	@Test
	public void getAccessToken400Exception() {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		WebClientResponseException responseException = ClientResponse.create(HttpStatus.BAD_REQUEST).build()
				.createException().block();
		doThrow(responseException).when(exchangeFunction).exchange(any(ClientRequest.class));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		assertThrows(Exception.class, () -> {
			middlewareClient.getAccessToken().block();
		});
		verify(logBuilder, times(3)).log();
	}

	@Test
	public void getAccessToken404() throws IOException {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		AccessToken expectedResp = new AccessToken();
		ObjectMapper objMapper = new ObjectMapper();
		ClientResponse clientResponse = ClientResponse.create(HttpStatus.NOT_FOUND)
				.header(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON.toString()).body("").build();
		given(this.exchangeFunction.exchange(any(ClientRequest.class))).willReturn(Mono.just(clientResponse));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken result = middlewareClient.getAccessToken().block();
		Assert.assertEquals(expectedResp.getAccessToken(), result.getAccessToken());
		verify(logBuilder, times(2)).log();
	}

	@Test
	public void getAccessToken200EmptyResp() throws IOException {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		AccessToken expectedResp = new AccessToken();
		ObjectMapper objMapper = new ObjectMapper();
		String responseStr = objMapper.writeValueAsString(expectedResp);
		ClientResponse clientResponse = ClientResponse.create(HttpStatus.NOT_FOUND)
				.header(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON.toString()).body(responseStr)
				.build();
		given(this.exchangeFunction.exchange(any(ClientRequest.class))).willReturn(Mono.just(clientResponse));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken result = middlewareClient.getAccessToken().block();
		Assert.assertEquals(expectedResp.getAccessToken(), result.getAccessToken());
		verify(logBuilder, times(2)).log();
	}

	@Test
	public void getAccessToken200Success() throws IOException {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		AccessToken expectedResp = new AccessToken();
		expectedResp.setAccessToken("dumy_access_token");
		ObjectMapper objMapper = new ObjectMapper();
		String responseStr = objMapper.writeValueAsString(expectedResp);
		ClientResponse clientResponse = ClientResponse.create(HttpStatus.OK)
				.header(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON.toString()).body(responseStr)
				.build();
		given(this.exchangeFunction.exchange(any(ClientRequest.class))).willReturn(Mono.just(clientResponse));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken result = middlewareClient.getAccessToken().block();
		Assert.assertEquals(expectedResp.getAccessToken(), result.getAccessToken());
		verify(logBuilder, times(2)).log();
	}

	@Test
	public void getMiddleware503Exception() {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		WebClientResponseException responseException = ClientResponse.create(HttpStatus.SERVICE_UNAVAILABLE).build()
				.createException().block();
		doThrow(responseException).when(exchangeFunction).exchange(any(ClientRequest.class));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		assertThrows(Exception.class, () -> {
			middlewareClient.getMiddleware(token, "id").block();
		});
		verify(logBuilder, times(12)).log();
	}

	@Test
	public void getMiddleware404Exception() {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		WebClientResponseException responseException = ClientResponse.create(HttpStatus.NOT_FOUND).build()
				.createException().block();
		doThrow(responseException).when(exchangeFunction).exchange(any(ClientRequest.class));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		assertThrows(Exception.class, () -> {
			middlewareClient.getMiddleware(token, "id").block();
		});
		verify(logBuilder, times(3)).log();
	}

	@Test
	public void getMiddleware400Exception() {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		WebClientResponseException responseException = ClientResponse.create(HttpStatus.BAD_REQUEST).build()
				.createException().block();
		doThrow(responseException).when(exchangeFunction).exchange(any(ClientRequest.class));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		assertThrows(Exception.class, () -> {
			middlewareClient.getMiddleware(token, "id").block();
		});
		verify(logBuilder, times(3)).log();
	}

	@Test
	public void getMiddleware404() throws IOException {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		MiddlewareResponse expectedResp = new MiddlewareResponse();
		ObjectMapper objMapper = new ObjectMapper();
		ClientResponse clientResponse = ClientResponse.create(HttpStatus.NOT_FOUND)
				.header(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON.toString()).body("").build();
		given(this.exchangeFunction.exchange(any(ClientRequest.class))).willReturn(Mono.just(clientResponse));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		MiddlewareResponse result = middlewareClient.getMiddleware(token, "id").block();
		Assert.assertEquals(expectedResp.getId(), result.getId());
		verify(logBuilder, times(2)).log();
	}

	@Test
	public void getMiddleware200EmptyResp() throws IOException {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		MiddlewareResponse expectedResp = new MiddlewareResponse();
		ObjectMapper objMapper = new ObjectMapper();
		String responseStr = objMapper.writeValueAsString(expectedResp);
		ClientResponse clientResponse = ClientResponse.create(HttpStatus.OK)
				.header(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON.toString()).body(responseStr)
				.build();
		given(this.exchangeFunction.exchange(any(ClientRequest.class))).willReturn(Mono.just(clientResponse));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		MiddlewareResponse result = middlewareClient.getMiddleware(token, "id").block();
		Assert.assertEquals(expectedResp.getId(), result.getId());
		verify(logBuilder, times(2)).log();
	}

	@Test
	public void getMiddleware200Success() throws IOException {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		MiddlewareResponse expectedResp = new MiddlewareResponse();
		expectedResp.setId("id");
		ObjectMapper objMapper = new ObjectMapper();
		String responseStr = objMapper.writeValueAsString(expectedResp);
		ClientResponse clientResponse = ClientResponse.create(HttpStatus.OK)
				.header(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON.toString()).body(responseStr)
				.build();
		given(this.exchangeFunction.exchange(any(ClientRequest.class))).willReturn(Mono.just(clientResponse));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		MiddlewareResponse result = middlewareClient.getMiddleware(token, "id").block();
		Assert.assertEquals(expectedResp.getId(), result.getId());
		verify(logBuilder, times(2)).log();
	}

	@Test
	public void getOrganization503Exception() {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		WebClientResponseException responseException = ClientResponse.create(HttpStatus.SERVICE_UNAVAILABLE).build()
				.createException().block();
		doThrow(responseException).when(exchangeFunction).exchange(any(ClientRequest.class));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		assertThrows(Exception.class, () -> {
			middlewareClient.getOrganization(token, "id").block();
		});
		verify(logBuilder, times(12)).log();
	}

	@Test
	public void getOrganization404Exception() {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		WebClientResponseException responseException = ClientResponse.create(HttpStatus.NOT_FOUND).build()
				.createException().block();
		doThrow(responseException).when(exchangeFunction).exchange(any(ClientRequest.class));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		assertThrows(Exception.class, () -> {
			middlewareClient.getOrganization(token, "id").block();
		});
		verify(logBuilder, times(3)).log();
	}

	@Test
	public void getOrganization400Exception() {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		WebClientResponseException responseException = ClientResponse.create(HttpStatus.BAD_REQUEST).build()
				.createException().block();
		doThrow(responseException).when(exchangeFunction).exchange(any(ClientRequest.class));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		assertThrows(Exception.class, () -> {
			middlewareClient.getOrganization(token, "id").block();
		});
		verify(logBuilder, times(3)).log();
	}

	@Test
	public void getOrganization404() throws IOException {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		Organization expectedResp = new Organization();
		ObjectMapper objMapper = new ObjectMapper();
		ClientResponse clientResponse = ClientResponse.create(HttpStatus.NOT_FOUND)
				.header(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON.toString()).body("").build();
		given(this.exchangeFunction.exchange(any(ClientRequest.class))).willReturn(Mono.just(clientResponse));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		Organization result = middlewareClient.getOrganization(token, "id").block();
		Assert.assertEquals(expectedResp.getId(), result.getId());
		verify(logBuilder, times(2)).log();
	}

	@Test
	public void getOrganization200EmptyResp() throws IOException {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		Organization expectedResp = new Organization();
		ObjectMapper objMapper = new ObjectMapper();
		String responseStr = objMapper.writeValueAsString(expectedResp);
		ClientResponse clientResponse = ClientResponse.create(HttpStatus.OK)
				.header(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON.toString()).body(responseStr)
				.build();
		given(this.exchangeFunction.exchange(any(ClientRequest.class))).willReturn(Mono.just(clientResponse));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		Organization result = middlewareClient.getOrganization(token, "id").block();
		Assert.assertEquals(expectedResp.getId(), result.getId());
		verify(logBuilder, times(2)).log();
	}

	@Test
	public void getOrganization200Success() throws IOException {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		Organization expectedResp = new Organization();
		expectedResp.setId("id");
		ObjectMapper objMapper = new ObjectMapper();
		String responseStr = objMapper.writeValueAsString(expectedResp);
		ClientResponse clientResponse = ClientResponse.create(HttpStatus.OK)
				.header(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON.toString()).body(responseStr)
				.build();
		given(this.exchangeFunction.exchange(any(ClientRequest.class))).willReturn(Mono.just(clientResponse));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		Organization result = middlewareClient.getOrganization(token, "id").block();
		Assert.assertEquals(expectedResp.getId(), result.getId());
		verify(logBuilder, times(2)).log();
	}

	@Test
	public void saveMiddleware503Exception() {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		WebClientResponseException responseException = ClientResponse.create(HttpStatus.SERVICE_UNAVAILABLE).build()
				.createException().block();
		doThrow(responseException).when(exchangeFunction).exchange(any(ClientRequest.class));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		MiddlewareRequest middlewareRequest = new MiddlewareRequest();
		assertThrows(Exception.class, () -> {
			middlewareClient.saveMiddleware(token, middlewareRequest).block();
		});
		verify(logBuilder, times(12)).log();
	}

	@Test
	public void saveMiddleware404Exception() {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		WebClientResponseException responseException = ClientResponse.create(HttpStatus.NOT_FOUND).build()
				.createException().block();
		doThrow(responseException).when(exchangeFunction).exchange(any(ClientRequest.class));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		MiddlewareRequest middlewareRequest = new MiddlewareRequest();
		assertThrows(Exception.class, () -> {
			middlewareClient.saveMiddleware(token, middlewareRequest).block();
		});
		verify(logBuilder, times(3)).log();
	}

	@Test
	public void saveMiddleware400Exception() {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		WebClientResponseException responseException = ClientResponse.create(HttpStatus.BAD_REQUEST).build()
				.createException().block();
		doThrow(responseException).when(exchangeFunction).exchange(any(ClientRequest.class));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		MiddlewareRequest middlewareRequest = new MiddlewareRequest();
		assertThrows(Exception.class, () -> {
			middlewareClient.saveMiddleware(token, middlewareRequest).block();
		});
		verify(logBuilder, times(3)).log();
	}

	@Test
	public void saveMiddleware404() throws IOException {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		MiddlewareResponse expectedResp = new MiddlewareResponse();
		ClientResponse clientResponse = ClientResponse.create(HttpStatus.NOT_FOUND)
				.header(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON.toString()).body("").build();
		given(this.exchangeFunction.exchange(any(ClientRequest.class))).willReturn(Mono.just(clientResponse));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		MiddlewareRequest middlewareRequest = new MiddlewareRequest();
		MiddlewareResponse result = middlewareClient.saveMiddleware(token, middlewareRequest).block();
		Assert.assertEquals(expectedResp.getId(), result.getId());
		verify(logBuilder, times(2)).log();
	}

	@Test
	public void saveMiddleware200EmptyResp() throws IOException {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		MiddlewareResponse expectedResp = new MiddlewareResponse();
		ObjectMapper objMapper = new ObjectMapper();
		String responseStr = objMapper.writeValueAsString(expectedResp);
		ClientResponse clientResponse = ClientResponse.create(HttpStatus.OK)
				.header(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON.toString()).body(responseStr)
				.build();
		given(this.exchangeFunction.exchange(any(ClientRequest.class))).willReturn(Mono.just(clientResponse));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		MiddlewareRequest middlewareRequest = new MiddlewareRequest();
		MiddlewareResponse result = middlewareClient.saveMiddleware(token, middlewareRequest).block();
		Assert.assertEquals(expectedResp.getId(), result.getId());
		verify(logBuilder, times(2)).log();
	}

	@Test
	public void saveMiddleware200Success() throws IOException {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		MiddlewareResponse expectedResp = new MiddlewareResponse();
		expectedResp.setId("id");
		ObjectMapper objMapper = new ObjectMapper();
		String responseStr = objMapper.writeValueAsString(expectedResp);
		ClientResponse clientResponse = ClientResponse.create(HttpStatus.OK)
				.header(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON.toString()).body(responseStr)
				.build();
		given(this.exchangeFunction.exchange(any(ClientRequest.class))).willReturn(Mono.just(clientResponse));
		MiddlewareClient middlewareClient = new MiddlewareClient(webClient, logService);
		AccessToken token = new AccessToken();
		MiddlewareRequest middlewareRequest = new MiddlewareRequest();
		MiddlewareResponse result = middlewareClient.saveMiddleware(token, middlewareRequest).block();
		Assert.assertEquals(expectedResp.getId(), result.getId());
		verify(logBuilder, times(2)).log();
	}

}
