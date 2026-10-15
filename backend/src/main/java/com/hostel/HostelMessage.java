package com.hostel;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class HostelMessage {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
  @ManyToOne(optional = false) @JoinColumn(name = "recipient_id") public Student recipient;
  @Column(nullable = false, length = 100) public String title;
  @Column(nullable = false, length = 4000) public String body;
  @Column(nullable = false) public LocalDateTime sentAt = LocalDateTime.now();
}