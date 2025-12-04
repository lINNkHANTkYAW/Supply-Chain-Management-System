package com.example.SupplyChainManagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.SupplyChainManagement.model.Distributor;
import com.example.SupplyChainManagement.model.User;

@Repository
public interface DistributorRepository extends JpaRepository<Distributor, Long> {
    Optional<Distributor> findByUser(User user);

	Optional<Distributor> findByUser_UserId(Long userId);
	
	@Query("SELECT d.companyName, SUM(oi.quantity * p.price) as revenue " +
		       "FROM DistriOrder o JOIN o.orderItems oi " +
		       "JOIN oi.manuProduct p " +
		       "JOIN o.distributor d " +
		       "GROUP BY d.companyName " +
		       "ORDER BY revenue DESC")
		List<Object[]> findRevenueByDistributor();
		
}

