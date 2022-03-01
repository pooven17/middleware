package com.pooven.middleware.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableScheduling
public class MiddlewareBatch {

	public static void main(String[] args) {
		readFile();
	}

	@Scheduled(cron = "30 * * * * *")
	public void batch() {
		log.info("*****************batch*****************" + LocalDate.now());
		readFile();
	}

	private static void readFile() {
		try {
			log.info("*****************input*****************");
			File file = new File("D:\\work\\0206\\input.txt");
			List<String> outputLineList = new ArrayList<>();
			try (InputStream in = new FileInputStream(file)) {
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String inputLine;
				int lineNumber = 0;
				while ((inputLine = br.readLine()) != null) {
					lineNumber++;
					List<String> inputColnValues = Stream.of(inputLine.split(",", -1)).collect(Collectors.toList());
					List<String> outputColnValues = new ArrayList<>();
					outputColnValues.add(inputColnValues.get(1));
					outputColnValues.add(inputColnValues.get(3));
					outputColnValues.add(inputColnValues.get(5));
					outputColnValues.add(inputColnValues.get(7));
					outputColnValues.add(inputColnValues.get(9));
					outputLineList.add(String.join("|", outputColnValues));
				}
				log.info("*****************Number of lines*****************"+ lineNumber);
			}
			log.info("*****************output*****************");
			Files.write(new File("D:\\work\\0206\\output1.txt").toPath(), outputLineList, Charset.defaultCharset());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}