package by.zgirskaya.advanced_course.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payment_cards")
@Getter @Setter
public class PaymentCard extends AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  private String number;
  private String holder;
  private String expirationDate;
  private boolean active;
}
