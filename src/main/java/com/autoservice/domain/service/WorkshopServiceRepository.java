package com.autoservice.domain.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkshopServiceRepository extends JpaRepository<WorkshopService, WorkshopServiceID> {
}

