package com.hostel;
import com.hostel.Dtos.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/auth")
public class AuthController {
  private final UserRepository users; private final BCryptPasswordEncoder enc; private final TokenService tokens;
  public AuthController(UserRepository u, BCryptPasswordEncoder e, TokenService t) { users = u; enc = e; tokens = t; }

  @PostMapping("/register")
  public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterDto d) {
    if (users.findByUsername(d.username().trim()).isPresent()) throw new ApiException(409, "Username already taken");
    users.save(new User(d.username().trim(), enc.encode(d.password()), Role.STUDENT));
    return ResponseEntity.status(201).body(Map.of("message", "Registered"));
  }
  @PostMapping("/login")
  public Map<String, String> login(@Valid @RequestBody LoginDto d) {
    User u = users.findByUsername(d.username().trim()).filter(x -> enc.matches(d.password(), x.password))
        .orElseThrow(() -> new ApiException(401, "Invalid username or password"));
    return Map.of("token", tokens.create(u.username, u.role), "username", u.username, "role", u.role.name());
  }
}
