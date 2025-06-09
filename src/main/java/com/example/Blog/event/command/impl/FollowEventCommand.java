package com.example.Blog.event.command.impl;

import com.example.Blog.enums.NotificationMessage;
import com.example.Blog.event.EventBuffer;
import com.example.Blog.event.EventForAuthor;
import com.example.Blog.event.command.EventCommand;
import com.example.Blog.model.User;
import com.example.Blog.service.NotificationService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class FollowEventCommand implements EventCommand {

    private final EventBuffer eventBuffer;
    private final NotificationService notificationService;

    public FollowEventCommand(EventBuffer eventBuffer, NotificationService notificationService) {
        this.eventBuffer = eventBuffer;
        this.notificationService = notificationService;
    }


    @Override
    public void execute() {
        Map<Long, List<EventForAuthor>> eventsMap = eventBuffer.getAllBufferedFollows();
        for (Map.Entry<Long, List<EventForAuthor>> entry : eventsMap.entrySet()) {
            List<EventForAuthor> events = entry.getValue();
            if (events.isEmpty()) continue;
            User recipient = events.getFirst().recipient();
            if (ObjectUtils.isEmpty(recipient)) continue;
            User mainUser = events.getFirst().user();
            String message = (events.size() == 1)
                    ? NotificationMessage.FOLLOW_YOU.formatMessage(mainUser.getName())
                    : NotificationMessage.GROUP_FOLLOW_YOU.formatMessage(mainUser.getName(), events.size() - 1);
            notificationService.sendNotification(recipient, message, null);
            eventBuffer.flushFollows(Long.valueOf(recipient.getId()));
        }
    }
}
