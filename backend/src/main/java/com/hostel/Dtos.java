package com.hostel;
import jakarta.validation.constraints.*;
public class Dtos {
  public record LoginDto(@NotBlank(message = "Required") String username, @NotBlank(message = "Required") String password) {}
  public record RegisterDto(@NotBlank @Size(min = 3, max = 30, message = "3-30 characters") String username,
                            @NotBlank @Size(min = 6, message = "Min 6 characters") String password,
                            @NotBlank(message = "Name is required") String name,
                            @NotBlank(message = "Email is required") @Email(message = "Invalid email") String email,
                            @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits") String phone) {}
  public record StudentDto(Long id, @NotBlank(message = "Name is required") String name,
                           @NotBlank(message = "Email is required") @Email(message = "Invalid email") String email,
                           @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits") String phone,
                           String username, Long roomId, String roomNo) {}
  public record RoomDto(Long id, @NotBlank(message = "Room number required") String roomNo,
                        @Min(value = 1, message = "Min 1") @Max(value = 10, message = "Max 10") int capacity, long occupied) {}
  public record HostelerCreateDto(@NotBlank @Size(min = 3, max = 30) String username,
                                  @NotBlank @Size(min = 6, message = "Min 6 characters") String password,
                                  @NotBlank String name, @NotBlank @Email String email,
                                  @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits") String phone,
                                  Long roomId) {}
  public record MessageDto(Long id, Long recipientId, String recipient, String title, String body, java.time.LocalDateTime sentAt) {}
  public record MessageCreateDto(@NotNull Long recipientId, @NotBlank @Size(max = 100) String title,
                                 @NotBlank @Size(max = 4000) String body) {}
  public record FeeDto(Long id, Long studentId, String hosteler, String period, java.math.BigDecimal amount,
                       java.time.LocalDate dueDate, String status, java.time.LocalDateTime createdAt) {}
  public record FeeCreateDto(@NotNull Long studentId, @NotBlank @Size(max = 40) String period,
                             @NotNull @DecimalMin("0.01") java.math.BigDecimal amount, @NotNull java.time.LocalDate dueDate) {}
  public record FeeReviewDto(@NotBlank @Pattern(regexp = "PAID|DECLINED") String status) {}
  public record MealDto(Long id, @NotBlank String dayOfWeek, @NotBlank String meal,
                        @NotNull java.time.LocalTime time, @NotBlank @Size(max = 500) String menu) {}
  public record ComplaintDto(Long id, Long studentId, String hosteler, String title, String details,
                             String status, String response, java.time.LocalDateTime createdAt) {}
  public record ComplaintCreateDto(@NotBlank @Size(max = 120) String title, @NotBlank @Size(max = 4000) String details) {}
  public record ComplaintUpdateDto(@NotBlank @Pattern(regexp = "OPEN|IN_PROGRESS|RESOLVED") String status,
                                   @Size(max = 2000) String response) {}
}
