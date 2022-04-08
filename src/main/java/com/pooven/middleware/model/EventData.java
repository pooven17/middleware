package com.pooven.middleware.model;

import java.util.List;

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
	
	private List<Address> address;
	
	private List<Name> names;
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Address {
		private String type;
		private String addressLine1;
		private String addressLine2;
		private String city;
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Name {
		private String type;
		private String firstName;
		private String lastName;
	}

}
