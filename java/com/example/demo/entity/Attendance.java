package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "attendance")
public class Attendance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private LocalTime start_time;
	private LocalTime end_time;
	
	@Column(nullable = false)
    private LocalDate date;
	
	@ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "id", nullable = false)
    private Users user;
	
	@ManyToOne
    @JoinColumn(name = "requestid", referencedColumnName = "id")
    private Request request;
}
