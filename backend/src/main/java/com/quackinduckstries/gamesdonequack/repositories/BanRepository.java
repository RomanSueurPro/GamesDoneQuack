package com.quackinduckstries.gamesdonequack.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.quackinduckstries.gamesdonequack.entities.Ban;

public interface BanRepository extends JpaRepository<Ban, Long>{
	
	@Query("""
		    SELECT COUNT(b) > 0
		    FROM Ban b
		    WHERE b.user.id = :userId
		      AND b.startDate <= :now
		      AND (b.endDate IS NULL OR b.endDate > :now)
		""")
		boolean isUserCurrentlyBanned(@Param("userId") Long userId, @Param("now") LocalDate now);
	
	@Query("""
		    SELECT b
		    FROM Ban b
		    WHERE b.user.id = :userId
			  AND b.startDate <= :now
		      AND b.endDate >= :now
		    ORDER BY b.endDate DESC
		""")
	List<Ban> findActiveBansForUser(
		    @Param("userId") Long userId,
		    @Param("now") LocalDate now,
		    Pageable pageable
		);
	
	@Query("""
		    SELECT b
		    FROM Ban b
		    WHERE b.user.id = :userId
		      AND b.startDate <= CURRENT_DATE
		      AND (b.endDate IS NULL OR b.endDate > CURRENT_DATE)
		""")
		Optional<Ban> lastBanForUser(@Param("userId") Long userId);
	
		Optional<Ban> findById(Long id);
}		
