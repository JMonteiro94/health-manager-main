package com.myhealth.healthmanagermain.config;

import com.github.benmanes.caffeine.jcache.configuration.CaffeineConfiguration;
import com.myhealth.healthmanagermain.domain.Authority;
import com.myhealth.healthmanagermain.domain.UserAccount;
import com.myhealth.healthmanagermain.repository.UserAccountRepository;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import javax.cache.Cache;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfiguration {

  private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

  public CacheConfiguration() {
    CaffeineConfiguration<Object, Object> caffeineConfiguration = new CaffeineConfiguration<>();
    caffeineConfiguration.setMaximumSize(OptionalLong.of(500L));
    caffeineConfiguration.setExpireAfterWrite(
        OptionalLong.of(TimeUnit.SECONDS.toNanos(600)));
    caffeineConfiguration.setStatisticsEnabled(true);
    jcacheConfiguration = caffeineConfiguration;
  }

  @Bean
  public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(
      CacheManager cacheManager) {
    return hibernateProperties -> hibernateProperties.put("hibernate.javax.cache.cache_manager",
        cacheManager);
  }

  @Bean
  public JCacheManagerCustomizer cacheManagerCustomizer() {
    return cm -> {
      createCache(cm, UserAccountRepository.USERS_BY_USERNAME_CACHE);
      createCache(cm, UserAccountRepository.USERS_BY_EMAIL_CACHE);
      createCache(cm, UserAccount.class.getName());
      createCache(cm, Authority.class.getName());
      createCache(cm, UserAccount.class.getName() + ".authorities");
    };
  }

  private void createCache(javax.cache.CacheManager cm, String cacheName) {
    Cache<Object, Object> cache = cm.getCache(cacheName);
    if (cache != null) {
      cache.clear();
    } else {
      cm.createCache(cacheName, jcacheConfiguration);
    }
  }
}
