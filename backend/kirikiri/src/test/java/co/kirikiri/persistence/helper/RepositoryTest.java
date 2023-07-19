package co.kirikiri.persistence.helper;

import co.kirikiri.common.config.JpaConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@DataJpaTest
@ActiveProfiles("test")
@Import({JpaConfig.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@TestConstructor(autowireMode = AutowireMode.ALL)
public @interface RepositoryTest {

}
