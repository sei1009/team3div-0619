package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "request")
public class Request {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 申請タイプ (1が申請あり、0が申請なしを想定)
	private int paid;
	private int early;
	private int absence;
	private int late;

	// 承認ステータス (0=申請中, 1=承認済み, 2=却下済み)
	// デフォルト値を0（申請中）に設定
	private int paid_app = 0;
	private int early_app = 0;
	private int absence_app = 0;
	private int late_app = 0;

	@ManyToOne
	@JoinColumn(name = "userid", referencedColumnName = "id", nullable = false)
	private Users user;

	@OneToMany(mappedBy = "request") 
	private List<Attendance> attendances = new ArrayList<>(); 
}
