import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventScheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public EventScheduler(EventCRUDApp eventCRUDApp) {
        try {
            scheduler.scheduleAtFixedRate(eventCRUDApp::sendNotifications, 0, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
