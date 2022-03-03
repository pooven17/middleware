package com.pooven.middleware;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pooven.middleware.model.AccessToken;
import com.pooven.middleware.model.EventData;
import com.pooven.middleware.model.MiddlewareRequest;
import com.pooven.middleware.model.MiddlewareResponse;
import com.pooven.middleware.service.LoggerService;
import com.pooven.middleware.service.MiddlewareClient;
import com.pooven.middleware.service.MiddlewareService;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class MiddlewareServiceTest {

	@Mock
	private MiddlewareClient middlewareClient;

	@Mock
	private LoggerService logService;

	private Validator validator;

	@Mock
	private LoggerService.LogBuilder logBuilder;

	@BeforeEach
	void init() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	@Test
	public void triggerForNullEvent() {
		when(logService.builder(anyString())).thenReturn(logBuilder);
//		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		MiddlewareService service = new MiddlewareService(middlewareClient, logService, validator);
		EventData result = service.trigger(null).block();
		Assert.assertNull(result);
		verify(logBuilder, times(2)).log();
	}

	@Test
	public void triggerForEmptyEvent() {
		when(logService.builder(anyString())).thenReturn(logBuilder);
		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		MiddlewareService service = new MiddlewareService(middlewareClient, logService, validator);
		EventData eventData = new EventData();
		EventData result = service.trigger(eventData).block();
		Assert.assertEquals(eventData, result);
		verify(logBuilder, times(3)).log();
	}
	
	@Test
	public void triggerForInValidEvent() {
		when(logService.builder(anyString())).thenReturn(logBuilder);
		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		MiddlewareService service = new MiddlewareService(middlewareClient, logService, validator);
		EventData eventData = new EventData();
		eventData.setName("name");
		EventData result = service.trigger(eventData).block();
		Assert.assertEquals(eventData, result);
		verify(logBuilder, times(3)).log();
	}
	
	@Test
	public void triggerForValidEvent() {
		when(logService.builder(anyString())).thenReturn(logBuilder);
		when(logBuilder.data(anyMap())).thenReturn(logBuilder);
//		when(logBuilder.level(any(Level.class))).thenReturn(logBuilder);
		MiddlewareService service = new MiddlewareService(middlewareClient, logService, validator);
		EventData eventData = new EventData();
		eventData.setId("id");
		eventData.setName("name");
		AccessToken accessToken = new AccessToken();
		accessToken.setAccessToken("dumy_access_token");
		MiddlewareResponse middlewareResponse = new MiddlewareResponse();
		middlewareResponse.setId("id");
		MiddlewareRequest middlewareRequest = new MiddlewareRequest();
		middlewareRequest.setId("id");
		middlewareRequest.setName("name");
		middlewareRequest.setPatch(true);
		given(middlewareClient.getAccessToken()).willReturn(Mono.just(accessToken));
		given(middlewareClient.getMiddleware(accessToken, "id")).willReturn(Mono.just(middlewareResponse));
		given(middlewareClient.saveMiddleware(accessToken, middlewareRequest)).willReturn(Mono.just(middlewareResponse));
		EventData result = service.trigger(eventData).block();
		Assert.assertEquals(eventData, result);
		verify(logBuilder, times(5)).log();
	}

}
