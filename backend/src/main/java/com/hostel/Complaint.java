package com.hostel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Complaint {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
  @ManyToOne(optional = false) @JoinColumn(name = "student_id") public Student student;
  @Column(nullable = false, length = 120) public String title;
  @Column(nullable = false, length = 4000) public String details;
  @Column(nullable = false, length = 20) public String status = "OPEN";
  @Column(length = 2000) public String response;
  @Column(nullable = false) public LocalDateTime createdAt = LocalDateTime.now();
}