<template>
  <div class="logout-container">
    <h2>ログアウトしますか？</h2>
    <button @click="confirmLogout" class="logout-button">はい、ログアウト</button>
    <router-link to="/dashboard" class="cancel-link">キャンセル</router-link>
  </div>
</template>

<script>
export default {
  name: 'Logout',
  methods: {
    confirmLogout() {
      localStorage.removeItem('loggedIn')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.logout-container {
  text-align: center;
  margin-top: 100px;
}

.logout-button {
  padding: 10px 20px;
  background-color: #cc0000;
  color: white;
  border: none;
  cursor: pointer;
  margin-right: 20px;
}

.cancel-link {
  color: #007acc;
  text-decoration: underline;
}
</style>
