package com.hostel;
import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
public class MealSchedule {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
  @Column(nullable = false, length = 12) public String dayOfWeek;
  @Column(nullable = false, length = 20) public String meal;
  @Column(nullable = false) public LocalTime time;
  @Column(nullable = false, length = 500) public String menu;
}