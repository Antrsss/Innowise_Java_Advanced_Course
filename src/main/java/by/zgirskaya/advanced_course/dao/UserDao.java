package by.zgirskaya.advanced_course.dao;

import by.zgirskaya.advanced_course.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDao extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
  @Modifying
  @Query("UPDATE User u SET u.active = :status WHERE u.id = :id")
  void setUserStatus(@Param("id") Long id, @Param("status") boolean status);
}
