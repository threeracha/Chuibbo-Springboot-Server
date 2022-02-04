package com.sriracha.ChuibboServer.repository;

import com.sriracha.ChuibboServer.model.entity.CareerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerTypeRepository extends JpaRepository<CareerType, Long> {
}
