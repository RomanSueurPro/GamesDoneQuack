package com.quackinduckstries.gamesdonequack.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.quackinduckstries.gamesdonequack.services.UserService;

@Component
public class ScheduledTasks {

	private final UserService userService;
	
	public ScheduledTasks(UserService userService) {
		this.userService = userService;
	}
	
	@Scheduled(cron = "0 0 0 * * *")
	private void RemoveDeletedAccounts() {
		System.out.println("Scheduler works !");
		this.userService.ScheduledUserDelete();
	}
}
