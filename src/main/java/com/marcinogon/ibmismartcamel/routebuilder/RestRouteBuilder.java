package com.marcinogon.ibmismartcamel.routebuilder;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

import com.marcinogon.ibmismartcamel.model.User;

@Component
public class RestRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // @formatter:off
        // configure we want to use servlet as the component for the rest DSL
        // and we enable json binding mode
        restConfiguration()
            .dataFormatProperty("prettyPrint", "true")
            .enableCORS(true)
            .apiContextPath("/api-doc")
            .apiProperty("api.title", "IBMi Smart Camel REST API")
            .apiProperty("api.version", "v1")
            .apiContextRouteId("REST:OpenApiRoute")
            .component("servlet")
            .bindingMode(RestBindingMode.json);

        // this user REST service is json only
        rest("/users").description("User rest service")
            .consumes("application/json").produces("application/json")

            .get("/{id}").routeId("REST:User:GetById").description("Find user by id").outType(User.class)
                .param().name("id").type(RestParamType.path)
                    .description("The id of the user to get")
                    .dataType("int")
                .endParam()
                .to("bean:userService?method=getUser(${header.id})")

            .get().routeId("REST:User:GetAll").description("Find all users").outType(User[].class)
                .to("bean:userService?method=listUsers")
                
            .put("/{id}").routeId("REST:User:Update").description("Update a user").type(User.class)
                .param().name("id").type(RestParamType.path).description("The ID of the user to update").dataType("integer").endParam()    
                .param().name("body").type(RestParamType.body).description("The user to update").endParam()
                .responseMessage().code(204).message("User successfully updated").endResponseMessage()
                .to("direct:update-user");

        from("direct:update-user")
            .to("bean:userService?method=updateUser")
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(204))
            .setBody(constant(""));

        // @formatter:on
    }
}
