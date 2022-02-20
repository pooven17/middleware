package com.pooven.middleware.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pooven.middleware.model.Middleware;
import com.pooven.middleware.model.Token;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MiddlewareController {

	@GetMapping("/accessToken")
	public Token getAccessToken() {
		log.info("*****************API-getAccessToken*****************");
		return new Token("AccessToken");
	}

	@GetMapping("/getMiddleware/{id}")
	public Middleware getMiddleware(@PathVariable(name = "id") String id) {
		log.info("*****************API-getMiddleware-{}*****************", id);
		if (id.equals("404")) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
		}
		return new Middleware(id, id, null, true);
	}

	@PostMapping("/saveMiddleware")
	public Middleware saveMiddleware(@RequestBody Middleware middleware) {
		log.info("*****************API-saveMiddleware-{}*****************", middleware);
		return new Middleware(middleware.getId(), middleware.getName(), null, true);
	}

	@PatchMapping("/saveMiddleware")
	public Middleware updateMiddleware(@RequestBody Middleware middleware) {
		log.info("*****************API-updateMiddleware-{}*****************", middleware.getId());
		return new Middleware(middleware.getId(), middleware.getName(), null, true);
	}

}
