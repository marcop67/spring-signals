package com.copytrading.bot.repository;

import com.copytrading.bot.model.UserOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserOrdersRepository extends JpaRepository<UserOrders, Integer> {

    @Query("SELECT o FROM UserOrders o WHERE o.order_id = :orderId AND (( o.orderStatus = 'BUDGET_OVERFLOW' OR o.orderStatus = 'POSITION_IDX' OR o.orderStatus = NULL)" +
            " OR (o.retries > 0 OR o.retries = NULL))")
    List<UserOrders> findUserOrdersToRetry(@Param("orderId") String orderId);
}
