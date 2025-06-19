<template>
  <div class="login-container">
    <h2 class="title">ログイン画面</h2>

    <form @submit.prevent="handleLogin">
      <input v-model="username" type="text" placeholder="名前" class="input-box" />
      <input v-model="password" type="password" placeholder="パスワード" class="input-box" />
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      <button type="submit" class="login-button">ログイン</button>
    </form>

    <p class="register-link">
      <router-link to="/register">新規登録はこちら</router-link>
    </p>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      username: '',
      password: '',
      errorMessage: ''
    }
  },
  methods: {
    handleLogin() {
      if (this.username === 'admin' && this.password === '1234') {
        localStorage.setItem('loggedIn', 'true') // ✅ 記錄登入
        this.errorMessage = ''
        this.$router.push('/dashboard')
      } else {
        this.errorMessage = 'エラーメッセージです'
      }
    }
  }
}
</script>

<style scoped>
.login-container {
  width: 300px;
  margin: 100px auto;
  text-align: center;
  font-family: sans-serif;
}

.title {
  margin-bottom: 20px;
  font-size: 20px;
}

.input-box {
  width: 100%;
  height: 35px;
  margin-bottom: 10px;
  border: 1px solid black;
  padding: 5px;
  font-size: 16px;
}

.error-message {
  color: red;
  margin-bottom: 10px;
  font-size: 14px;
}

.login-button {
  padding: 5px 20px;
  border: 1px solid black;
  background-color: white;
  cursor: pointer;
}

.register-link {
  margin-top: 20px;
  font-size: 14px;
}
</style>
