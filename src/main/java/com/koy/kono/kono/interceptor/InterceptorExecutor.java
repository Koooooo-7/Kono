package com.koy.kono.kono.interceptor;

import com.koy.kono.kono.core.RequestContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description the interceptor executor, which will exec the interceptors chain.
 */

public enum InterceptorExecutor {

    PRE, POST;

    private LinkedList<IInterceptor> registerInterceptors = new LinkedList<>();

    // TODO: the life circle should be more, it will retain in all application life, not just thread
    private static final ThreadLocal<List<IInterceptor>> matchedIInterceptorsCache = new ThreadLocal<>();

    public boolean doInterceptor(Object[] args) {

        // default true
        LinkedList<IInterceptor> registerInterceptors = getRegisterInterceptors();
        if (Objects.isNull(registerInterceptors) || registerInterceptors.isEmpty()) {
            return true;
        }
        switch (this) {
            case PRE:
                return execute(((RequestContext) args[0]).getRequest(), ((RequestContext) args[0]), IInterceptor::preHandle);
            case POST:
                return execute(null, (FullHttpResponse) args[0], IInterceptor::postHandle);
            default:
                return false;
        }
    }

    public <T> boolean execute(FullHttpRequest request, T target, Function<? super IInterceptor, ? extends Predicate<T>> mapper) {
        // TODO: change url to Router
        List<IInterceptor> matchedInterceptors = getMatchedInterceptors(request);
        if (matchedInterceptors.isEmpty()) {
            return true;
        }
        LinkedList<Predicate<T>> predicates = matchedInterceptors.stream().map(mapper).collect(Collectors.toCollection(LinkedList::new));
        Predicate<T> interceptorPredicateChain = getPredicateChain(predicates, 0);
        if (this == POST) {
            matchedIInterceptorsCache.remove();
        }
        return interceptorPredicateChain.test(target);
    }

    // TODO: change to reduce
    // translate list to predicate chain
    public <T> Predicate<T> getPredicateChain(LinkedList<Predicate<T>> interceptors, int index) {
        if (index > interceptors.size() - 1) {
            return e -> true;
        }
        Predicate<T> interceptorPredicate = interceptors.get(index);
        return interceptorPredicate.and(getPredicateChain(interceptors, ++index));
    }

    private List<IInterceptor> getMatchedInterceptors(FullHttpRequest request) {
        // if request == null, there should have cache already, put by the request interceptor.
        if (Objects.isNull(matchedIInterceptorsCache.get())) {
            // TODO: change url to Router
            String uri = request.uri();
            List<IInterceptor> matchedInterceptors = this.registerInterceptors.stream()
                    .filter(interceptor -> interceptor.isMatchPathPatterns(uri))
                    .sorted(Comparator.comparingInt(IInterceptor::order))
                    .collect(Collectors.toList());
            matchedIInterceptorsCache.set(matchedInterceptors);
        }
        return matchedIInterceptorsCache.get();
    }

    // register all interceptors on pre
    public void addRegisterInterceptors(IInterceptor interceptor) {
        this.registerInterceptors.add(interceptor);
    }

    // register interceptors on pre, so get from it also
    private LinkedList<IInterceptor> getRegisterInterceptors() {
        return InterceptorExecutor.PRE.registerInterceptors;
    }

}
