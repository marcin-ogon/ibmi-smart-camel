package com.marcinogon.ibmismartcamel.routebuilder;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ServletRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off

        from("servlet:main")
            .routeId("WEB:Main")
            .setHeader("Content-Type", constant("text/html"))
            .to("mustache:templates/Main.mustache");

        // @formatter:on
    }

}
