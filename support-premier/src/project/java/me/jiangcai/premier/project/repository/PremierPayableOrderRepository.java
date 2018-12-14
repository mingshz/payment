package me.jiangcai.premier.project.repository;

import me.jiangcai.premier.project.entity.PremierPayableOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PremierPayableOrderRepository extends JpaRepository<PremierPayableOrder, Long>, JpaSpecificationExecutor<PremierPayableOrder> {
}
