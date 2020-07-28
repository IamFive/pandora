package net.turnbig.pandora.web.cluster;

import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author Woo Cubic
 */
//@EnableRedisHttpSession
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RedisHttpSessionConfig.RedisSessionProperties.class)
@AutoConfigureBefore(RedisHttpSessionConfiguration.class)
public class RedisHttpSessionConfig {

    final RedisHttpSessionConfig.RedisSessionProperties properties;
    protected RedisConnectionFactory redisConnectionFactory;

    public RedisHttpSessionConfig(RedisHttpSessionConfig.RedisSessionProperties properties) {
        this.properties = properties;
    }


    /**
     * replacing default redis connection factory for spring session
     */
    @Autowired
    @ConditionalOnBean(RedisConnectionFactory.class)
    public void redisSessionConnectionFactory(GenericApplicationContext context) {
        // update exists RedisConnectionFactory bean as primary bean
        BeanDefinition primary = context.getBeanDefinition("redisConnectionFactory");
        primary.setPrimary(true);

        // register a new RedisConnectionFactory with qualified annotation SpringSessionRedisConnectionFactory
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(properties.getHost());
        config.setPort(properties.getPort());
        config.setPassword(properties.getPassword());
        config.setDatabase(properties.getDatabase());

        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        context.registerBean(
            "redisSessionConnectionFactory",
            RedisConnectionFactory.class,
            () -> factory,
            (bd) -> {
                RootBeanDefinition rbd = (RootBeanDefinition) bd;
                rbd.addQualifier(new AutowireCandidateQualifier(SpringSessionRedisConnectionFactory.class));
            }
        );
    }


    /**
     * Composite Session Id resolver support header first then cookie.
     *
     * @return A Composite HTTP Session Id Resolver support both head and cookie.
     */
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        HeaderHttpSessionIdResolver first = HeaderHttpSessionIdResolver.xAuthToken();
        CookieHttpSessionIdResolver second = new CookieHttpSessionIdResolver();
        return new CompositeHttpSessionIdResolver(first, second);
    }


    @Data
    @ConfigurationProperties(prefix = "spring.session.redis")
    public static class RedisSessionProperties {
        String host = "127.0.0.1";
        String password;
        int database = 1;
        int port = 6379;
    }


    public static class CompositeHttpSessionIdResolver implements HttpSessionIdResolver {

        List<HttpSessionIdResolver> resolvers = new ArrayList<>();

        public CompositeHttpSessionIdResolver(HttpSessionIdResolver first) {
            this.resolvers.add(first);
        }

        public CompositeHttpSessionIdResolver(HttpSessionIdResolver first, HttpSessionIdResolver second) {
            this.resolvers.add(first);
            this.resolvers.add(second);
        }

        /**
         * add a HttpSessionIdResolver to composite resolver list
         *
         * @param resolver new resolver to be added to composite resolver list
         */
        public void addResolver(HttpSessionIdResolver resolver) {
            this.resolvers.add(resolver);
        }

        @Override
        public List<String> resolveSessionIds(HttpServletRequest request) {
            for (HttpSessionIdResolver resolver : resolvers) {
                List<String> sessionIds = resolver.resolveSessionIds(request);
                if (sessionIds != null && !sessionIds.isEmpty()) {
                    // short circuit composite resolvers or return all?
                    return sessionIds;
                }
            }
            return Collections.emptyList();
        }

        @Override
        public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
            for (HttpSessionIdResolver resolver : resolvers) {
                resolver.setSessionId(request, response, sessionId);
            }
        }

        @Override
        public void expireSession(HttpServletRequest request, HttpServletResponse response) {
            for (HttpSessionIdResolver resolver : resolvers) {
                resolver.expireSession(request, response);
            }
        }
    }

}
