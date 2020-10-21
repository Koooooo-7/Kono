package com.koy.kono.kono.core;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.internal.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class RequestContext {

    private FullHttpRequest request;

    private FullHttpResponse response;

    private HttpMethod methodType;

    private String url;

    private Map<String, Object> paramsCache;

    public RequestContext(FullHttpRequest request) {
        this.request = request;
        this.methodType = request.method();
        this.url = request.uri();
    }


    public static class Builder {
        private FullHttpRequest request;

        private HttpMethod methodType;

        private String url;

        public Builder setRequest(FullHttpRequest request) {
            this.request = request;
            return this;
        }

        public Builder setRequestMethodType(HttpMethod methodType) {
            this.methodType = methodType;
            return this;
        }

        public Builder setRequestUrl(String url) {
            this.url = url;
            return this;
        }

        public RequestContext build() {
            return new RequestContext(this.request);
        }
    }


    public <T> Optional<T> get(String paramsName) {
        if (methodType != HttpMethod.GET) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.getGetParam(paramsName));
    }

    public <T> Optional<T> get(String paramsName, Function<T, T> func) {
        Optional<T> o = get(paramsName);
        o.ifPresent(func::apply);
        return o;
    }

    public <T> Optional<T> post(String paramsName) {
        return Optional.empty();
    }

    public <T> Optional<T> post(String paramsName, Function<T, T> func) {
        Optional<T> o = get(paramsName);
        o.ifPresent(func::apply);
        return o;
    }


    @SuppressWarnings("unchecked")
    private <T> T getGetParam(String paramsName) {
        return (T) this.getParamsCache().get(paramsName);
    }

    private Map<String, Object> getParamsCache() {
        if (this.paramsCache == null) {
            synchronized (this) {
                if (this.paramsCache == null) {
                    try {
                        this.paramsCache = new HashMap<>();

                        int index = this.url.indexOf('?');
                        if (index == -1) {
                            return this.paramsCache;
                        }

                        String params = this.url.substring(index + 1);
                        if (StringUtil.isNullOrEmpty(params)) {
                            return this.paramsCache;
                        }
                        params = URLDecoder.decode(params, "utf-8");

                        String[] dict = params.split("&");
                        for (String kv : dict) {
                            String[] split = kv.split("=");
                            if (split.length != 2) {
                                continue;
                            }
                            String k = split[0];
                            String v = split[1];
                            paramsCache.put(k, v);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return paramsCache;
                }
            }
        }
        return this.paramsCache;
    }

    public boolean isGet() {
        return this.methodType == HttpMethod.GET;
    }

    public boolean isPost() {
        return this.methodType == HttpMethod.POST;
    }


    public Response send() {
        return new Response(response);
    }

    // TODO
    public Response sendBuilder() {
        return null;
    }

    public static class Response {

        private FullHttpResponse response;

        public Response(FullHttpResponse response) {
            this.response = response;
        }

        public <T> Response json(T data) {
//            response.
            return this;
        }

    }

    public FullHttpRequest getRequest() {
        return request;
    }
}
