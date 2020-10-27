package com.koy.kono.kono.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.internal.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description The request context is occurred in each request happens and removed when it return back.
 */

public class RequestContext {

    public static final String POST_JSON_OBJECT_KEY = "kono.post.json.object";
    public static final String POST_STRING_KEY = "kono.post.string";


    private FullHttpRequest request;

    private ApplicationContext applicationContext;

    private HttpMethod methodType;

    private String url;

    private Map<String, Object> paramsCache;

    public RequestContext(FullHttpRequest request, ApplicationContext ctx) {
        this.request = request;
        this.applicationContext = ctx;
        this.methodType = request.method();
        this.url = request.uri();
    }


    public static class Builder {
        private FullHttpRequest request;
        private ApplicationContext ctx;

        private HttpMethod methodType;

        private String url;

        public Builder setRequest(FullHttpRequest request, ApplicationContext applicationContext) {
            this.request = request;
            this.ctx = applicationContext;
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
            return new RequestContext(this.request, ctx);
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

    @SuppressWarnings("unchecked")
    public <T> Optional<T> post() {
        if (methodType != HttpMethod.POST) {
            return Optional.empty();
        }
        return Optional.ofNullable((T) this.getPostParamsCache().get(POST_JSON_OBJECT_KEY));
    }

    public <T> Optional<T> post(String paramsName) {
        if (methodType != HttpMethod.POST) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.getPostParam(paramsName));
    }

    public <T> Optional<T> post(Class<T> clz) {
        if (methodType != HttpMethod.POST) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(this.getPostParam(clz));
        } catch (Exception e) {
            throw new ClassCastException("can not cast post data to specific class type");
        }

    }

    public <T> Optional<T> post(String paramsName, Function<T, T> func) {
        Optional<T> o = get(paramsName);
        o.ifPresent(func::apply);
        return o;
    }


    @SuppressWarnings("unchecked")
    private <T> T getPostParam(String paramsName) {
        return (T) ((JSONObject) this.getPostParamsCache().get(POST_JSON_OBJECT_KEY)).get(paramsName);
    }

    private <T> T getPostParam(Class<T> clz) {
        String postString = (String) this.getPostParamsCache().get(POST_STRING_KEY);
        return JSON.parseObject(postString, clz);
    }

    @SuppressWarnings("unchecked")
    private <T> T getGetParam(String paramsName) {
        return (T) this.getGetParamsCache().get(paramsName);
    }

    private Map<String, Object> getGetParamsCache() {
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

    private Map<String, Object> getPostParamsCache() {
        if (this.paramsCache == null) {
            synchronized (this) {
                if (this.paramsCache == null) {
                    try {
                        this.paramsCache = new HashMap<>();
                        ByteBuf content = this.request.content();
                        String postString = content.toString(StandardCharsets.UTF_8);
                        this.paramsCache.put(POST_STRING_KEY, postString);
                        JSONObject postJsonObject = JSONObject.parseObject(postString);
                        this.paramsCache.put(POST_JSON_OBJECT_KEY, postJsonObject);
                    } catch (Exception e) {
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


    public <T> Response<T> send(T data) {
        return new Response<T>(this, data);
    }


    public <T> Response.Builder<T> sendBuilder(T data) {
        Response<T> response = new Response<T>(this, data);
        return new Response.Builder<T>(response);
    }

    public static class Response<T> {

        private FullHttpResponse response;
        private RequestContext requestContext;
        private DataWrapper<T> dataWrapper;

        public Response(RequestContext ctx, T data) {
            this.requestContext = ctx;
            this.dataWrapper = new DataWrapper<T>(data);
            new Builder<T>(this).build();
        }

        public Response<T> setResponse(FullHttpResponse response) {
            this.response = response;
            return this;
        }

        public DataWrapper<T> getDataWrapper() {
            return dataWrapper;
        }

        public FullHttpResponse getResponse() {
            return response;
        }

        public void json() {
            this.requestContext.applicationContext.out(response);
        }


        public static class Builder<T> {
            private Response<T> response;
            private HttpVersion httpVersion = HttpVersion.HTTP_1_1;
            private HttpResponseStatus responseStatus = HttpResponseStatus.OK;
            private HttpHeaders httpHeaders = new DefaultHttpHeaders().set("Content-Type", "application/json");
            private boolean validateHeaders = true;
            private boolean singleFieldHeaders = false;
            private HttpHeaders trailingHeaders = singleFieldHeaders ? new CombinedHttpHeaders(validateHeaders)
                    : new DefaultHttpHeaders(validateHeaders);

            public Builder(Response<T> response) {
                this.response = response;
            }

//            private Builder setHttpVersion(HttpVersion httpVersion) {
//                this.httpVersion = httpVersion;
//                return this;
//            }

            public Builder<T> setHttpResponseStatus(HttpResponseStatus status) {
                this.responseStatus = status;
                return this;
            }

            public Builder<T> setHttpHeaders(HttpHeaders httpHeaders) {
                this.httpHeaders = httpHeaders;
                return this;
            }

            public Response<T> build() {
                // make sure it is ["Content-Type", "application/json"]
                this.httpHeaders.set("Content-Type", "application/json");
                return this.response.setResponse(
                        new DefaultFullHttpResponse(httpVersion, responseStatus
                                , Unpooled.wrappedBuffer(response.getDataWrapper().getData()), httpHeaders, trailingHeaders));
            }

        }


        static class DataWrapper<V> {
            private V data;

            public DataWrapper(V data) {
                this.data = data;
            }

            public byte[] getData() {
                if (Objects.isNull(data)) {
                    return "".getBytes();
                }
                String jsonData = JSON.toJSONString(data);
                return jsonData.getBytes();
            }
        }

    }

    public FullHttpRequest getRequest() {
        return request;
    }
}
