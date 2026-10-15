package com.hostel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HostelMessageRepository extends JpaRepository<HostelMessage, Long> {
  List<HostelMessage> findByRecipientUserUsernameOrderBySentAtDesc(String username);
  List<HostelMessage> findAllByOrderBySentAtDesc();
}