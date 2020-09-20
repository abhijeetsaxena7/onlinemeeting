package com.libsys.onlinemeeting.config.vendor.zoom.sdk;

import java.net.URL;

public interface RequestBuilder<T> {
    RequestBuilder<T> queryParam(String key, String value);
    RequestBuilder<T> path(String path);
    RequestBuilder<T> addHeader(String name,String value);
    T post(T body);
    T put(T body);
    T get();
    void delete();
    T patch(T body);
}

