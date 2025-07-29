package com.testproject.test.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.testproject.test.domain.AdminUser;
import com.testproject.test.enums.Role;

@Repository
public interface TestRepo extends JpaRepository<AdminUser, Long>{
	Optional<AdminUser> findByEmail(String email);
	Optional<AdminUser> findByUsername(String username);
	List<AdminUser> findByAccessRole(Role accessRole);
	Page<AdminUser> findByAccessRole(Role accessRole, org.springframework.data.domain.Pageable pageable);
	@Query("SELECT u FROM AdminUser u WHERE u.accessRole = :role AND " +
		       "(LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		       "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
		Page<AdminUser> searchByKeywordAndRole(@Param("keyword") String keyword, @Param("role") Role role, org.springframework.data.domain.Pageable pageable);

}
