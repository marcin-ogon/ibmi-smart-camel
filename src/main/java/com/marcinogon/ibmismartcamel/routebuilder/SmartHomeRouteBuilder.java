package com.marcinogon.ibmismartcamel.routebuilder;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.support.jsse.KeyManagersParameters;
import org.apache.camel.support.jsse.KeyStoreParameters;
import org.apache.camel.support.jsse.SSLContextParameters;
import org.apache.camel.support.jsse.TrustManagersParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmartHomeRouteBuilder extends RouteBuilder {

    private static final String SQL_UPDATE_STRING = "update SMARTDEV set (status, last_change) = ('OK', current timestamp)";
    private static final String SQL_FAILED_STRING = "update SMARTDEV set (status, last_change) = ('FAIL', current timestamp)";

    @Override
    public void configure() {

        // @formatter:off
        from("sql:classpath:sql/DeviceStatePoll.sql?delay=5000&onConsume=" + SQL_UPDATE_STRING
                + "&onConsumeFailed=" + SQL_FAILED_STRING)
                .routeId("IBMi:SQL:DeviceStatePollRoute")
                .setHeader("MASKME", constant("MaskMeValue"))
                .log(LoggingLevel.INFO,
                        "Header: ${headers}")
                .log(LoggingLevel.INFO,
                        "New command type: ${body['TYPE']}, state: ${body['STATE']}, color: ${body['COLOR']}")

                .choice()
                    .when(simple("${body['DEVICE']} == null || ${body['DEVICE']} == ''"))
                        .log(LoggingLevel.ERROR, "Device is empty")
                    .when(simple("${body['TYPE']} == 'LIGHT' && ${body['STATE']} == 'ON'"))
                        .setHeader(Exchange.HTTP_QUERY, simple("secret={{api.secret}}&device=${body['DEVICE']}&color=${body['COLOR'].toLowerCase()}"))
                        .setHeader(Exchange.HTTP_URI,simple("{{api.setcolor}}"))
                        .to("direct:sendCommand")
                    .when(simple("${body['TYPE']} == 'SWITCH' && ${body['STATE']} == 'ON'"))
                        .setHeader(Exchange.HTTP_QUERY, simple("secret={{api.secret}}&device=${body['DEVICE']}"))
                        .setHeader(Exchange.HTTP_URI, simple("{{api.turnon}}"))
                        .to("direct:sendCommand")
                    .when(simple("${body['STATE']} == 'OFF'"))
                        .setHeader(Exchange.HTTP_QUERY, simple("secret={{api.secret}}&device=${body['DEVICE']}"))
                        .setHeader(Exchange.HTTP_URI, simple("{{api.turnoff}}"))
                        .to("direct:sendCommand")
                    .otherwise()
                        .log(LoggingLevel.ERROR, "Unknown device type: ${body['TYPE']}")
                .end();

        from("direct:sendCommand")
                .routeId("IBMi:SQL:SendCommandRoute")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .to("http://127.0.0.1?followRedirects=true");

        // @formatter:on
    }
}
