package co.kirikiri.persistence.helper;

import java.util.Objects;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@DataRedisTest
@ActiveProfiles("test")
@Import({RedisTestContainer.class})
public class RedisTest {

    @Autowired
    protected RedisTemplate<String, String> redisTemplate;

    @AfterEach
    public void cleanUp() {
        Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection()
                .serverCommands()
                .flushDb();
    }
}
