package com.example.demo.entity;

import java.util.ArrayList; // ArrayList をインポート
import java.util.List; // List をインポート

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Table(name = "users")
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	private String password;
	private String email;
	private int category_id;

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true) // または CascadeType.REMOVE
	@JsonIgnore
	private List<Request> requests;

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true) // または CascadeType.REMOVE
	@JsonIgnore
	private List<Attendance> attendances;

	public Users() {
		this.requests = new ArrayList<>();
		this.attendances = new ArrayList<>();
	}
}