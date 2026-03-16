package by.zgirskaya.advanced_course.service.impl;

import by.zgirskaya.advanced_course.dao.CardDao;
import by.zgirskaya.advanced_course.dao.UserDao;
import by.zgirskaya.advanced_course.entity.PaymentCard;
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
    Long userId = card.getUser().getId();

    userDao.findByIdWithLock(userId)
        .orElseThrow(() -> new CardServiceException("User not found or inactive!"));

    if (cardDao.countByUserId(userId) >= 5) {
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
  @CacheEvict(value = "users", key = "#result")
  public Long setCardStatus(Long id, boolean status) throws CardServiceException {
    PaymentCard card = cardDao.findById(id)
        .orElseThrow(() -> new CardServiceException("Card not found"));
    cardDao.setCardStatus(id, status);

    return card.getUser().getId();
  }
}
