package com.example.demo.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Attendance;

@Repository
public interface Attendancedao extends JpaRepository<Attendance, Long> {
	List<Attendance> findByRequestId(Long requestId);

	List<Attendance> findByUsersId(Long userId);

	Attendance findByUsersIdAndDate(long id, LocalDate date);
}