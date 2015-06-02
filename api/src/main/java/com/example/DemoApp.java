package com.example;

import com.example.api.DumbAuthImplementation;
import com.example.api.HelloApi;
import com.example.api.HelloResource;
import com.example.api.RequestScopedResource;
import com.example.api.WebExceptionMapper;
import com.example.authentication.AuthRequestFilter;
import com.example.filter.JWTFilter;
import com.example.healthcheck.TemplateHealthCheck;
import com.example.tasks.DemoTask;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;


public class DemoApp extends Application<DemoAppConfiguration> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) throws Exception {
        new DemoApp().run(args);
    }

    @Override
    public String getName() {
        return "DemoApp";
    }

    @Override
    public void initialize(Bootstrap<DemoAppConfiguration> bootstrap) {
        AssetsBundle assetsBundle = new AssetsBundle("/assets/", "/", "index.html", "static");
        bootstrap.addBundle(assetsBundle);
    }

    @Override
    public void run(DemoAppConfiguration configuration, Environment environment) {
        enableWadl(environment);

        final HelloApi resource = new HelloResource(
            configuration.getTemplate(),
            configuration.getDefaultName()
        );

        final TemplateHealthCheck healthCheck = new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);

        environment.getApplicationContext().addFilter(JWTFilter.class, "/api/*", EnumSet.allOf(DispatcherType.class));

        environment.admin().addTask(new DemoTask());

        environment.jersey().register(resource);

        environment.jersey().register(RequestScopedResource.class);
        environment.jersey().register(DumbAuthImplementation.class);

        environment.jersey().getResourceConfig().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(AuthRequestFilter.class);

        environment.jersey().register(new WebExceptionMapper());

    }

    private void enableWadl(Environment environment) {
        Map<String, Object> props = new HashMap<>();
        props.put("jersey.config.server.wadl.disableWadl", "false");
        environment.jersey().getResourceConfig().addProperties(props);
    }

}