package com.example.Blog.service.impl;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPT = 3;
    private static final int BLOCK_MINUTES = 5;

    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> blockTimeCache = new ConcurrentHashMap<>();

    public void loginFailed(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0);
        attempts++;
        attemptsCache.put(username, attempts);

        if (attempts >= MAX_ATTEMPT) {
            blockTimeCache.put(username, LocalDateTime.now().plusMinutes(BLOCK_MINUTES));
        }
    }

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        blockTimeCache.remove(username);
    }

    public boolean isBlocked(String username) {
        if (!blockTimeCache.containsKey(username)) return false;

        LocalDateTime unblockTime = blockTimeCache.get(username);
        if (LocalDateTime.now().isAfter(unblockTime)) {
            blockTimeCache.remove(username);
            attemptsCache.remove(username);
            return false;
        }
        return true;
    }
}
