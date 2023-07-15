package co.kirikiri.persistence.helper;

import co.kirikiri.common.config.JpaConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@DataJpaTest
@ActiveProfiles("test")
@Import({TestEntityManagerConfig.class, JpaConfig.class})
@TestConstructor(autowireMode = AutowireMode.ALL)
public class RepositoryTest {

}
