package com.hostel;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class HostelApplication {
  public static void main(String[] a) { SpringApplication.run(HostelApplication.class, a); }
  @Bean BCryptPasswordEncoder encoder() { return new BCryptPasswordEncoder(); }
  @Bean CommandLineRunner seed(UserRepository u, StudentRepository s, RoomRepository r, BCryptPasswordEncoder e) {
    return x -> {
      if (r.count() == 0) for (String n : new String[]{"101", "102", "103"}) { Room m = new Room(); m.roomNo = n; m.capacity = 3; r.save(m); }
      if (u.findByUsername("owner").isEmpty()) {
        u.save(new User("owner", e.encode(System.getenv().getOrDefault("OWNER_PASSWORD", "owner123")), Role.OWNER));
      }
      if (u.findByUsername("hosteler").isEmpty()) {
        User hosteler = u.save(new User("hosteler", e.encode(System.getenv().getOrDefault("HOSTELER_PASSWORD", "hosteler123")), Role.HOSTELER));
        Student student = new Student(); student.user = hosteler; student.name = "Demo Hosteler";
        student.email = "hosteler@example.com"; student.phone = "0000000000";
        student.room = r.findAll().stream().findFirst().orElse(null); s.save(student);
      }
    };
  }
}
