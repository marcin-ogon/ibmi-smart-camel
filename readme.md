# IbmiSmartCamel Application

This project is a Spring Boot application that uses Apache Camel for integration and routing of messages. It interacts with IBMi systems and provides a REST API for monitoring system performance and handling user inquiries.

## Project Structure

The project consists of several components:

- `IbmiSmartCamelApplication.java`: The main entry point of the Spring Boot application.
- `UserService.java` and `UserServiceImpl.java`: Service layer for handling user-related operations.
- `ServletRouteBuilder.java`: Defines Camel routes for handling HTTP requests.
- `SmartHomeRouteBuilder.java`: Defines Camel routes for smart home device state polling.
- `RestRouteBuilder.java`: Defines Camel routes for RESTful services.
- `InquiryMessageRouteBuilder.java`: Defines Camel routes for handling inquiry messages.
- `CpuRateWithDtaqRouteBuilder.java`: Defines Camel routes for monitoring CPU usage with DTAQ.
- `CpuRateRouteBuilder.java`: Defines Camel routes for monitoring CPU usage.

## Templates

The project uses Mustache templates for generating HTML responses:

- `Monitor.mustache`: Template for the monitor page.
- `Main.mustache`: Template for the main page.
- `InquiryMessage.mustache`: Template for the inquiry message page.

## SQL Scripts

- `DeviceStatePoll.sql`: SQL script for polling the state of smart home devices.

## Configuration

The application's configuration is defined in `application.properties`.

## Running the Application

To run the application, use the following command in the terminal:

```bash
mvn spring-boot:run
```

This will start the application on the default port (8080). You can access the application by navigating to `http://localhost:8080` in your web browser.

## Websites

- `/camel/main`: Main website.
- `/camel/monitor`: Average CPU usage graph.
- `/camel/dtaq`: Average CPU usage using DTAQ graph.
- `/camel/message`: Response to an inquiry message.

## API Endpoints

The application provides several REST API endpoints for interacting with the system:

- `/api/cpu`: Returns the average CPU usage.
- `/api/cpudtaq`: Returns the average CPU usage using DTAQ.
- `/api/inquiryMessage`: Returns the response to an inquiry message.

