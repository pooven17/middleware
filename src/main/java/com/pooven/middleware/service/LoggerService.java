package com.pooven.middleware.service;

import java.util.Map;
import java.util.logging.Level;

public class LoggerService {

	public LogBuilder builder(String msg) {
		return new LogBuilder(msg);
	}

	public class LogBuilder {

		LogBuilder(String msg) {
			this.msg = msg;
		}

		String msg;

		Map<String, Object> dataMap;

		Level level;

		public LogBuilder data(Map<String, Object> dataMap) {
			this.dataMap = dataMap;
			return this;
		}

		public LogBuilder level(Level level) {
			this.level = level;
			return this;
		}

		public void log() {
			System.out.println(msg);
		}

	}

}
