package com.themall.paymentservice.repository;

import com.themall.paymentservice.entity.ProcessedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, String> {

    @Query("SELECT COUNT(p) FROM ProcessedMessage p WHERE p.messageId = ?1")
    int existsByMessageId(String messageId);
}