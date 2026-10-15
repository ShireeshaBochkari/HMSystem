package com.hostel;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
  private final TokenService tokens;
  public AuthInterceptor(TokenService tokens) { this.tokens = tokens; }
  @Override public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object h) {
    if (req.getMethod().equals("OPTIONS")) return true;
    String hd = req.getHeader("Authorization");
    String[] t = hd != null && hd.startsWith("Bearer ") ? tokens.parse(hd.substring(7)) : null;
    if (t == null) throw new ApiException(401, "Please login");
    if (!req.getMethod().equals("GET") && !t[1].equals("ADMIN")) throw new ApiException(403, "Admin access required");
    return true;
  }
}
