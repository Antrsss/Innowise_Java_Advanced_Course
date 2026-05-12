package com.innowise.userservice.dao;

import com.innowise.userservice.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserDao extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

  @Query("SELECT u FROM User u LEFT JOIN FETCH u.cards WHERE u.id = :id AND u.active = true")
  Optional<User> findActiveUserByIdWithCards(Long id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT u FROM User u WHERE u.id = :id AND u.active = true")
  Optional<User> findByIdWithLock(Long id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT u FROM User u WHERE u.email = :email AND u.active = true")
  Optional<User> findByEmailWithLock(String email);

  @Modifying
  @Query("UPDATE User u SET u.active = :status WHERE u.id = :id")
  void setUserStatus(@Param("id") Long id, @Param("status") boolean status);

  boolean existsByEmailAndActiveTrue(String email);
}
