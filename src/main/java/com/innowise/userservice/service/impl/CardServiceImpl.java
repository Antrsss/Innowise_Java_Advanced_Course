package com.innowise.userservice.service.impl;

import com.innowise.userservice.dao.CardDao;
import com.innowise.userservice.dao.UserDao;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.exception.ResourceConflictException;
import com.innowise.userservice.service.CardService;
import com.innowise.userservice.specification.CardSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

  private final UserDao userDao;
  private final CardDao cardDao;
  private final CacheManager cacheManager;

  @Override
  @CacheEvict(value = "users", key = "#card.user.id")
  public PaymentCard createCard(PaymentCard card) throws EntityNotFoundException, ResourceConflictException {
    Long userId = card.getUser().getId();

    userDao.findByIdWithLock(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    if(cardDao.existsByNumberAndActiveTrue(card.getNumber())) {
      throw new ResourceConflictException("Card with that number already exists");
    }

    if (cardDao.countByUserId(userId) >= 5) {
      throw new ResourceConflictException("User already has 5 cards. Limit reached!");
    }

    return cardDao.save(card);
  }

  @Override
  @Transactional(readOnly = true)
  public PaymentCard findCardById(Long id) throws EntityNotFoundException {
    return cardDao.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Card not found with id: " + id));
  }

  @Override
  @Transactional(readOnly = true)
  public List<PaymentCard> findCardsByUserId(Long id) {
    return cardDao.findCardsByUserId(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PaymentCard> findAll(String name, String surname, Pageable pageable) {
    Specification<PaymentCard> spec = Specification
        .where(CardSpecifications.hasUserName(name))
        .and(CardSpecifications.hasUserSurname(surname));

    return cardDao.findAll(spec, pageable);
  }

  @Override
  public void setCardStatus(Long id, boolean status) throws EntityNotFoundException {
    PaymentCard card = cardDao.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Card not found"));
    cardDao.setCardStatus(id, status);

    Cache usersCache = cacheManager.getCache("users");
    if (usersCache != null) {
      usersCache.evict(card.getUser().getId());
    }
  }
}
