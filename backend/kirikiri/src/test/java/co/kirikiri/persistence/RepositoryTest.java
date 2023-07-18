package co.kirikiri.persistence;

import co.kirikiri.common.config.JpaConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

@Import(JpaConfig.class)
@DataJpaTest
@TestConstructor(autowireMode = AutowireMode.ALL)
public class RepositoryTest {

}
