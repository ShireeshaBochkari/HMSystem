package com.hostel;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  private final AuthInterceptor auth;
  public WebConfig(AuthInterceptor auth) { this.auth = auth; }
  @Override public void addInterceptors(InterceptorRegistry r) { r.addInterceptor(auth).addPathPatterns("/api/**").excludePathPatterns("/api/auth/**"); }
  // Frontend is a separate app, so allow it to call the API (token is sent in a header, no cookies).
  @Override public void addCorsMappings(CorsRegistry r) { r.addMapping("/api/**").allowedOriginPatterns("*").allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedHeaders("*"); }
}
