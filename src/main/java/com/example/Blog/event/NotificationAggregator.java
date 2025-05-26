package com.example.Blog.event;

import com.example.Blog.constant.NotificationUrl;
import com.example.Blog.enums.NotificationMessage;
import com.example.Blog.model.Post;
import com.example.Blog.model.User;
import com.example.Blog.repository.NotificationRepository;
import com.example.Blog.repository.UserRepository;
import com.example.Blog.service.NotificationService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.example.Blog.constant.NotificationUrl.BLOG_URL_PATTERN;

@Service
public class NotificationAggregator {

    @Autowired
    private EventBuffer eventBuffer;
    @Autowired
    private NotificationRepository notificationRepo;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;


    @Scheduled(fixedRate = 60000)
    public void processBufferedLikes() {
        processLikeEvents();
        processCommentEvents();
    }

    private void processLikeEvents() {
        Map<Long, List<EventForAuthor>> eventsMap = eventBuffer.getAllBufferedLikes();
        for (Map.Entry<Long, List<EventForAuthor>> entry : eventsMap.entrySet()) {
            List<EventForAuthor> events = entry.getValue();
            if (events.isEmpty()) continue;
            User recipient = events.get(0).getPostOwner();
            Post post = events.get(0).getPost();
            if (ObjectUtils.anyNull(recipient, post)) continue;
            User mainUser = events.get(0).getUser();
            String message = (events.size() == 1)
                    ? NotificationMessage.LIKE_BLOG.formatMessage(mainUser.getName(), post.getTitle())
                    : NotificationMessage.GROUP_LIKE_BLOG.formatMessage(mainUser.getName(), events.size() - 1, post.getTitle());

            String notificationUrl = NotificationUrl.getUrl(BLOG_URL_PATTERN, post.getId());
            notificationService.sendNotification(recipient, message, notificationUrl);
            eventBuffer.flushLikes(Long.valueOf(post.getId()));
        }
    }

    private void processCommentEvents() {
        Map<Long, List<EventForAuthor>> eventsMap = eventBuffer.getAllBufferedComments();
        for (Map.Entry<Long, List<EventForAuthor>> entry : eventsMap.entrySet()) {
            List<EventForAuthor> events = entry.getValue();
            if (events.isEmpty()) continue;
            User recipient = events.get(0).getPostOwner();
            Post post = events.get(0).getPost();
            if (ObjectUtils.anyNull(recipient, post)) continue;
            User mainUser = events.get(0).getUser();
            String message = (events.size() == 1)
                    ? NotificationMessage.COMMENT_ON_BLOG.formatMessage(mainUser.getName(), post.getTitle())
                    : NotificationMessage.GROUP_COMMENT_ON_BLOG.formatMessage(mainUser.getName(), events.size() - 1, post.getTitle());
            String notificationUrl = NotificationUrl.getUrl(BLOG_URL_PATTERN, post.getId());
            notificationService.sendNotification(recipient, message, notificationUrl);
            eventBuffer.flushComments(Long.valueOf(post.getId()));
        }
    }
}
