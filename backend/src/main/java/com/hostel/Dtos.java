package com.hostel;
import jakarta.validation.constraints.*;
public class Dtos {
  public record LoginDto(@NotBlank(message = "Required") String username, @NotBlank(message = "Required") String password) {}
  public record RegisterDto(@NotBlank @Size(min = 3, max = 30, message = "3-30 characters") String username,
                            @NotBlank @Size(min = 6, message = "Min 6 characters") String password) {}
  public record StudentDto(Long id, @NotBlank(message = "Name is required") String name,
                           @NotBlank(message = "Email is required") @Email(message = "Invalid email") String email,
                           @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits") String phone,
                           Long roomId, String roomNo) {}
  public record RoomDto(Long id, @NotBlank(message = "Room number required") String roomNo,
                        @Min(value = 1, message = "Min 1") @Max(value = 10, message = "Max 10") int capacity, long occupied) {}
}
