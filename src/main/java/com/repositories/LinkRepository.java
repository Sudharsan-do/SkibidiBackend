package com.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.models.Link;
import com.models.UserDetails;

public interface LinkRepository extends JpaRepository<Link, Long>{
	
	public Link findByUserDetails(UserDetails user);
	
}
