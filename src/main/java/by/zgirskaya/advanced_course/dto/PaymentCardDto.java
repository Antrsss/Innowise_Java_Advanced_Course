package by.zgirskaya.advanced_course.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class PaymentCardDto {
  private Long id;

  @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
  private String number;

  @NotBlank(message = "Holder name is required")
  @Pattern(regexp = "^[A-Z ]+$", message = "Only uppercase Latin letters allowed")
  private String holder;

  @NotBlank(message = "Expiration date is required")
  @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Use MM/YY format")
  private String expirationDate;
  private boolean active;

  private Long userId;
}
