package com.recipefind.backend.dao;

import com.recipefind.backend.entity.MealPlanEntity;
import com.recipefind.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlanEntity, Integer> {
    MealPlanEntity getMealPlanEntityByUserAndPlannedDate (User user, Date date);

    List<MealPlanEntity> getMealPlanEntityByUserAndPlannedDateBetween (User user, Date startDate, Date endDate);

}
