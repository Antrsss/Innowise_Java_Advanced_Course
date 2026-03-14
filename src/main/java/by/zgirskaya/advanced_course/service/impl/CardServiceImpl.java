package by.zgirskaya.advanced_course.service.impl;

import by.zgirskaya.advanced_course.dao.CardDao;
import by.zgirskaya.advanced_course.dao.UserDao;
import by.zgirskaya.advanced_course.entity.PaymentCard;
import by.zgirskaya.advanced_course.entity.User;
import by.zgirskaya.advanced_course.exception.CardServiceException;
import by.zgirskaya.advanced_course.service.CardService;
import by.zgirskaya.advanced_course.specification.CardSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

  private final UserDao userDao;
  private final CardDao cardDao;

  @Override
  @Transactional
  @CacheEvict(value = "users", key = "#card.user.id")
  public PaymentCard createCard(PaymentCard card) throws CardServiceException {
    if (card.getUser() == null || card.getUser().getId() == null) {
      throw new CardServiceException("Card must be assigned to a user!");
    }

    User user = userDao.findById(card.getUser().getId())
        .orElseThrow(() -> new CardServiceException("User not found!"));

    if (user.getCards().size() >= 5) {
      throw new CardServiceException("User already has 5 cards. Limit reached!");
    }

    return cardDao.save(card);
  }

  @Override
  public PaymentCard findCardById(Long id) throws CardServiceException {
    return cardDao.findById(id)
        .orElseThrow(() -> new CardServiceException("Card not found with id: " + id));
  }

  @Override
  public List<PaymentCard> findCardsByUserId(Long id) {
    return cardDao.findCardsByUserId(id);
  }

  @Override
  public Page<PaymentCard> findAll(String name, String surname, Pageable pageable) {
    Specification<PaymentCard> spec = Specification
        .where(CardSpecifications.hasUserName(name))
        .and(CardSpecifications.hasUserSurname(surname));

    return cardDao.findAll(spec, pageable);
  }

  @Override
  @Transactional
  public void setCardStatus(Long id, boolean status) {
    cardDao.setCardStatus(id, status);
  }

  @Override
  @Transactional
  public void deleteCard(Long id) {
    cardDao.deleteById(id);
  }
}
