package com.sosborne.repository;

import com.sosborne.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentIntentId(String id);

    Optional<Payment> findById(UUID paymentId);
}

