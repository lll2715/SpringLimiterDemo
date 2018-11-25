package site.higgs.limiterdemo;

import site.higgs.limiter.interceptor.LimiterFallbackResolver;

import java.lang.reflect.Method;

public class BusyFallbackResolver implements LimiterFallbackResolver<ResponseMessage> {
    @Override
    public ResponseMessage resolve(Method method, Class<?> aClass, Object[] objects, String s) {
        return ResponseMessage.error("服务繁忙");
    }
}
