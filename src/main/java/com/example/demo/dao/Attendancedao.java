package com.example.demo.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Attendance;

@Repository
public interface Attendancedao extends JpaRepository<Attendance, Long> {

	Attendance findByUseridAndDate(long id, LocalDate date);

	Optional<Attendance> findByRequestid(Long id);

	List<Attendance> findByUseridAndDateBetween(Long id, LocalDate start, LocalDate end);

	List<Attendance> findAllByRequestid(Long id);

	List<Attendance> findByUserid(Long id);

	List<Attendance> findByRequestidIsNull();

}
