package by.zgirskaya.advanced_course.service;

import by.zgirskaya.advanced_course.entity.PaymentCard;
import by.zgirskaya.advanced_course.exception.CardServiceException;

import java.util.List;

public interface CardService {
  PaymentCard createCard(PaymentCard card) throws CardServiceException;
  PaymentCard findCardById(Long id) throws CardServiceException;
  List<PaymentCard> findCardsByUserId(Long id);
  void setCardStatus(Long id, boolean status);
  void deleteCard(Long id);
}
