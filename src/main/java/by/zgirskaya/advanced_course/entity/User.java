package by.zgirskaya.advanced_course.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String surname;
  private LocalDate birthDate;
  private String email;
  private boolean active;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<PaymentCard> cards;
}
