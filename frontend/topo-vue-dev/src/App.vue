<template>
  <a-config-provider :locale="antdvLocale">
    <!-- elementPlus国际化 -->
    <el-config-provider :locale="elPlusLocale">
      <router-view />
    </el-config-provider>
  </a-config-provider>
</template>

<script lang="ts">
import axios from 'axios';
import { computed, defineComponent } from 'vue';
import { ElConfigProvider } from 'element-plus'
import { useStore } from 'vuex';
export default defineComponent({
  name: 'App',
  components: {
    ElConfigProvider,
  },
  setup() {
    const store = useStore();
    store.dispatch('user/profile');
    return {
      antdvLocale: computed(() => store.getters.antdvLocale),
      user: computed(() => store.state.user.profile),
      elPlusLocale:computed(() => store.getters.elPlusLocale)
    };
  }
});
</script>

<style lang="scss">
@import '@/styles/variables.scss';
</style>
