package com.innowise.userservice.dao;

import com.innowise.userservice.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardDao extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

  List<PaymentCard> findCardsByUserId(Long userId);

  Long countByUserId(Long userId);

  @Modifying
  @Query(value = "UPDATE payment_cards SET active = :status WHERE id = :id", nativeQuery = true)
  void setCardStatus(@Param("id") Long id, @Param("status") boolean status);

  @Modifying
  @Query("UPDATE PaymentCard pc SET pc.active = :status WHERE pc.user.id = :userId")
  void setCardsStatusByUserId(@Param("userId") Long userId, @Param("status") boolean status);

  boolean existsByNumberAndActiveTrue(String number);

  boolean existsByIdAndUserId(Long cardId, Long userId);
}
