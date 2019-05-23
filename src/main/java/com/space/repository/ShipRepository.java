package com.space.repository;

import com.space.model.Ship;
import org.springframework.data.jpa.repository.*;

public interface ShipRepository extends JpaRepository<Ship, Long> {
}
