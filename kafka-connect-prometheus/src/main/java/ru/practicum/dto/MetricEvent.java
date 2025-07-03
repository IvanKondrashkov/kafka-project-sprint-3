package ru.practicum.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class MetricEvent {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("Value")
    private double value;
}