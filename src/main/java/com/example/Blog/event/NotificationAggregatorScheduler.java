package com.example.Blog.event;

import com.example.Blog.event.command.EventCommand;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationAggregatorScheduler {

    private final List<EventCommand> eventCommands;

    public NotificationAggregatorScheduler(List<EventCommand> eventCommands) {
        this.eventCommands = eventCommands;
    }


    @Scheduled(fixedRate = 60000)
    public void runAllCommands() {
        for (EventCommand command : eventCommands) {
            new Thread(command::execute).start();
        }
    }

}
