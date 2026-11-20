package com.noljo.nolzo.support.annotation;

import com.noljo.nolzo.support.DatabaseCleanerExtension;
import com.noljo.nolzo.support.RedisCleanerExtension;
import com.noljo.nolzo.support.config.RedisTestcontainersConfig;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(RedisTestcontainersConfig.class)
@Retention(RetentionPolicy.RUNTIME)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith({
        DatabaseCleanerExtension.class,
        RedisCleanerExtension.class
})
public @interface ServiceTest {
}
