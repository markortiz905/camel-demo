package demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ThreadPoolBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

/**
 * @author mortiz
 */
@Slf4j
@SpringBootApplication
public class SimpleDemo {

    public static void main(String[] args) {
        SpringApplication.run(SimpleDemo.class, args);
    }

    @Bean
    public ExecutorService executorService(CamelContext camelContext) throws Exception {
        return new ThreadPoolBuilder(camelContext)
                .poolSize(10)
                .maxPoolSize(20)
                .maxQueueSize(100)
                .build("Custom-TPool");
    }

    @Bean
    public Processor sink(ProducerTemplate template) {
        return exchange -> {
            ObjectMapper mapper = new ObjectMapper();
            String fileName = exchange.getIn().getHeader("CamelFileName").toString();
            Profile profile = exchange.getIn().getBody(Profile.class);
            template.sendBodyAndHeader("file://{{sink_folder}}/" +
                    exchange.getIn().getHeader("FIRST_LETTER") + "/",
                    mapper.writeValueAsString(profile).getBytes(StandardCharsets.UTF_8), "CamelFileName",fileName);
            log.info("file written {}", fileName);
        };
    }

}
