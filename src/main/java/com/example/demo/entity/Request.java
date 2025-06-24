package com.example.demo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "request")
public class Request {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private int paid;
	private int early;
	private int absence;
	private int late;
	private int paid_app;
	private int early_app;
	private int absence_app;
	private int late_app;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@ToString.Exclude
	private Users users;

	@OneToOne(mappedBy = "request", cascade = CascadeType.REMOVE, orphanRemoval = true)
	@JsonIgnore
	private Attendance attendance;

}