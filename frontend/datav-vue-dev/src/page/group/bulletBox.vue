<template>
    <div class="bulletBox" :style="{
        width:bulletBoxData.urlWidth+'px' || 0,
        height:bulletBoxData.urlHeight+'px' || 0
    }">
        <div class="close" @click="closeBulletBox">
            <i class="el-icon-circle-close"></i>
        </div>
        <iframe class="iframe-style" :src="iframeValue" style="width: 100%; height: 100%;" allow="payment" ref='iframe' name="iframe" draggable="false"></iframe>
    </div>
</template>
<script>
// import { EventBus } from '@/bus.js'
export default {
    name:'bulletBox',
    data(){
        return {
            iframeValue:''
        }
    },
    props:['bulletBoxData'],
    created(){
        console.log('bulletBoxData',this.bulletBoxData)
        let iframeValue = this.bulletBoxData.urlAdress
        if(iframeValue){
            if(this.bulletBoxData.urlParameterValue){
                if(this.bulletBoxData.urlParameterValue.length>0){
                    let urlParameterValue = this.bulletBoxData.urlParameterValue;
                    let parmsData = ''
                    urlParameterValue.forEach((item,index)=>{
                        parmsData =  parmsData + item.urlParameterName + '=' + item.urlParameterValue + (index===urlParameterValue.length-1?'':'&')
                    })
                    console.log(parmsData)
                    iframeValue = `${iframeValue}?${parmsData}`
                }
            }
            this.iframeValue = iframeValue
        }
    },
    methods:{
        closeBulletBox() {
            this.$emit('closeBulletBox')
        },
    }
}
</script>
<style lang="scss" scoped>
.bulletBox{
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%,-50%);
    z-index: 100000;
    // background-color:red
}
.close{
    position: absolute;
    right: -17px;
    top: -18px;
    font-size: 25px;
    color: #409eff;
    cursor: pointer;
}
.iframe-style{
    border: medium none;
}
</style>