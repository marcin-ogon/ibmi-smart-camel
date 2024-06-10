package com.marcinogon.ibmismartcamel.routebuilder;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CpuRateRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off

        from("servlet:monitor")
            .routeId("WEB:Monitor")
            .setHeader("Content-Type", constant("text/html"))
            .setHeader("diagram", 
                constant(
                    "graph TD\n" +
                    "A[Website polling API]\n" +
                    "B[Camel REST request]\n" +
                    "C[Camel SQL <- IBMi DB2]\n" +
                    "D[Send REST answer]\n" +
                    "A --> B\n" +
                    "B --> C\n" +
                    "C --> D"))
            .setHeader("apiUrl", constant("/camel/api/cpu"))
            .setHeader("size", constant("100"))
            .setHeader("label", constant("Average CPU Rate :: QSYS2.SYSTEM_ACTIVITY_INFO()"))
            .to("mustache:templates/Monitor.mustache");

        rest("/api/cpu")
            .produces("text/plain")
            .get().description("Average CPU Rate with SQL").outType(String.class)
            .to("direct:GetAverageRateWithSQL");

        from("direct:GetAverageRateWithSQL")
            .routeId("REST:Cpu:GetAverageRateWithSQL")
            .to("sql:SELECT AVERAGE_CPU_RATE FROM TABLE(QSYS2.SYSTEM_ACTIVITY_INFO())")
            .setBody(simple("${body[0][AVERAGE_CPU_RATE]}"))
            .log(LoggingLevel.INFO, "DTAQ :: SYSTEM_ACTIVITY_INFO :: ${body}");

        // @formatter:on
    }

}
