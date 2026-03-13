package by.zgirskaya.advanced_course.service;

import by.zgirskaya.advanced_course.entity.PaymentCard;
import by.zgirskaya.advanced_course.exception.CardServiceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CardService {
  PaymentCard createCard(PaymentCard card) throws CardServiceException;
  PaymentCard findCardById(Long id) throws CardServiceException;
  List<PaymentCard> findCardsByUserId(Long id);
  Page<PaymentCard> findAll(String name, String surname, Pageable pageable);
  void setCardStatus(Long id, boolean status);
  void deleteCard(Long id);
}
