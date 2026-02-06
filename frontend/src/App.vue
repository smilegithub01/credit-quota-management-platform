<template>
  <div id="app">
    <el-container>
      <el-header class="header">
        <h1>银行信贷额度管控平台</h1>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

.header {
  background-color: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.header h1 {
  margin: 0;
  font-size: 20px;
}
</style>