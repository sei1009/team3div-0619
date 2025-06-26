package com.example.demo.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.Request;
import com.example.demo.entity.Users;

@Repository
public interface Attendancedao extends JpaRepository<Attendance, Long> {

	// Userオブジェクトと日付で勤怠を検索
	Attendance findByUserAndDate(Users user, LocalDate date);

	// Requestオブジェクトで勤怠を検索
	Optional<Attendance> findByRequest(Request request);

	// 特定のUserと日付範囲で勤怠リストを検索
	List<Attendance> findByUserAndDateBetween(Users user, LocalDate start, LocalDate end);

	// 特定のRequestに紐づく全ての勤怠を検索
	List<Attendance> findAllByRequest(Request request);

	// 特定のUserに紐づく全ての勤怠を検索
	List<Attendance> findByUser(Users user);
	
	List<Attendance> findByRequestIsNull();
	
	@Query("SELECT a FROM Attendance a WHERE a.request.id = :requestId")
	Optional<Attendance> findByRequestId(@Param("requestId") Long requestId);
}
