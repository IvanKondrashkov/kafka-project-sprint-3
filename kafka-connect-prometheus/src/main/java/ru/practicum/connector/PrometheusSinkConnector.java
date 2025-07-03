package ru.practicum.connector;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.sink.SinkConnector;
import ru.practicum.config.PrometheusSinkConnectorConfig;
import ru.practicum.util.Versions;

public class PrometheusSinkConnector extends SinkConnector {
    private Map<String, String> configProps;

    @Override
    public void start(Map<String, String> props) {
        configProps = props;
    }

    @Override
    public Class<? extends Task> taskClass() {
        return PrometheusSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> taskConfigs = new ArrayList<>();
        for (int i = 0; i < maxTasks; i++) {
            taskConfigs.add(configProps);
        }
        return taskConfigs;
    }

    @Override
    public void stop() {
    }

    @Override
    public ConfigDef config() {
        return PrometheusSinkConnectorConfig.config();
    }

    @Override
    public String version() {
        return Versions.getVersions();
    }
}