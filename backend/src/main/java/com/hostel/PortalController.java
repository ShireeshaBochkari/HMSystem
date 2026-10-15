package com.hostel;
import com.hostel.Dtos.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PortalController {
  private final PortalService portal;
  public PortalController(PortalService portal) { this.portal = portal; }

  @GetMapping("/me") public StudentDto profile(@RequestAttribute("username") String username) { return portal.profile(username); }
  @GetMapping("/hostelers") public List<StudentDto> hostelers() { return portal.hostelers(); }
  @PostMapping("/hostelers") @ResponseStatus(HttpStatus.CREATED)
  public StudentDto createHosteler(@Valid @RequestBody HostelerCreateDto d) { return portal.createHosteler(d); }

  @GetMapping("/messages") public List<MessageDto> messages(@RequestAttribute("username") String username,
                                                               @RequestAttribute("role") String role) {
    return portal.listMessages(username, role.equals("OWNER") || role.equals("ADMIN"));
  }
  @PostMapping("/messages") @ResponseStatus(HttpStatus.CREATED)
  public MessageDto sendMessage(@Valid @RequestBody MessageCreateDto d) { return portal.sendMessage(d); }

  @GetMapping("/fees") public List<FeeDto> fees(@RequestAttribute("username") String username,
                                                 @RequestAttribute("role") String role) {
    return portal.listFees(username, role.equals("OWNER") || role.equals("ADMIN"));
  }
  @PostMapping("/fees") @ResponseStatus(HttpStatus.CREATED)
  public FeeDto createFee(@Valid @RequestBody FeeCreateDto d) { return portal.createFee(d); }
  @PostMapping("/fees/{id}/payment") public FeeDto submitPayment(@PathVariable Long id, @RequestAttribute("username") String username) {
    return portal.submitPayment(id, username);
  }
  @PutMapping("/fees/{id}/verify") public FeeDto reviewPayment(@PathVariable Long id, @Valid @RequestBody FeeReviewDto d) {
    return portal.reviewPayment(id, d);
  }

  @GetMapping("/schedule") public List<MealDto> meals() { return portal.listMeals(); }
  @PostMapping("/schedule") @ResponseStatus(HttpStatus.CREATED)
  public MealDto createMeal(@Valid @RequestBody MealDto d) { return portal.saveMeal(null, d); }
  @PutMapping("/schedule/{id}") public MealDto updateMeal(@PathVariable Long id, @Valid @RequestBody MealDto d) { return portal.saveMeal(id, d); }
  @DeleteMapping("/schedule/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteMeal(@PathVariable Long id) { portal.deleteMeal(id); }

  @GetMapping("/complaints") public List<ComplaintDto> complaints(@RequestAttribute("username") String username,
                                                                     @RequestAttribute("role") String role) {
    return portal.listComplaints(username, role.equals("OWNER") || role.equals("ADMIN"));
  }
  @PostMapping("/complaints") @ResponseStatus(HttpStatus.CREATED)
  public ComplaintDto createComplaint(@RequestAttribute("username") String username, @Valid @RequestBody ComplaintCreateDto d) {
    return portal.createComplaint(username, d);
  }
  @PutMapping("/complaints/{id}") public ComplaintDto updateComplaint(@PathVariable Long id, @Valid @RequestBody ComplaintUpdateDto d) {
    return portal.updateComplaint(id, d);
  }
}