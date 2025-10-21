package com.griddynamics.gridu.pbazhko.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class MongoDBTestContainerConfig {

    private static final String IMAGE = "mongo:6.0.13";

    private static final MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse(IMAGE))
                    .waitingFor(Wait.forListeningPort());

    static {
        if (!mongoDBContainer.isRunning()) {
            mongoDBContainer.start();
        }
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    configurableApplicationContext,
                    "spring.data.mongodb.uri=" + mongoDBContainer.getReplicaSetUrl()
            );
        }
    }
}
