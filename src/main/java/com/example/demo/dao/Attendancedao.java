package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Attendance;

public interface Attendancedao extends JpaRepository<Attendance, Integer> {
	
}
