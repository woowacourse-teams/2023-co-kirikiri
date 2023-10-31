package co.kirikiri.persistence.helper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@DataRedisTest
@ActiveProfiles("test")
@Import({RedisTestContainer.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@TestConstructor(autowireMode = AutowireMode.ALL)
public @interface RedisRepositoryTest {

}
