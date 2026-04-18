package edu.icet.ecom.listener;

import edu.icet.ecom.event.BookingCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationListener {

    // 1. @EventListener means "Listen for anyone shouting 'BookingCreatedEvent'"
    // 2. @Async tells Spring to hand this to our specific custom thread pool
    @EventListener
    @Async("backgroundTaskExecutor")
    public void handleBookingCreated(BookingCreatedEvent event) {

        log.info("STARTING ASYNC JOB: Preparing to send confirmation email to {} for Booking {}",
                event.customerEmail(), event.bookingId());

        try {
            // Simulating a slow, 5-second process (like generating a PDF and calling Gmail's API)
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            log.error("Async thread was interrupted!", e);
        }

        log.info("FINISHED ASYNC JOB: Email successfully sent to {}!", event.customerEmail());
    }
}