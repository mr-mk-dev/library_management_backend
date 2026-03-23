package com.hacktropia.repository;

import com.hacktropia.modal.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    Boolean existsByPlanCode(String planCode);

    SubscriptionPlan findByPlanCode(String planCode);
}
