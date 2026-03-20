package by.zgirskaya.advanced_course.service;

import by.zgirskaya.advanced_course.entity.PaymentCard;
import by.zgirskaya.advanced_course.exception.EntityNotFoundException;
import by.zgirskaya.advanced_course.exception.ResourceConflictException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CardService {
  PaymentCard createCard(PaymentCard card) throws EntityNotFoundException, ResourceConflictException;
  PaymentCard findCardById(Long id) throws EntityNotFoundException;
  List<PaymentCard> findCardsByUserId(Long id);
  Page<PaymentCard> findAll(String name, String surname, Pageable pageable);
  void setCardStatus(Long id, boolean status) throws EntityNotFoundException;
}
