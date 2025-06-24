package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.Usersdao;
import com.example.demo.entity.Log_Form;
import com.example.demo.entity.Reg_Form;
import com.example.demo.entity.Users;

@Service
public class LoginService {

	private final Usersdao usersdao;

	@Autowired
	public LoginService(Usersdao usersdao) {
		this.usersdao = usersdao;
	}

	// ユーザー登録
	public Users register(Reg_Form reg_form) {
		// 空文字チェック
		if (reg_form.getUsername() == null || reg_form.getUsername().isEmpty()) {
			throw new IllegalArgumentException("ユーザー名を入力してください");
		}
		if (reg_form.getEmail() == null || reg_form.getEmail().isEmpty()) {
			throw new IllegalArgumentException("メールアドレスを入力してください");
		}
		// メール形式の簡単チェック（正規表現は簡略）
		if (!reg_form.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
			throw new IllegalArgumentException("メールアドレスの形式が正しくありません");
		}
		if (reg_form.getPassword() == null || reg_form.getPassword().isEmpty()) {
			throw new IllegalArgumentException("パスワードを入力してください");
		}

		Users users = new Users();
		users.setUsername(reg_form.getUsername());
		users.setEmail(reg_form.getEmail());
		users.setPassword(reg_form.getPassword());
		users.setCategory_id(0);

		return usersdao.save(users);
	}

	// ログイン（equalsでパスワード比較）
	public Users login(Log_Form log_Form) {
		Optional<Users> userOpt = usersdao.findByEmail(log_Form.getEmail());

		if (userOpt.isPresent()) {
			Users users = userOpt.get();
			if (log_Form.getPassword().equals(users.getPassword())) {
				return users;
			}
		}
		throw new IllegalArgumentException("メールアドレスまたはパスワードが違います");
	}

	//	// 管理者ログイン
	//	public UserDto loginAsAdmin(LoginRequest loginRequest) {
	//		Optional<User> userOpt = usersdao.findByEmail(loginRequest.getEmail());
	//
	//		if (userOpt.isPresent()) {
	//			User user = userOpt.get();
	//			if (user.isAdmin() && loginRequest.getPassword().equals(user.getPassword())) {
	//				return new UserDto(user.getId(), user.getName(), user.getEmail());
	//			}
	//		}
	//		throw new IllegalArgumentException("メールアドレスまたはパスワードが違います");
	//	}

}