package com.hostel;
import com.hostel.Dtos.StudentDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/students")
public class StudentController {
  private final HostelService svc;
  public StudentController(HostelService svc) { this.svc = svc; }
  @GetMapping public Map<String, Object> list(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
                                              @RequestParam(defaultValue = "id") String sort, @RequestParam(defaultValue = "") String q) { return svc.listStudents(page, size, sort, q); }
  @PostMapping public ResponseEntity<StudentDto> create(@Valid @RequestBody StudentDto d) { return ResponseEntity.status(201).body(svc.saveStudent(null, d)); }
  @PutMapping("/{id}") public StudentDto update(@PathVariable Long id, @Valid @RequestBody StudentDto d) { return svc.saveStudent(id, d); }
  @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { svc.deleteStudent(id); return ResponseEntity.noContent().build(); }
}
