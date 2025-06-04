package com.example.Blog.constant;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final Map<String, String> ORDER_BY = new HashMap<String, String>() {{
        put("Asc", "asc");
        put("Desc", "desc");
    }};
    public static final Map<String, String> SORT_BY = new HashMap<String, String>() {{
        put("Date", "publishedAt");
        put("Author", "author");
        put("Title", "title");
    }};

    public static final String CLEAR_ALL = "ALL";
}
