package com.hostel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {
  List<FeePayment> findByStudentUserUsernameOrderByCreatedAtDesc(String username);
  List<FeePayment> findAllByOrderByCreatedAtDesc();
}