package de.ydsgermany.herborder.config;

import de.ydsgermany.herborder.global.ExternalIdGenerator;
import de.ydsgermany.herborder.order.OrdersRepository;
import de.ydsgermany.herborder.order_batch.OrderBatchesRepository;
import java.util.List;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApplicationConfiguration implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
    }

    @Bean
    Flyway flyway(
        @Value("${flyway.datasource.url}") String url,
        @Value("${flyway.datasource.username}") String username,
        @Value("${flyway.datasource.password}") String password
    ) {
        FluentConfiguration configure = Flyway.configure()
            .dataSource(url, username, password)
            .baselineOnMigrate(true);
        Flyway flyway = new Flyway(configure);
        flyway.migrate();
        return flyway;
    }

    @Bean(name = "ordersExternalIdGenerator")
    ExternalIdGenerator ordersExternalIdGenerator(OrdersRepository ordersRepository) {
        return new ExternalIdGenerator(ordersRepository);
    }

    @Bean(name = "orderBatchesExternalIdGenerator")
    ExternalIdGenerator orderBatchesExternalIdGenerator(OrderBatchesRepository orderBatchesRepository) {
        return new ExternalIdGenerator(orderBatchesRepository);
    }

}
