package demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

/**
 * @author mortiz
 */
@Slf4j
@Component
public class SimpleRouteDemo extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        onException(Exception.class)
                .log(LoggingLevel.ERROR, log, "Some Exception Occurred ${exception.message}")
                .maximumRedeliveries(2).redeliveryDelay(2000L)
                .end();

        from("file://{{source_folder}}/" +
                "?initialDelay=5s" +
                "&maxMessagesPerPoll=1" +
                "&recursive=false" +
                "&include=.*json" +
                "&move=processed/${file:name}")
                .threads().executorServiceRef("executorService")

                .unmarshal().json(JsonLibrary.Jackson, Profile.class)
                .process(exchange -> { // this can have its own bean processor in real projects
                    Profile profile = (Profile) exchange.getIn().getBody();
                    profile.setBmi(calcBmi(profile));
                    exchange.getIn().setHeader("FIRST_LETTER", profile.getUsername().charAt(0));
                    exchange.getIn().setBody(profile);
                })

                .marshal().json(JsonLibrary.Jackson)
                .log(LoggingLevel.INFO, log, "${body}")
                .to("bean:sink");
    }

    private double calcBmi(Profile profile) {
        //84÷(5.4÷3.281)² example bmi
        double metersPowOf2 = Math.pow(profile.getHeight()/3.281, 2);
        return Math.round(profile.getWeight() / metersPowOf2);
    }
}
