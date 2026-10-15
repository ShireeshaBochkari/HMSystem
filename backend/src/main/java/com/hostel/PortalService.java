package com.hostel;
import com.hostel.Dtos.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.DayOfWeek;
import java.util.List;

@Service
public class PortalService {
  private final StudentRepository students;
  private final UserRepository users;
  private final RoomRepository rooms;
  private final HostelMessageRepository messages;
  private final FeePaymentRepository fees;
  private final MealScheduleRepository meals;
  private final ComplaintRepository complaints;
  private final BCryptPasswordEncoder encoder;

  public PortalService(StudentRepository students, UserRepository users, RoomRepository rooms,
                       HostelMessageRepository messages, FeePaymentRepository fees,
                       MealScheduleRepository meals, ComplaintRepository complaints,
                       BCryptPasswordEncoder encoder) {
    this.students = students; this.users = users; this.rooms = rooms; this.messages = messages;
    this.fees = fees; this.meals = meals; this.complaints = complaints; this.encoder = encoder;
  }

  private Student resident(String username) {
    return students.findByUserUsername(username).orElseThrow(() -> new ApiException(404, "Hosteler profile not found"));
  }

  private StudentDto residentDto(Student s) {
    return new StudentDto(s.id, s.name, s.email, s.phone, s.user == null ? null : s.user.username,
        s.room == null ? null : s.room.id, s.room == null ? null : s.room.roomNo);
  }

  private void assignRoom(Student student, Long roomId) {
    if (roomId == null) { student.room = null; return; }
    Room room = rooms.findById(roomId).orElseThrow(() -> new ApiException(404, "Room not found"));
    boolean sameRoom = student.room != null && student.room.id.equals(room.id);
    if (!sameRoom && students.countByRoomId(room.id) >= room.capacity) throw new ApiException(400, "Room " + room.roomNo + " is full");
    student.room = room;
  }

  @Transactional(readOnly = true)
  public StudentDto profile(String username) { return residentDto(resident(username)); }

  @Transactional(readOnly = true)
  public List<StudentDto> hostelers() {
    return students.findAll().stream()
        .filter(s -> s.user != null && (s.user.role == Role.HOSTELER || s.user.role == Role.STUDENT))
        .map(this::residentDto).toList();
  }

  @Transactional
  public StudentDto createHosteler(HostelerCreateDto d) {
    if (users.findByUsername(d.username().trim()).isPresent()) throw new ApiException(409, "Username already taken");
    User user = users.save(new User(d.username().trim(), encoder.encode(d.password()), Role.HOSTELER));
    Student student = new Student(); student.user = user; student.name = d.name().trim();
    student.email = d.email().trim(); student.phone = d.phone(); assignRoom(student, d.roomId());
    return residentDto(students.save(student));
  }

  @Transactional(readOnly = true)
  public List<MessageDto> listMessages(String username, boolean owner) {
    List<HostelMessage> found = owner ? messages.findAllByOrderBySentAtDesc() : messages.findByRecipientUserUsernameOrderBySentAtDesc(username);
    return found.stream().map(m -> new MessageDto(m.id, m.recipient.id, m.recipient.name, m.title, m.body, m.sentAt)).toList();
  }

  @Transactional
  public MessageDto sendMessage(MessageCreateDto d) {
    Student recipient = students.findById(d.recipientId()).orElseThrow(() -> new ApiException(404, "Hosteler not found"));
    if (recipient.user == null) throw new ApiException(400, "A login account is required to receive messages");
    HostelMessage message = new HostelMessage(); message.recipient = recipient;
    message.title = d.title().trim(); message.body = d.body().trim();
    message = messages.save(message);
    return new MessageDto(message.id, recipient.id, recipient.name, message.title, message.body, message.sentAt);
  }

  private FeeDto feeDto(FeePayment f) {
    return new FeeDto(f.id, f.student.id, f.student.name, f.period, f.amount, f.dueDate, f.status, f.createdAt);
  }

  @Transactional(readOnly = true)
  public List<FeeDto> listFees(String username, boolean owner) {
    List<FeePayment> found = owner ? fees.findAllByOrderByCreatedAtDesc() : fees.findByStudentUserUsernameOrderByCreatedAtDesc(username);
    return found.stream().map(this::feeDto).toList();
  }

  @Transactional
  public FeeDto createFee(FeeCreateDto d) {
    Student student = students.findById(d.studentId()).orElseThrow(() -> new ApiException(404, "Hosteler not found"));
    if (student.user == null) throw new ApiException(400, "A login account is required for fee tracking");
    FeePayment fee = new FeePayment(); fee.student = student; fee.period = d.period().trim();
    fee.amount = d.amount(); fee.dueDate = d.dueDate();
    return feeDto(fees.save(fee));
  }

  @Transactional
  public FeeDto submitPayment(Long id, String username) {
    FeePayment fee = fees.findById(id).orElseThrow(() -> new ApiException(404, "Fee not found"));
    if (fee.student.user == null || !fee.student.user.username.equals(username)) throw new ApiException(403, "This fee belongs to another hosteler");
    if (fee.status.equals("PAID")) throw new ApiException(400, "This fee is already paid");
    fee.status = "PAYMENT_PENDING";
    return feeDto(fees.save(fee));
  }

  @Transactional
  public FeeDto reviewPayment(Long id, FeeReviewDto d) {
    FeePayment fee = fees.findById(id).orElseThrow(() -> new ApiException(404, "Fee not found"));
    fee.status = d.status();
    return feeDto(fees.save(fee));
  }

  @Transactional(readOnly = true)
  public List<MealDto> listMeals() {
    return meals.findAllByOrderByDayOfWeekAscTimeAsc().stream()
        .map(m -> new MealDto(m.id, m.dayOfWeek, m.meal, m.time, m.menu)).toList();
  }

  @Transactional
  public MealDto saveMeal(Long id, MealDto d) {
    String day;
    try { day = DayOfWeek.valueOf(d.dayOfWeek().trim().toUpperCase()).name(); }
    catch (IllegalArgumentException e) { throw new ApiException(400, "Choose a valid day of the week"); }
    String mealName = d.meal().trim().toUpperCase();
    if (!List.of("BREAKFAST", "LUNCH", "DINNER", "SNACK").contains(mealName)) throw new ApiException(400, "Choose a valid meal");
    MealSchedule meal = id == null ? new MealSchedule() : meals.findById(id).orElseThrow(() -> new ApiException(404, "Meal entry not found"));
    meal.dayOfWeek = day; meal.meal = mealName; meal.time = d.time(); meal.menu = d.menu().trim();
    meal = meals.save(meal);
    return new MealDto(meal.id, meal.dayOfWeek, meal.meal, meal.time, meal.menu);
  }

  @Transactional
  public void deleteMeal(Long id) {
    if (!meals.existsById(id)) throw new ApiException(404, "Meal entry not found");
    meals.deleteById(id);
  }

  private ComplaintDto complaintDto(Complaint c) {
    return new ComplaintDto(c.id, c.student.id, c.student.name, c.title, c.details, c.status, c.response, c.createdAt);
  }

  @Transactional(readOnly = true)
  public List<ComplaintDto> listComplaints(String username, boolean owner) {
    List<Complaint> found = owner ? complaints.findAllByOrderByCreatedAtDesc() : complaints.findByStudentUserUsernameOrderByCreatedAtDesc(username);
    return found.stream().map(this::complaintDto).toList();
  }

  @Transactional
  public ComplaintDto createComplaint(String username, ComplaintCreateDto d) {
    Complaint complaint = new Complaint(); complaint.student = resident(username);
    complaint.title = d.title().trim(); complaint.details = d.details().trim();
    return complaintDto(complaints.save(complaint));
  }

  @Transactional
  public ComplaintDto updateComplaint(Long id, ComplaintUpdateDto d) {
    Complaint complaint = complaints.findById(id).orElseThrow(() -> new ApiException(404, "Complaint not found"));
    complaint.status = d.status(); complaint.response = d.response() == null ? null : d.response().trim();
    return complaintDto(complaints.save(complaint));
  }
}