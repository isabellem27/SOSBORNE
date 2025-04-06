package com.sosborne.service;

import com.sosborne.enu.PaymentStatus;
import com.sosborne.model.dto.PaymentRequestDTO;
import com.sosborne.model.entity.Payment;
import com.sosborne.model.entity.Reservation;
import com.sosborne.model.entity.User;
import com.sosborne.repository.PaymentRepository;
import com.sosborne.repository.ReservationRepository;
import com.sosborne.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.net.RequestOptions;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@NoArgsConstructor
//@RequiredArgsConstructor
@Getter
@Setter
public class PaymentService {

    private PaymentRepository paymentRepository;
    private ReservationRepository reservationRepository;
    private UserRepository userRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, ReservationRepository reservationRepository,
                          UserRepository userRepository, @Value("${stripe.api.key}") String apiKey) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        Stripe.apiKey = apiKey;
    }

    /**
     * Créer un paiement avec Stripe
     */
    public String createPayment(PaymentRequestDTO paymentRequest) throws StripeException {
        // Fetch the user
        User user = userRepository.findById(paymentRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Fetch the reservation
        Reservation reservation = reservationRepository.findById(paymentRequest.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        // Create Stripe PaymentMethod
        RequestOptions requestOptions = RequestOptions.builder()
                .setApiKey(Stripe.apiKey)
                .build();

        Map<String, Object> paymentMethodParams = new HashMap<>();
        Map<String, Object> cardParams = new HashMap<>();
        cardParams.put("token", "tok_visa"); // Use test token
        paymentMethodParams.put("type", paymentRequest.getPaymentMethod());
        paymentMethodParams.put("card", cardParams);

        PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams, requestOptions);

        // Create PaymentIntent
        Map<String, Object> params = new HashMap<>();
        params.put("amount", paymentRequest.getAmount().multiply(new BigDecimal(100)).intValue()); // Convert to cents
        params.put("currency", paymentRequest.getCurrency());
        params.put("payment_method", paymentMethod.getId());
        params.put("confirm", true);

        PaymentIntent paymentIntent = PaymentIntent.create(params, requestOptions);

        // Save payment in database
        Payment payment = new Payment();
        payment.setPaymentIntentId(paymentIntent.getId());
        System.out.println("Payment created: " + paymentIntent.getId() + ", Status: " + paymentIntent.getStatus());
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency(paymentRequest.getCurrency());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setUser(user);
        payment.setReservation(reservation);
        paymentRepository.save(payment);

        return paymentIntent.getClientSecret();
    }

    /**
     * Valider le statut du paiement (Stripe Webhooks ou Verification)
     */
    public PaymentStatus validatePayment(String paymentIntentId) throws StripeException {
        // Retrieve PaymentIntent from Stripe
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

        // Fetch payment from DB
        Payment payment = paymentRepository.findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        // Update status based on Stripe response
        if ("succeeded".equals(paymentIntent.getStatus())) {
            payment.setStatus(PaymentStatus.COMPLETED);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);
        return payment.getStatus();
    }

}
