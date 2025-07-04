let t = '<temp' +'late>'
let t1 = '</temp' +'late>'
let c = '<scr' +'ipt>'
let c1 = '</scr' +'ipt>'
export const customVueData = `
${t}
  <div class="test">
    <img src="/img/vue1.png" @click="handleClick" />
  </div>
${t1}
${c}
export default{
    data(){
        return{
        }
    },
    props:['customVueData'],
    created(){
    },
    methods:{
        handleClick(){
            let parms =[{
                id:'c881c53a-1157-4573-b955-08ecc1b93247',
                eventAction:2,
                p:{
                a:1
                }
            },{
                id:'9d899756-3d76-4cca-ace3-a418a34e3f8c',
                eventAction:2,
                p:{
                a:1
                }
            }] 
            this.$emit('customVueClick',parms)
            window.windowUpdateClick(parms)
            console.log(this.customVueData,1111)
        }
    }
}
${c1}
<style scoped>
    .test{
        text-align:center;
        color:red;
        font-size:40px;
        img{
            margin-top: 40px ;
        }
    }
</style>
`