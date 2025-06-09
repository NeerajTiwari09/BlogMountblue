package com.example.Blog.event.command.impl;

import com.example.Blog.constant.NotificationUrl;
import com.example.Blog.enums.NotificationMessage;
import com.example.Blog.event.EventBuffer;
import com.example.Blog.event.EventForAuthor;
import com.example.Blog.event.command.EventCommand;
import com.example.Blog.model.Post;
import com.example.Blog.model.User;
import com.example.Blog.service.NotificationService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.example.Blog.constant.NotificationUrl.BLOG_URL_PATTERN;

@Component
public class LikeEventCommand implements EventCommand {

    private final EventBuffer eventBuffer;
    private final NotificationService notificationService;

    public LikeEventCommand(EventBuffer eventBuffer, NotificationService notificationService) {
        this.eventBuffer = eventBuffer;
        this.notificationService = notificationService;
    }

    @Override
    public void execute() {
        Map<Long, List<EventForAuthor>> eventsMap = eventBuffer.getAllBufferedLikes();
        for (Map.Entry<Long, List<EventForAuthor>> entry : eventsMap.entrySet()) {
            List<EventForAuthor> events = entry.getValue();
            if (events.isEmpty()) continue;
            User recipient = events.getFirst().recipient();
            Post post = events.getFirst().post();
            if (ObjectUtils.anyNull(recipient, post)) continue;
            User mainUser = events.getFirst().user();
            String message = (events.size() == 1)
                    ? NotificationMessage.LIKE_BLOG.formatMessage(mainUser.getName(), post.getTitle())
                    : NotificationMessage.GROUP_LIKE_BLOG.formatMessage(mainUser.getName(), events.size() - 1, post.getTitle());

            String notificationUrl = NotificationUrl.getUrl(BLOG_URL_PATTERN, post.getId());
            notificationService.sendNotification(recipient, message, notificationUrl);
            eventBuffer.flushLikes(Long.valueOf(post.getId()));
        }
    }
}
