package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.Request;
import com.example.demo.entity.Users;

import lombok.Data;

@Data
public class CombinedApplicationDto {
	private Long id;
	private Long requestId;
	private String date; // 日付 (YYYY-MM-DD形式)
	private String username;
	private String recordType;
	private String requestType;
	private List<ApplicationDetail> applications;
	private Long deleteId;

	public CombinedApplicationDto(Attendance attendance, Users user, Request request) {
		this.username = user != null ? user.getUsername() : "不明";
		this.applications = new ArrayList<>();

		if (request != null) {
			this.id = request.getId();
			this.requestId = request.getId();
			this.deleteId = request.getId(); // Requestの削除用ID
			this.recordType = "request";
			this.date = attendance != null && attendance.getDate() != null ? attendance.getDate().toString() : "不明な日付";

			// 申請タイプをapplicationsリストに追加
			if (request.getPaid() == 1) {
				this.applications.add(new ApplicationDetail("paid", "有給", request.getPaid_app()));
			}
			if (request.getEarly() == 1) {
				this.applications.add(new ApplicationDetail("early", "早退", request.getEarly_app()));
			}
			if (request.getAbsence() == 1) {
				this.applications.add(new ApplicationDetail("absence", "欠勤", request.getAbsence_app()));
			}
			if (request.getLate() == 1) {
				this.applications.add(new ApplicationDetail("late", "遅刻", request.getLate_app()));
			}

			// requestType の設定（applicationsが空の場合も考慮）
			List<String> types = new ArrayList<>();
			if (request.getPaid() == 1)
				types.add("有給");
			if (request.getEarly() == 1)
				types.add("早退");
			if (request.getAbsence() == 1)
				types.add("欠勤");
			if (request.getLate() == 1)
				types.add("遅刻");
			this.requestType = types.isEmpty() ? "不明な申請" : String.join(", ", types);

		} else if (attendance != null) {
			// 純粋な勤怠レコードの場合（この部分は変更なしでOK）
			this.id = attendance.getId();
			this.deleteId = attendance.getId();
			this.recordType = "attendance";
			this.requestType = "勤怠のみ";
			this.date = attendance.getDate() != null ? attendance.getDate().toString() : "不明な日付";
		} else {
			// どちらのデータもない、または初期化されない場合
			this.id = null;
			this.deleteId = null;
			this.recordType = "不明";
			this.requestType = "データなし";
			this.date = "不明な日付";
		}
	}

	// 内部クラス（変更なし）
	@Data
	public static class ApplicationDetail {
		private String typeKey;
		private String typeName;
		private int status;

		public ApplicationDetail(String typeKey, String typeName, int status) {
			this.typeKey = typeKey;
			this.typeName = typeName;
			this.status = status;
		}
	}
}