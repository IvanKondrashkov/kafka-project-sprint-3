package ru.practicum.config;

import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.AbstractConfig;

public class PrometheusSinkConnectorConfig extends AbstractConfig {
    public static final String PROMETHEUS_SERVER_URL = "prometheus.listener.url";
    public static final String PROMETHEUS_SERVER_URL_DOC = "URL сервера Prometheus";
    public static final String PROMETHEUS_SERVER_PORT = "prometheus.listener.port";
    public static final String PROMETHEUS_SERVER_PORT_DOC = "Порт сервера Prometheus";
    private static final int PROMETHEUS_SERVER_PORT_DEFAULT = 8084;

    public static ConfigDef config() {
        return new ConfigDef()
                .define(PROMETHEUS_SERVER_URL, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, PROMETHEUS_SERVER_URL_DOC)
                .define(PROMETHEUS_SERVER_PORT, ConfigDef.Type.INT, PROMETHEUS_SERVER_PORT_DEFAULT, ConfigDef.Importance.HIGH, PROMETHEUS_SERVER_PORT_DOC);
    }

    public PrometheusSinkConnectorConfig(Map<String, String> props) {
        super(config(), props);
    }

    public String getPrometheusServerUrl() {
        return this.getString(PROMETHEUS_SERVER_URL);
    }

    public int getPrometheusServerPort() {
        return this.getInt(PROMETHEUS_SERVER_PORT);
    }
}