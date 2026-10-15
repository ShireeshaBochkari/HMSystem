package com.hostel;
import jakarta.persistence.*;
@Entity @Table(name = "users")
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
  @Column(unique = true, nullable = false) public String username;
  @Column(nullable = false) public String password;
  @Enumerated(EnumType.STRING) public Role role;
  public User() {}
  public User(String u, String p, Role r) { username = u; password = p; role = r; }
}
