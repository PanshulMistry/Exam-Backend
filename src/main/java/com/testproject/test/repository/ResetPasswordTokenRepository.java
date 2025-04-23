package com.testproject.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.testproject.test.proxy.ResetPasswordToken;


@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, String> {

}
