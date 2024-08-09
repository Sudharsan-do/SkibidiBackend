package com.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.models.UserDetails;

@Repository
public interface UserRepository extends JpaRepository<UserDetails, Long>{
	
	UserDetails findByEmail(String email);
	UserDetails findByUsername(String username);
	
}
