package com.copytrading.bot.repository;

import com.copytrading.bot.model.Order;
import com.copytrading.bot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT o FROM Order o WHERE o.ticker = :ticker AND  o.active = true AND o.createDate BETWEEN :start AND :end")
    List<Order> findActiveOrdersByPairBetween(@Param("ticker") String ticker, @Param("start") LocalDateTime start , @Param("end") LocalDateTime end);
}
