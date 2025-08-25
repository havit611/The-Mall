package com.themall.paymentservice.repository;

import com.themall.paymentservice.entity.Payment;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends CassandraRepository<Payment, String> {

    // 修正方法名（原来是 findByorderId，应该是 findByOrderId）
    @Query("SELECT * FROM payments WHERE order_id = ?0 ALLOW FILTERING")
    Optional<Payment> findByOrderId(String orderId);

    Payment save(Payment payment);

    Optional<Payment> findByorderId(String orderId);
}