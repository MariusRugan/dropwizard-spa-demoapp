package com.example.tasks;

import com.google.common.collect.ImmutableMultimap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;

import io.dropwizard.servlets.tasks.Task;

public class MyTestTask extends Task {

  final Logger logger = LoggerFactory.getLogger(getClass());

  public MyTestTask() {
    super("test");
  }

  @Override
  public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception {
    logger.info("Got map with {} paramters", parameters.size());

    logger.info("Keys");
    parameters.keySet().forEach((key) -> logger.info("Key: {}", key));

    output.write(parameters.asMap().toString());
  }
}
