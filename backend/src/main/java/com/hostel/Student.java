package com.hostel;
import jakarta.persistence.*;
@Entity
public class Student {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
  @Column(nullable = false) public String name;
  @Column(unique = true, nullable = false) public String email;
  public String phone;
  @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", unique = true) public User user;
  @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "room_id") public Room room;
}
