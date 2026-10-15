package com.hostel;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface StudentRepository extends JpaRepository<Student, Long> {
  Page<Student> findByNameContainingIgnoreCase(String q, Pageable p);
  long countByRoomId(Long roomId);
  java.util.Optional<Student> findByUserUsername(String username);
}
