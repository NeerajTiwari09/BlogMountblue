package com.example.Blog.controller;

import com.example.Blog.dto.output_dto.Response;
import com.example.Blog.enums.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.example.Blog.constant.Constants.CLEAR_ALL;

@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
public class HazelcastCacheController {

    private final HazelcastInstance hazelcastInstance;
    private static final ObjectMapper mapper = new ObjectMapper();

    @GetMapping(value = {"/get", "/get/{key}"})
    public Response<Object> getCacheValues(@PathVariable(value = "key", required = false) String key) {
        Map<Object, Object> response = new HashMap<>();
        if (StringUtils.hasText(key)) {
            IMap<Object, Object> map = hazelcastInstance.getMap(key);
            response.put(key, (map.get(key)));
        } else {
            for (DistributedObject distributedObjects : hazelcastInstance.getDistributedObjects()) {
                IMap<Object, Object> value = hazelcastInstance.getMap(distributedObjects.getName());
                response.put(distributedObjects.getName(), value);
            }
        }
        return new Response<>(response);
    }

    @GetMapping("/clear/{key}")
    public Response<Object> clearCacheValues(@RequestParam(value = "key", required = false, defaultValue = "") String key) {
        IMap<Object, Object> map = hazelcastInstance.getMap("users");
        Map<Object, Object> response = new HashMap<>();
        try {
            if (!StringUtils.hasText(key)) {
                return new Response<>(ErrorCode.NO_KEY_SELECTED_TO_CLEAR);
            }
            if (CLEAR_ALL.equals(key)) {
                Set<Map.Entry<Object, Object>> entrySet = map.entrySet();
                for (Map.Entry<Object, Object> entry : entrySet) {
                    response.put(entry.getKey(), mapper.writeValueAsString(entry.getValue()));
                }
                map.clear();
            } else {
                response.put(key, mapper.writeValueAsString(map.get(key)));
                map.remove(key);
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response<>(ErrorCode.RUNTIME_EXCEPTION);
        }
        return new Response<>(response);
    }
}
