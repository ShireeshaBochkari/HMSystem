package com.hostel;
import org.springframework.web.bind.annotation.*;

/** Public health check for Render (path is /healthz, outside the /api auth interceptor). */
@RestController
public class HealthController {
  @GetMapping("/healthz") public String health() { return "ok"; }
}
