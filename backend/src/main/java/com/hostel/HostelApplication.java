package com.hostel;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class HostelApplication {
  public static void main(String[] a) { SpringApplication.run(HostelApplication.class, a); }
  @Bean BCryptPasswordEncoder encoder() { return new BCryptPasswordEncoder(); }
  @Bean CommandLineRunner seed(UserRepository u, RoomRepository r, BCryptPasswordEncoder e) {
    return x -> {
      if (u.count() == 0) {
        u.save(new User("admin", e.encode("admin123"), Role.ADMIN));
        u.save(new User("student", e.encode("student123"), Role.STUDENT));
      }
      if (r.count() == 0) for (String n : new String[]{"101", "102", "103"}) { Room m = new Room(); m.roomNo = n; m.capacity = 3; r.save(m); }
    };
  }
}
