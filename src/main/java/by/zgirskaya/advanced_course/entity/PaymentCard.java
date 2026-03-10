package by.zgirskaya.advanced_course.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_cards")
public class PaymentCard extends AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  private String number;
  private String holder;
  private String expirationDate;
  private boolean active;
}
