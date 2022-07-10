package com.myhealth.healthmanagermain;

import com.myhealth.healthmanagermain.config.DefaultProfileUtil;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

		DefaultProfileUtil.addDefaultProfile(application.application());
		return application.sources(HealthmanagermainApplication.class);
	}
}
