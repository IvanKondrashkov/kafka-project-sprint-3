package ru.practicum;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import java.net.InetSocketAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.apache.kafka.connect.sink.SinkRecord;
import ru.practicum.connector.PrometheusSinkTask;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import ru.practicum.config.PrometheusSinkConnectorConfig;
import static org.assertj.core.api.Assertions.assertThat;

public class PrometheusSinkTaskTest {
    private PrometheusSinkTask task;
    private PrometheusMeterRegistry meterRegistry;
    private InetSocketAddress inetSocketAddress;
    private Map<String, String> props;

    @BeforeEach
    void setUp() {
        props = new HashMap<>();
        props.put("prometheus.listener.url", "http://localhost");
        props.put("prometheus.listener.port", "8084");

        PrometheusSinkConnectorConfig config = new PrometheusSinkConnectorConfig(props);
        meterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        inetSocketAddress = new InetSocketAddress(config.getPrometheusServerPort());
        task = new PrometheusSinkTask(config, meterRegistry, inetSocketAddress);
    }

    @Test
    void version() {
        assertThat(task.version()).isNotNull();
    }

    @Test
    void start() {
        task.start(props);

        assertThat(inetSocketAddress.isUnresolved()).isFalse();
    }

    @Test
    void put() {
        String json = "{\"Alloc\":{\"Type\":\"gauge\",\"Name\":\"Alloc\",\"Description\":\"Alloc is bytes of allocated heap objects.\",\"Value\":24293912},\"PollCount\":{\"Type\":\"counter\",\"Name\":\"PollCount\",\"Description\":\"PollCount is quantity of metrics collection iteration.\",\"Value\":3}}";

        SinkRecord record = new SinkRecord("test-topic", 0, null, null, null, json, 0);
        Collection<SinkRecord> records = Collections.singletonList(record);

        task.put(records);
        assertThat(meterRegistry.find("Alloc").gauge()).isNotNull();
        assertThat(meterRegistry.find("Alloc").gauge().value()).isEqualTo(24293912.0);

        assertThat(meterRegistry.find("PollCount").counter()).isNotNull();
        assertThat(meterRegistry.find("PollCount").counter().count()).isEqualTo(3.0);
    }
}