<template>
  <component v-bind="linkProps(to)">
    <slot />
  </component>
</template>

<script>
export default {
  props: {
    to: {
      type: String,
      required: true
    }
  },
  data () {
    return {

    }
  },

  created () {

  },
  methods: {
    linkProps(url) {
      return {
        is: 'router-link',
        to: url
      }
    }
  }
}
</script>

<style scoped lang='scss'>

</style>
