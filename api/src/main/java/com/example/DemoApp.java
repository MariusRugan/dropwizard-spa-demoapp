package com.example;

import com.example.api.HelloApi;
import com.example.api.HelloResource;
import com.example.api.HelloSecureResource;
import com.example.exception.DemoAppExceptionMapper;
import com.example.filter.AuthRequestFilter;
import com.example.filter.CrossDomainServletFilter;
import com.example.healthcheck.TemplateHealthCheck;
import com.example.tasks.DemoTask;
import com.github.stokito.gag.annotation.disclaimer.WrittenWhile;

import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.joda.time.DateTimeZone;
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

        DateTimeZone.setDefault(DateTimeZone.UTC);

        AssetsBundle assetsBundle = new AssetsBundle("/assets/", "/", "index.html", "static");
        bootstrap.addBundle(assetsBundle);
    }

    /**
     * @todo extract into separate method
     *
     * @param configuration
     * @param environment
     */

    @WrittenWhile("lulz")
    @Override
    public void run(DemoAppConfiguration configuration, Environment environment) {

        //Servlet filters
        environment.servlets()
            .addFilter("CrossDomainFilter", new CrossDomainServletFilter())
            .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/api/*");

        /*
        environment.servlets()
            .addFilter("TokenHeaderServletFilter", new TokenHeaderServletFilter())
            .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/api/*");
        */

        //Jersey properties
        environment.jersey().property(ServerProperties.PROCESSING_RESPONSE_ERRORS_ENABLED, false);

        //Jersey filters
        environment.jersey().register(DemoAppExceptionMapper.class);
        environment.jersey().register(AuthRequestFilter.class);

        enableWadl(environment);


        final TemplateHealthCheck healthCheck = new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);

        //environment.getApplicationContext().addFilter(JwtServletFilter.class, "/api/*", EnumSet.allOf(DispatcherType.class));

        //@todo this or that above?
        //environment.getApplicationContext().addFilter(CrossDomainServletFilter.class, "/api/*", EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));

        environment.admin().addTask(new DemoTask());

        // resources
        final HelloApi resource = new HelloResource(
            configuration.getTemplate(),
            configuration.getDefaultName()
        );
        environment.jersey().register(resource);
        environment.jersey().register(HelloSecureResource.class);
        //end resources

        environment.jersey().getResourceConfig().register(RolesAllowedDynamicFeature.class);


    }

    private void enableWadl(Environment environment) {
        Map<String, Object> props = new HashMap<>();
        props.put("jersey.config.server.wadl.disableWadl", "false");
        environment.jersey().getResourceConfig().addProperties(props);
    }
}