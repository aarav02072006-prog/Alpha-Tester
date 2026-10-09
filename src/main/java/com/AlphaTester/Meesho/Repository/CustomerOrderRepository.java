package com.AlphaTester.Meesho.Repository;

import com.AlphaTester.Meesho.Model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder,Long> {
}
