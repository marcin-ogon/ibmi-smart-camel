package com.marcinogon.ibmismartcamel.routebuilder;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jt400.Jt400Constants;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;
import com.ibm.as400.access.AS400Message;

@Component
public class InquiryMessageRouteBuilder extends RouteBuilder {

    private 
    Faker faker = new Faker();

    @Override
    public void configure() throws Exception {

        final String question = "What is your name?             ";

        // @formatter:off

        from("jt400://{{spring.datasource.username}}:{{spring.datasource.password}}@{{ibmi.host}}/qsys.lib/{{msgq.library}}.lib/{{msgq.name}}.msgq?sendingReply=true")
            .routeId("MSGQ:InquiryMessage")
            .log("Received message: ${body}")
            .choice()
                .when(header(Jt400Constants.MESSAGE_TYPE).isEqualTo(AS400Message.INQUIRY))
                .process((exchange) -> {
                    String reply = faker.name().firstName(); 
                    exchange.getIn().setBody(reply);
                })
                .to("jt400://{{spring.datasource.username}}:{{spring.datasource.password}}@{{ibmi.host}}/qsys.lib/{{msgq.library}}.lib/{{msgq.name}}.msgq");

        from("servlet:message")
            .routeId("WEB:InquiryMessage")
            .setHeader("Content-Type", constant("text/html"))
            .setHeader("apiUrl", constant("/camel/api/inquiryMessage"))
            .setHeader("question", simple(question))
            .to("mustache:templates/InquiryMessage.mustache");

        rest("/api/inquiryMessage")
            .produces("text/plain")
            .get().description("Get inquiry message response").outType(String.class)
            .to("direct:invokeInquiryMessage");

        from("direct:invokeInquiryMessage")
            .routeId("DIRECT:MSG:InquiryMessage")
            .process( exchange -> {
                String message = question;
                Object[] parms = new Object[] {
                    message
                };
                exchange.getIn().setBody(parms);
            })
            .to("jt400://{{spring.datasource.username}}:{{spring.datasource.password}}@{{ibmi.host}}/qsys.lib/{{msgq.pgm.library}}.lib/{{msgq.pgm.name}}.PGM?fieldsLength=32&outputFieldsIdx=0")
            .setBody(simple("${body[0]}"))
            .log(LoggingLevel.INFO, "MSGQ :: ${body}");
        
        // @formatter:on
    }
}