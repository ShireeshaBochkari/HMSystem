package com.hostel;
import com.hostel.Dtos.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class HostelService {
  private final StudentRepository students; private final RoomRepository rooms;
  public HostelService(StudentRepository s, RoomRepository r) { students = s; rooms = r; }

  private StudentDto toDto(Student s) {
    return new StudentDto(s.id, s.name, s.email, s.phone, s.user == null ? null : s.user.username,
        s.room == null ? null : s.room.id, s.room == null ? null : s.room.roomNo);
  }
  public Map<String, Object> listStudents(int page, int size, String sort, String q) {
    String field = Set.of("id", "name", "email").contains(sort) ? sort : "id";
    Sort so = field.equals("id") ? Sort.by(Sort.Direction.DESC, "id") : Sort.by(field);
    Page<Student> p = students.findByNameContainingIgnoreCase(q, PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50), so));
    return Map.of("content", p.getContent().stream().map(this::toDto).toList(), "totalPages", p.getTotalPages(), "totalElements", p.getTotalElements());
  }
  public StudentDto saveStudent(Long id, StudentDto d) {
    Student s = id == null ? new Student() : students.findById(id).orElseThrow(() -> new ApiException(404, "Student not found"));
    s.name = d.name().trim(); s.email = d.email().trim(); s.phone = d.phone();
    if (d.roomId() == null) s.room = null;
    else {
      Room r = rooms.findById(d.roomId()).orElseThrow(() -> new ApiException(404, "Room not found"));
      boolean same = s.room != null && s.room.id.equals(r.id);
      if (!same && students.countByRoomId(r.id) >= r.capacity) throw new ApiException(400, "Room " + r.roomNo + " is full");
      s.room = r;
    }
    return toDto(students.save(s));
  }
  public void deleteStudent(Long id) {
    if (!students.existsById(id)) throw new ApiException(404, "Student not found");
    students.deleteById(id);
  }
  public List<RoomDto> listRooms() {
    return rooms.findAll(Sort.by("roomNo")).stream().map(r -> new RoomDto(r.id, r.roomNo, r.capacity, students.countByRoomId(r.id))).toList();
  }
  public RoomDto saveRoom(RoomDto d) {
    Room r = new Room(); r.roomNo = d.roomNo().trim(); r.capacity = d.capacity(); rooms.save(r);
    return new RoomDto(r.id, r.roomNo, r.capacity, 0);
  }
  public void deleteRoom(Long id) {
    if (students.countByRoomId(id) > 0) throw new ApiException(400, "Room has students; move them first");
    rooms.deleteById(id);
  }
}
