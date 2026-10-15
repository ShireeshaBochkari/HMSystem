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
    req.setAttribute("username", t[0]);
    req.setAttribute("role", t[1]);
    if (t[1].equals("OWNER") || t[1].equals("ADMIN")) return true;

    String path = req.getRequestURI().substring(req.getContextPath().length());
    String method = req.getMethod();
    boolean hostelerRead = method.equals("GET") && (path.equals("/api/me") || path.equals("/api/rooms")
        || path.equals("/api/messages") || path.equals("/api/fees") || path.equals("/api/schedule")
        || path.equals("/api/complaints"));
    boolean hostelerWrite = method.equals("POST") && (path.equals("/api/complaints")
        || path.matches("/api/fees/\\d+/payment"));
    if ((t[1].equals("HOSTELER") || t[1].equals("STUDENT")) && (hostelerRead || hostelerWrite)) return true;
    throw new ApiException(403, "Owner access required");
  }
}
