package com.jtriemstra.dominion.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.jtriemstra.dominion.api.controllers.TestInterceptorAdapter;

@Configuration
public class DominionApiConfiguration implements WebMvcConfigurer {
	@Autowired
    private TestInterceptorAdapter x;
 
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(x)
          .addPathPatterns("/**");
    }
}
