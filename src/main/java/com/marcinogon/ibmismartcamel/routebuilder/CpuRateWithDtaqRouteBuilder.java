package com.marcinogon.ibmismartcamel.routebuilder;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.springframework.stereotype.Component;

import com.marcinogon.ibmismartcamel.model.DtaqMessageFormat;

@Component
public class CpuRateWithDtaqRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off

        from("servlet:dtaq")
            .routeId("WEB:MonitorDtaq")
            .setHeader("Content-Type", constant("text/html"))
            .setHeader("diagram", 
                constant(
                    "graph TD\n" +
                    "A[Website polling API]\n" +
                    "B[Camel REST request]\n" +
                    "C[Camel JT400 sends message to REQUEST DTAQ]\n" +
                    "D[Camel JT400 starts listening on RESONSE DTAQ]\n" +
                    "E[IBMi listener program gets message]\n" +
                    "F[IBMi program sends answer to RESPONSE DTAQ]\n" +
                    "G[Camel JT400 receives reply on RESONSE DTAQ]\n" +
                    "H[Camel BINDY unmarshall message to object]\n" +
                    "I[Camel REST return one object field]\n" +
                    "A --> B\n" +
                    "B --> C\n" +
                    "C --> D\n" +
                    "D --> E\n" +
                    "E --> F\n" +
                    "F --> G\n" +
                    "G --> H\n" +
                    "H --> I"))
            .setHeader("apiUrl", constant("/camel/api/cpudtaq"))
            .setHeader("size", constant("200"))
            .setHeader("label", constant("% processing unit used :: QWCRSSTS API in CLLE LISTENER and Q2Q"))
            .to("mustache:templates/Monitor.mustache");

        rest("/api/cpudtaq")
            .produces("text/plain")
            .get().description("Get average rate with DTAQ").outType(String.class)
            .to("direct:GetAverageRateWithDtaq");

        from("direct:GetAverageRateWithDtaq")
            .routeId("REST:Cpu:GetAverageRateWithDtaq")
            .setBody(constant("Retrieve System Status :: QWCRSSTS :: % processing unit used"))
            .to("jt400://{{spring.datasource.username}}:{{spring.datasource.password}}@{{ibmi.host}}/QSYS.LIB/{{dtaq.request.library}}.LIB/{{dtaq.request.name}}.DTAQ")
                .pollEnrich("jt400://{{spring.datasource.username}}:{{spring.datasource.password}}@{{ibmi.host}}/QSYS.LIB/{{dtaq.response.library}}.LIB/{{dtaq.response.name}}.DTAQ")
                    .log(LoggingLevel.INFO, "DTAQ :: BEFORE BINDY :: ${body}")
                    .unmarshal().bindy(BindyType.Fixed, DtaqMessageFormat.class)
                    .log(LoggingLevel.INFO, "DTAQ :: AFTER BINDY  :: ${body}")
                    .setBody(simple("${body.unitUsed}"));

        // @formatter:on
    }
}
