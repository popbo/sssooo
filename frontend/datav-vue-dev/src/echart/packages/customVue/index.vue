<template>
  <div class="customVue">
    <div class="code-box" :id="id+ number +'customVue'"></div>
  </div>
</template>
<script>
import create from '../../create'
export default create({
  name: 'customVue',
  data() {
    return {
      js:'',
      css:'',
      html:'',
      code:'',
      program:null,
      number:0,
    }
  },
  created() {
  },
  watch:{
    'option.customVueData': {
      handler(val) {
        if (val) {
          this.number = this.number+1
          this.$nextTick(()=>{
            this.reset()
            this.buildDom()
          })
        }
      },
      deep:true
    },
  },
  mounted() {
    this.$nextTick(()=>{
      this.buildDom()
      this.program.$on('customVueClick',(item=>{
        // console.log(item)
        this.customVueClick(item)
      }))
    })
  },
  methods: {
    reset () {
      document.getElementById(`${this.id}${this.number}customVue`).innerHTML = ''
      if (this.program) {
        this.program.$destroy()
      }
      this.program = null
    },
    getSource (source, type) {
        const regex = new RegExp(`<${type}[^>]*>`)
        let openingTag = source.match(regex)
        if (!openingTag) return ''
        else openingTag = openingTag[0]

        return source.slice(
          source.indexOf(openingTag) + openingTag.length,
          source.lastIndexOf(`</${type}>`)
        )
    },
    splitCode () {
      this.code = this.option.customVueData
      const script = this.getSource(this.code, 'script').replace(
        /export default/,
        'return'
      )
      const style = this.getSource(this.code, 'style')
      const template =this.getSource(this.code, 'template') 
      this.js = script
      this.css = style
      this.html = template
    },
    buildDom () {
      this.splitCode()
      if (this.html === '' || this.js === '') {
        this.$message.error('请输入有效的Vue代码')
        // eslint-disable-next-line semi
        return;
      }
      // eslint-disable-next-line no-new-func
      const common = new Function(this.js)()
      // console.log(common,1456)
      this.css = `#${this.id}${this.number}customVue{
        ${this.css}
      }`
      common.template = this.html
      const Template = Vue.extend(common)
      const vm = this
      // console.log(vm,666)
      this.program = new Template({
        propsData:{
          customVueData:vm
        }
      })
      document.querySelector(`#${this.id}${this.number}customVue`).appendChild(this.program.$mount().$el)
      if (this.css !== '') {
        const styles = document.createElement('style')
        styles.type = 'text/css'
        styles.innerHTML = this.css
        document.getElementsByTagName('head')[0].appendChild(styles)
      }
    },
  }
})
</script>
<style lang="scss" scoped>
.customVue{
  height: 100%;
  width: 100%;
  overflow: auto;
  .code-box{
    height: 100%;
    width: 100%;
    cursor: default;
  }
}
</style>
