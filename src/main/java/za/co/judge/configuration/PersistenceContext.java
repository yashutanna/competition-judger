package za.co.judge.configuration;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan("za.co.judges")
@EnableNeo4jRepositories("za.co.judge.repositories")
public class PersistenceContext {
    @Bean
    public SessionFactory sessionFactory() {
        return new SessionFactory(configuration(), "za.co.judge.domain");
    }

    @Bean
    public Neo4jTransactionManager transactionManager() throws Exception {
        SessionFactory sessionFactory = sessionFactory();
        return new Neo4jTransactionManager(sessionFactory);
    }

    @Bean
    public org.neo4j.ogm.config.Configuration configuration() {
        return new org.neo4j.ogm.config.Configuration.Builder()
                .credentials("neo4j", "NEW PASSWORD")
                .uri("bolt://127.0.0.1:7687")
                .build();
    }
}