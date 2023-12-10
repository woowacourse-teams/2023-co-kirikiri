package co.kirikiri.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class CacheKeyGeneratorTest {

    private final CacheKeyGenerator cacheKeyGenerator = new CacheKeyGenerator();

    @Test
    void 정상적으로_키를_생성한다() throws NoSuchMethodException {
        //given
        final Class<?> targetClass = Object.class;
        final Method method = targetClass.getMethod("toString");
        final Object[] params = new Object[]{"param1", null, 123};

        //when
        final String key = (String) cacheKeyGenerator.generate(targetClass, method, params);

        //then
        assertThat(key).contains("Class", "toString");
        for (final Object param : params) {
            if (param == null) {
                continue;
            }
            assertThat(key).contains(String.valueOf(param.hashCode()));
        }
    }
}
