package com.pooven.middleware.model;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventData {
	
	@NotNull
	private String id;

	@NotNull
	private String name;
	
	private Address address;
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Address {
		private String addressLine1;
		private String addressLine2;
	}

}
