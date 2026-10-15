package com.hostel;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class FeePayment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
  @ManyToOne(optional = false) @JoinColumn(name = "student_id") public Student student;
  @Column(nullable = false, length = 40) public String period;
  @Column(nullable = false, precision = 10, scale = 2) public BigDecimal amount;
  @Column(nullable = false) public LocalDate dueDate;
  @Column(nullable = false, length = 20) public String status = "DUE";
  @Column(nullable = false) public LocalDateTime createdAt = LocalDateTime.now();
}