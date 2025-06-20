package com.example.demo.dao;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Attendance;

@Repository
public interface Attendancedao extends JpaRepository<Attendance, Long> {

	Attendance findByUseridAndDate(long id, LocalDate date);
}
