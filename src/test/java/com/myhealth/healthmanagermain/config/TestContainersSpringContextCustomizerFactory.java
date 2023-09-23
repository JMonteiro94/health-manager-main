package com.myhealth.healthmanagermain.config;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

@Slf4j
public class TestContainersSpringContextCustomizerFactory implements ContextCustomizerFactory {

  private static SqlTestContainer prodTestContainer;

  @Override
  public ContextCustomizer createContextCustomizer(Class<?> testClass,
      List<ContextConfigurationAttributes> configAttributes) {
    return (context, mergedConfig) -> {
      ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
      TestPropertyValues testValues = TestPropertyValues.empty();
      EmbeddedSQL sqlAnnotation = AnnotatedElementUtils.findMergedAnnotation(testClass,
          EmbeddedSQL.class);
      if (sqlAnnotation != null) {
        log.debug("detected the EmbeddedSQL annotation on class {}", testClass.getName());
        log.info("Warming up the sql database");
        if (
            Arrays
                .asList(context.getEnvironment().getActiveProfiles())
                .contains("test" + "prod")
        ) {
          if (null == prodTestContainer) {
            try {
              Class<? extends SqlTestContainer> containerClass = (Class<? extends SqlTestContainer>) Class.forName(
                  this.getClass().getPackageName() + ".MysqlTestContainer"
              );
              prodTestContainer = beanFactory.createBean(containerClass);
              beanFactory.registerSingleton(containerClass.getName(), prodTestContainer);
              // ((DefaultListableBeanFactory)beanFactory).registerDisposableBean(containerClass.getName(), prodTestContainer);
            } catch (ClassNotFoundException e) {
              throw new RuntimeException(e);
            }
          }
          testValues =
              testValues.and(
                  "spring.datasource.url=" +
                      prodTestContainer.getTestContainer().getJdbcUrl() +
                      "?useUnicode=true&characterEncoding=utf8&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC&createDatabaseIfNotExist=true"
              );
          testValues = testValues.and(
              "spring.datasource.username=" + prodTestContainer.getTestContainer().getUsername());
          testValues = testValues.and(
              "spring.datasource.password=" + prodTestContainer.getTestContainer().getPassword());
        }
      }
      testValues.applyTo(context);
    };
  }
}
