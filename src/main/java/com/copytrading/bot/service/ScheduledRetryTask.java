package com.copytrading.bot.service;

import com.copytrading.bot.logging.LoggingService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
public class ScheduledRetryTask {

    private final NewOrderService orderService;
    private final LoggingService logService;

    public ScheduledRetryTask(NewOrderService orderService, LoggingService logService) {
        this.orderService = orderService;
        this.logService = logService;
    }

//    @Async
//    @Scheduled(fixedRate = 20000)
//    public void scheduleFixedRateTaskAsync() throws InterruptedException {
//        logService.log("Managing retries");
//        orderService.manageOrdersToRetry();
//    }

}
