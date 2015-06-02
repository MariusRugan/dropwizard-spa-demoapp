package com.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.validator.constraints.NotEmpty;

import io.dropwizard.Configuration;

public class DemoAppConfiguration extends Configuration {

  @NotEmpty
  final private String template;

  @NotEmpty
  final private String defaultName;

  @JsonCreator
  public DemoAppConfiguration(@JsonProperty("template") String template, @JsonProperty("defaultName") String defaultName) {
    this.template = template;
    this.defaultName = defaultName;
  }

  @JsonProperty
  public String getTemplate() {
    return template;
  }

  @JsonProperty
  public String getDefaultName() {
    return defaultName;
  }
}
