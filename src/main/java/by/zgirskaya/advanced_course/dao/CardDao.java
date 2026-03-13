package by.zgirskaya.advanced_course.dao;

import by.zgirskaya.advanced_course.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardDao extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

  List<PaymentCard> findCardsByUserId(@Param("userId") Long userId);

  @Modifying
  @Query(value = "UPDATE payment_cards SET active = :status WHERE id = :id", nativeQuery = true)
  void setCardStatus(@Param("id") Long id, @Param("status") boolean status);
}
