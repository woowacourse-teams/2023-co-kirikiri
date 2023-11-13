package co.kirikiri.service;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

@Component
public class CacheKeyGenerator implements KeyGenerator {

    private static final String DELIMITER = "_";

    @Override
    public Object generate(final Object target, final Method method, final Object... params) {
        return String.format("%s-%s-%s",
                target.getClass().getSimpleName(),
                method.getName(),
                arrayToDelimitedString(params));
    }

    private Object arrayToDelimitedString(final Object... params) {
        return Arrays.stream(params)
                .map(o -> o == null ? "" : String.valueOf(o.hashCode()))
                .collect(Collectors.joining(DELIMITER));
    }
}
