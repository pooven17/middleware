package com.pooven.middleware.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pooven.middleware.model.AccessToken;
import com.pooven.middleware.model.MiddlewareResponse;
import com.pooven.middleware.model.Organization;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MiddlewareController {

	@GetMapping("/accessToken")
	public AccessToken getAccessToken() {
		log.info("*****************API-getAccessToken*****************");
		return new AccessToken("AccessToken");
	}

	@GetMapping("/getMiddleware/{id}")
	public MiddlewareResponse getMiddleware(@PathVariable(name = "id") String id) {
		log.info("*****************API-getMiddleware-{}*****************", id);
		if (id.equals("404")) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
		}
		return new MiddlewareResponse(id, id, null);
	}
	
	@GetMapping("/getOrganization/{id}")
	public Organization getOrganization(@PathVariable(name = "id") String id) {
		log.info("*****************API-getMiddleware-{}*****************", id);
		if (id.equals("404")) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
		}
		return new Organization(id, id, false);
	}

	@PostMapping("/saveMiddleware")
	public MiddlewareResponse saveMiddleware(@RequestBody MiddlewareResponse middleware) {
		log.info("*****************API-saveMiddleware-{}*****************", middleware);
		return new MiddlewareResponse(middleware.getId(), middleware.getName(), null);
	}

	@PatchMapping("/saveMiddleware")
	public MiddlewareResponse updateMiddleware(@RequestBody MiddlewareResponse middleware) {
		log.info("*****************API-updateMiddleware-{}*****************", middleware.getId());
		return new MiddlewareResponse(middleware.getId(), middleware.getName(), null);
	}

}
