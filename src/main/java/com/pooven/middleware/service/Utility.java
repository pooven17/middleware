package com.pooven.middleware.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.util.StringUtils;

import com.pooven.middleware.model.EventData;
import com.pooven.middleware.model.EventData.Address;
import com.pooven.middleware.model.EventData.Name;
import com.pooven.middleware.model.MiddlewareRequest;

public class Utility {

	public static void main(String[] args) {

		EventData.Name name1 = new EventData.Name("q", "f1", "l1");
		EventData.Name name2 = new EventData.Name("r", "f2", "l2");
		EventData.Name name3 = new EventData.Name("p", "f3", "l3");

		List<Name> nameList = Arrays.asList(name1, name2, name3);
		EventData.Name preferedName = nameList.stream().filter(name -> name.getType() == "p").findFirst().orElse(null);
		if (preferedName != null) {
			MiddlewareRequest.Name reqName = new MiddlewareRequest.Name(preferedName.getType(),
					preferedName.getFirstName(), preferedName.getLastName());
			System.out.println(reqName);
		}

		List<Address> addressList = new ArrayList<>();
		EventData.Address preferedAddr = addressList.stream().filter(addr -> addr.getType() == "p").findFirst()
				.orElse(null);
		if (preferedAddr != null) {
			MiddlewareRequest.Address reqAddr = new MiddlewareRequest.Address(preferedAddr.getAddressLine1(),
					preferedAddr.getAddressLine2());
		}

	}

	private static Date convertStrToDate(String dateStr) {
		if (StringUtils.isEmpty(dateStr)) {
			return null;
		}
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
		try {
			return dateFormatter.parse(dateStr);
		} catch (Exception exp) {
			return null;
		}

	}

}
