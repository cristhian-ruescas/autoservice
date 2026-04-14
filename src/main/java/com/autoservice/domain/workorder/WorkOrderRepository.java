package com.autoservice.domain.workorder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, WorkOrderID> {
}

