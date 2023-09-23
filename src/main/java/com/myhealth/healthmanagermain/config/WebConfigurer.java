package com.myhealth.healthmanagermain.config;

import static java.net.URLDecoder.decode;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebConfigurer implements ServletContextInitializer,
    WebServerFactoryCustomizer<WebServerFactory> {

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = getCorsConfiguration();
    if (!CollectionUtils.isEmpty(config.getAllowedOrigins()) || !CollectionUtils.isEmpty(
        config.getAllowedOriginPatterns())) {
      source.registerCorsConfiguration("/api/**", config);
      source.registerCorsConfiguration("/management/**", config);
      source.registerCorsConfiguration("/v3/api-docs", config);
      source.registerCorsConfiguration("/swagger-ui/**", config);
    }
    return new CorsFilter(source);
  }


  private CorsConfiguration getCorsConfiguration() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedHeaders(List.of("Authorization", "Link", "X-Total-Count"));
    corsConfiguration.setAllowedOrigins(
        List.of("http://localhost:8100", "https://localhost:8100", "http://localhost:4200",
            "https://localhost:4200"));
    corsConfiguration.setAllowedOriginPatterns(List.of("https://*.githubpreview.dev"));
    corsConfiguration.setAllowedMethods(List.of("*"));
    corsConfiguration.setAllowedHeaders(List.of("*"));
    corsConfiguration.setAllowCredentials(true);
    corsConfiguration.setMaxAge(1800L);
    return corsConfiguration;
  }

  private void initH2Console(ServletContext servletContext) {
    H2ConfigurationHelper.initH2Console(servletContext);
  }

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    initH2Console(servletContext);
  }

  @Override
  public void customize(WebServerFactory server) {
    setLocationForStaticAssets(server);
  }

  private void setLocationForStaticAssets(WebServerFactory server) {
    if (server instanceof ConfigurableServletWebServerFactory servletWebServer) {
      File root;
      String prefixPath = resolvePathPrefix();
      root = new File(prefixPath + "target/classes/static/");
      if (root.exists() && root.isDirectory()) {
        servletWebServer.setDocumentRoot(root);
      }
    }
  }

  private String resolvePathPrefix() {
    String fullExecutablePath = decode(this.getClass().getResource("").getPath(),
        StandardCharsets.UTF_8);
    String rootPath = Paths.get(".").toUri().normalize().getPath();
    String extractedPath = fullExecutablePath.replace(rootPath, "");
    int extractionEndIndex = extractedPath.indexOf("target/");
    if (extractionEndIndex <= 0) {
      return "";
    }
    return extractedPath.substring(0, extractionEndIndex);
  }
}
