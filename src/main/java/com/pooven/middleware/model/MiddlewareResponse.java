package com.pooven.middleware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MiddlewareResponse {

	private String id;

	private String name;

	private Address address;

	private Organization orgization;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Address {
		private String addressLine1;
		private String addressLine2;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Organization {
		private String id;
		private String code;
		private boolean delete;
	}

}
