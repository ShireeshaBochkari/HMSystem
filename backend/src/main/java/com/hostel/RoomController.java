package com.hostel;
import com.hostel.Dtos.RoomDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/rooms")
public class RoomController {
  private final HostelService svc;
  public RoomController(HostelService svc) { this.svc = svc; }
  @GetMapping public List<RoomDto> list() { return svc.listRooms(); }
  @PostMapping public ResponseEntity<RoomDto> create(@Valid @RequestBody RoomDto d) { return ResponseEntity.status(201).body(svc.saveRoom(d)); }
  @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { svc.deleteRoom(id); return ResponseEntity.noContent().build(); }
}
