package me.jiangcai.demo.project.repository;

import me.jiangcai.demo.project.entity.DemoTradeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author CJ
 */
public interface DemoTradeOrderRepository extends JpaRepository<DemoTradeOrder, Long> {
}
