<<template>
  <header class="global-header">
    <div class="header-inner">
      <h1 class="logo">My App</h1>
      <router-link v-if="isLoggedIn" to="/logout" class="logout-link">ログアウト</router-link>
    </div>
  </header>
</template>

<script>
export default {
  name: 'Header',
  computed: {
    isLoggedIn() {
      return localStorage.getItem('loggedIn') === 'true'
    }
  }
}
</script>
