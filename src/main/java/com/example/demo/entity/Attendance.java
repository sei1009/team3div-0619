package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "attendance")
public class Attendance {
	private int id;
	private LocalTime start_time;
	private LocalTime end_time;
	private LocalDate date;
	private int user_id;
	private int request_id;
}
