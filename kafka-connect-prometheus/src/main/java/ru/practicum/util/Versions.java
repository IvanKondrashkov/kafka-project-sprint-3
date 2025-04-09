package ru.practicum.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.connector.PrometheusSinkConnector;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Versions {
    public static String getVersions() {
        return PrometheusSinkConnector.class.getPackage().getImplementationVersion() != null
                ? PrometheusSinkConnector.class.getPackage().getImplementationVersion()
                : "1.0.0";
    }
}