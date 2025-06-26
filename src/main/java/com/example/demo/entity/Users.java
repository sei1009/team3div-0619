package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

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
	// 有給の残日数（デフォルト 10）
    @Column(name = "paid_date")
    private int paidDate;

    // 新規作成時にデフォルト値を設定
    @PrePersist
    public void prePersist() {
        if (paidDate == 0) {
            paidDate = 10;
        }
    }
}
