package ru.iunusov.timetable.scheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SchedulingApplication {

	public static void main(String[] args) {
		try (var context = SpringApplication.run(SchedulingApplication.class, args)) {
			context.isActive();
		}
	}

}
