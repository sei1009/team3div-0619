<template>
  <div class="login-container">
    <p class="back-link">
      <router-link to="/">&lt;&lt; 戻る</router-link>
    </p>

    <h2 class="title">新規登録画面</h2>

    <form @submit.prevent="handleRegister">
      <input v-model="username" type="text" placeholder="名前" class="input-box" />
      <input v-model="password" type="password" placeholder="パスワード" class="input-box" />
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      <button type="submit" class="login-button">ログイン</button>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      password: '',
      errorMessage: ''
    }
  },
  methods: {
    handleRegister() {
      if (this.username && this.password) {
        alert('登録完了')
        this.$router.push('/')
      } else {
        this.errorMessage = 'エラーメッセージです'
      }
    }
  }
}
</script>

<style scoped>
@import './style.css';
</style>
