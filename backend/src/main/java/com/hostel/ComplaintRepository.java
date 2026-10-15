package com.hostel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
  List<Complaint> findByStudentUserUsernameOrderByCreatedAtDesc(String username);
  List<Complaint> findAllByOrderByCreatedAtDesc();
}