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
  private String holder;

  private String expirationDate;
  private boolean active;

  private Long userId;
}
