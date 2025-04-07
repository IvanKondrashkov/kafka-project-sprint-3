package ru.practicum.connector;

import java.util.Map;
import java.util.Collection;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Counter;
import java.net.InetSocketAddress;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.HTTPServer;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import ru.practicum.dto.MetricEvent;
import org.apache.kafka.connect.sink.SinkTask;
import org.apache.kafka.connect.sink.SinkRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.config.PrometheusSinkConnectorConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import ru.practicum.util.Versions;
import java.io.IOException;

@Slf4j
@NoArgsConstructor
public class PrometheusSinkTask extends SinkTask {
    private PrometheusSinkConnectorConfig config;
    private PrometheusMeterRegistry meterRegistry;
    private InetSocketAddress inetSocketAddress;
    private HTTPServer server;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public PrometheusSinkTask(PrometheusSinkConnectorConfig config, PrometheusMeterRegistry meterRegistry, InetSocketAddress inetSocketAddress) {
        this.config = config;
        this.meterRegistry = meterRegistry;
        this.inetSocketAddress = inetSocketAddress;
    }

    @Override
    public String version() {
        return Versions.getVersions();
    }

    @Override
    public void start(Map<String, String> props) {
        config = new PrometheusSinkConnectorConfig(props);
        meterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        inetSocketAddress = new InetSocketAddress(config.getPrometheusServerPort());
        server = createServer(inetSocketAddress, meterRegistry.getPrometheusRegistry());

        log.info("Start prometheus task on url={}, port={}", config.getPrometheusServerUrl(), config.getPrometheusServerPort());
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        for (SinkRecord record : records) {
            try {
                log.info("Record parse start {}", record.value().toString());
                Map<String, MetricEvent> metrics = MAPPER.readValue(
                        record.value().toString(),
                        MAPPER.getTypeFactory().constructMapType(Map.class, String.class, MetricEvent.class)
                );

                metrics.forEach((name, metric) -> {
                    log.info("Registering metric: name={}, type={}, value={}", metric.getName(), metric.getType(), metric.getValue());
                    switch (metric.getType().toLowerCase()) {
                        case "gauge" -> Gauge.builder(metric.getName(), metric::getValue)
                                .description(metric.getDescription())
                                .register(meterRegistry);
                        case "counter" -> {
                            Counter counter = Counter.builder(metric.getName())
                                    .description(metric.getDescription())
                                    .register(meterRegistry);
                            counter.increment(metric.getValue());
                        }
                        default -> log.warn("Unknown type: {}", metric.getType());
                    }
                });

                meterRegistry.getMeters().forEach(meter -> log.info("Current meter: {}", meter.getId()));
            } catch (JsonProcessingException e) {
                log.error("Failed parse prometheus metric", e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            server.close();

            log.info("Stop prometheus task on url={}, port={}", config.getPrometheusServerUrl(), config.getPrometheusServerPort());
        }
    }

    private HTTPServer createServer(InetSocketAddress inetSocketAddress, CollectorRegistry collectorRegistry) {
        try {
            return new HTTPServer(inetSocketAddress, collectorRegistry, true);
        } catch (IOException e) {
            log.error("Failed to stop prometheus server", e);
            throw new RuntimeException(e);
        }
    }
}