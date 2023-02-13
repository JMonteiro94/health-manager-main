package com.myhealth.healthmanagermain.bootstrap;

import com.myhealth.healthmanagermain.aop.timer.MeasureTime;
import com.myhealth.healthmanagermain.security.AuthoritiesConstants;
import com.myhealth.healthmanagermain.service.domain.AuthorityService;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("demo")
@Component
@AllArgsConstructor
public class DemoDataBoostrap implements ApplicationRunner {

  private final AuthorityService authorityService;

  @Override
  @MeasureTime
  public void run(ApplicationArguments args) {
    loadAuthorities();
  }

  public void loadAuthorities() {
    Set<String> authorities = Set.of(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER,
        AuthoritiesConstants.PT, AuthoritiesConstants.ANONYMOUS);
    authorityService.saveAll(authorities);
  }
}
