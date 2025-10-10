package com.icsd.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.icsd.model.Admin;

public interface AdminRepo extends JpaRepository<Admin, Integer> {

	Optional<Admin> findByEmailIdAndPassword(String emailId, String password);

	Optional<Admin> findByEmailId(String emailId);

}
