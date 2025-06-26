package com.example.demo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Request;
import com.example.demo.entity.Users;

@Repository
public interface Requestdao extends JpaRepository<Request, Long> {
	// 特定のUserに紐づく全てのリクエストを検索
	List<Request> findByUser(Users user);

	@Query("SELECT r FROM Request r LEFT JOIN FETCH r.user LEFT JOIN FETCH r.attendances") // Requestエンティティ内のフィールド名に合わせる
	List<Request> findAllWithUserAndAttendance();
}
