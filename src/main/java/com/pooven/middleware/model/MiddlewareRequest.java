package com.pooven.middleware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MiddlewareRequest {

	private String id;

	private String name;

	private Address address;

	// @JsonIgnore
	private boolean isPatch;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Address {
		private String addressLine1;
		private String addressLine2;
	}

}
