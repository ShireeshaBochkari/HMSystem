package com.hostel;
import jakarta.persistence.*;
import java.util.*;
@Entity
public class Room {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
  @Column(unique = true, nullable = false) public String roomNo;
  public int capacity;
  @OneToMany(mappedBy = "room") public List<Student> students = new ArrayList<>();
}
