package by.zgirskaya.advanced_course.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
  private Long id;

  @NotBlank(message = "First name is required")
  @Size(min = 2, max = 50)
  private String name;

  @NotBlank(message = "Surname is required")
  private String surname;

  @Email(message = "Invalid email format")
  private String email;

  private boolean active;
  private List<PaymentCardDto> cards;
}
