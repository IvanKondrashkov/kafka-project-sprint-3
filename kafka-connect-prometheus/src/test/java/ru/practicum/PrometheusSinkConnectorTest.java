package ru.practicum;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.KafkaContainer;
import ru.practicum.connector.PrometheusSinkConnector;
import ru.practicum.connector.PrometheusSinkTask;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class PrometheusSinkConnectorTest {
    @Container
    public static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.9.0")
    );

    private PrometheusSinkConnector connector;
    private Map<String, String> props;

    @BeforeEach
    void setUp() {
        connector = new PrometheusSinkConnector();

        props = new HashMap<>();
        props.put("prometheus.listener.url", "http://localhost");
        props.put("prometheus.listener.port", "8084");
    }

    @Test
    void start() {
        connector.start(props);

        List<Map<String, String>> taskConfigs = connector.taskConfigs(2);
        assertThat(taskConfigs).hasSize(2);
        assertThat(taskConfigs.get(0)).isEqualTo(props);
        assertThat(taskConfigs.get(1)).isEqualTo(props);
    }

    @Test
    void taskClass() {
        assertThat(connector.taskClass()).isEqualTo(PrometheusSinkTask.class);
    }

    @Test
    void config() {
        assertThat(connector.config()).isNotNull();
        assertThat(connector.config().configKeys()).containsKeys(
                "prometheus.listener.url",
                "prometheus.listener.port"
        );
    }

    @Test
    void version() {
        assertThat(connector.version()).isNotNull();
    }
}