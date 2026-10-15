package com.hostel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MealScheduleRepository extends JpaRepository<MealSchedule, Long> {
  List<MealSchedule> findAllByOrderByDayOfWeekAscTimeAsc();
}