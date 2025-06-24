package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Request;

@Repository
public interface Requestdao extends JpaRepository<Request, Long> {
	List<Request> findByUsersId(Long userId);
}
