package by.zgirskaya.advanced_course.specification;

import by.zgirskaya.advanced_course.entity.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {

  private UserSpecifications() {}

  public static Specification<User> hasName(String name) {
    return (root, query, cb) ->
        name == null ? null : cb.equal(root.get("name"), name);
  }

  public static Specification<User> hasSurname(String surname) {
    return (root, query, cb) ->
        surname == null ? null : cb.equal(root.get("surname"), surname);
  }

  public static Specification<User> isActive() {
    return (root, query, cb) -> cb.isTrue(root.get("active"));
  }
}
