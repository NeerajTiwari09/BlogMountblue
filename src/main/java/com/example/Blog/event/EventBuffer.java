package com.example.Blog.event;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EventBuffer {
    private final Map<Long, List<EventForAuthor>> likeBuffer = new ConcurrentHashMap<>();
    private final Map<Long, List<EventForAuthor>> commentBuffer = new ConcurrentHashMap<>();

    public void bufferLike(EventForAuthor event) {
        likeBuffer.computeIfAbsent(Long.valueOf(event.getPost().getId()),
                k -> Collections.synchronizedList(new ArrayList<>())).add(event);
    }

    public List<EventForAuthor> flushLikes(Long postId) {
        return likeBuffer.remove(postId);
    }

    public Map<Long, List<EventForAuthor>> getAllBufferedLikes() {
        return new HashMap<>(likeBuffer);
    }

    public void bufferComments(EventForAuthor event) {
        commentBuffer.computeIfAbsent(Long.valueOf(event.getPost().getId()),
                k -> Collections.synchronizedList(new ArrayList<>())).add(event);
    }

    public List<EventForAuthor> flushComments(Long postId) {
        return commentBuffer.remove(postId);
    }

    public Map<Long, List<EventForAuthor>> getAllBufferedComments() {
        return new HashMap<>(commentBuffer);
    }
}
