package by.zgirskaya.advanced_course.specification;

import by.zgirskaya.advanced_course.entity.PaymentCard;
import by.zgirskaya.advanced_course.entity.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class CardSpecifications {
  public static Specification<PaymentCard> hasUserName(String name) {
    return (root, query, cb) -> {
      if (name == null || name.isEmpty()) return null;
      Join<PaymentCard, User> userJoin = root.join("user");
      return cb.equal(userJoin.get("name"), name);
    };
  }

  public static Specification<PaymentCard> hasUserSurname(String surname) {
    return (root, query, cb) -> {
      if (surname == null || surname.isEmpty()) return null;
      Join<PaymentCard, User> userJoin = root.join("user");
      return cb.equal(userJoin.get("surname"), surname);
    };
  }
}
