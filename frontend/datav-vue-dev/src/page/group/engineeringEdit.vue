<template>
    <div class="engineering">
        <el-dialog
            title="工程化编辑"
            width="40%"
            class="engineering-dialog"
            :close-on-click-modal="false"
            v-loading="loading"
            element-loading-text="拼命加载中.."
            element-loading-spinner="el-icon-loading"
            :visible.sync="imgVisible"
        >
        <div class="engineeringEdit">
            <div class="engineeringEdit-top">
                <el-button  size="mini" type="primary" @click="verallAdd">+全局替换</el-button>
                <el-button  size="mini" type="primary" @click="assemblyAdd">+组件修改</el-button>
                <el-button  size="mini" type="primary" @click="imgClick">批量导入</el-button>
            </div>
            <div class="engineeringEdit-img" v-show="overallList.length===0&&assemblyList.length===0&&!imgExport"></div>
            <div class="engineeringEdit-center" v-show="!imgExport">
                <div class="center-one" v-if="overallList.length>0">
                    <div class="overall">
                        <div class="title-name">全局替换</div>
                        <div class="overall-all" :style="{height:assemblyList.length>0?120+'px':300+'px'}">
                            <div class="overall-text" v-for="(item,index) in overallList" :key="index">
                                <div style="padding-left:10px">查找内容：</div>
                                <div>
                                    <el-input size="mini" v-model="item.oldContent" placeholder="" style="width: 250px;"></el-input>
                                </div>
                                <div style="padding:0px 15px">替换为</div>
                                <div>
                                    <el-input size="mini" v-model="item.newContent" placeholder="" style="width: 250px;"></el-input>
                                </div>
                                <div class="overall-delete" @click="overallDelete(item,index)">
                                    <i class="el-icon-delete"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="center-two" v-if="assemblyList.length>0">
                    <div class="assembly">
                        <div class="title-name">组件修改</div>
                        <div class="assembly-type">
                            <div class="assembly-name" v-for="(item,index) in assemblyType" :key="index">{{item}}</div>
                        </div>
                        <div class="assembly-all" :style="{height:overallList.length>0?120+'px':300+'px'}">
                            <div class="assembly-select" v-for="(item,index) in assemblyList" :key="index">
                                <div class="select-type">
                                    <el-select v-model="item.template" placeholder=""  size="small" filterable @change="templateChange($event,item)" @visible-change="templateVisible($event,item,index)">
                                        <el-option
                                            v-for="item in item.templateOptions"
                                            :key="item.id"
                                            :label="item.name"
                                            :value="item.id">
                                        </el-option>
                                    </el-select>
                                </div>
                                <div class="select-type">
                                    <el-select v-model="item.operate" placeholder=""  size="small"   multiple collapse-tags @change="operateChange($event,item)">
                                        <el-checkbox style="width: 50%; overflow: hidden; color: #fff ;margin-left: 20px;" v-model="item.operateChecked" @change='operateSelectAll($event,item)'>全选</el-checkbox>
                                        <el-option
                                            v-for="item in item.operateOptions"
                                            :key="item.value"
                                            :label="item.label"
                                            :value="item.value">
                                        </el-option>
                                    </el-select>
                                </div>
                                <div class="select-type">
                                    <el-select v-model="item.configuration" placeholder=""  size="small" multiple collapse-tags  @change="configurationChange($event,item)">
                                        <el-checkbox style="width: 50%; overflow: hidden; color: #fff ;margin-left: 20px;" v-model="item.configurationChecked" @change='configurationSelectAll($event,item)'>全选</el-checkbox>
                                        <el-option
                                            v-for="item in item.configurationOptions"
                                            :key="item.value"
                                            :label="item.label"
                                            :value="item.value">
                                        </el-option>
                                    </el-select>
                                </div>
                                <div class="select-type">
                                    <el-select v-model="item.editAssembly" placeholder=""  size="small" filterable  @change="editAssemblyChange($event,item)" multiple collapse-tags @visible-change="editAssemblyVisible($event,item,index)">
                                        <el-checkbox style="width: 50%; overflow: hidden; color: #fff ;margin-left: 20px;" v-model="item.editAssemblyChecked" @change='editAssemblySelectAll($event,item)'>全选</el-checkbox>
                                        <el-option
                                            v-for="item in item.editAssemblyOptions"
                                            :key="item.id"
                                            :label="item.name"
                                            :value="item.id">
                                        </el-option>
                                    </el-select>
                                </div>
                                <div class="assembly-delete" @click="assemblyDelete(item,index)">
                                    <i class="el-icon-delete"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="engineeringEdit-foot" v-show="!imgExport">
                <el-button  size="mini" type="info" style="width: 70px;" @click="restOne">取消</el-button>
                <el-button  size="mini" type="primary" style="width: 90px;" @click="ok">一键替换</el-button>
            </div>
            <!-- 批量导入 -->
            <div class="engineeringEdit-center img-center" v-show="imgExport">
                <div class="img-name">
                    <span>批量导入</span>
                    <el-button  size="mini" type="primary" @click="openText" style="width: 120px;" v-show="!imgSelect">文本框属性配置</el-button>
                </div>
                <el-input
                    v-show="!imgSelect"
                    type="textarea"
                    placeholder="在此处粘贴文本"
                    resize="none"
                    v-model="textarea">
                </el-input>
                <div v-if="imgSelect" class="img-select" id="img-select-id">
                    <div class="img-type" v-for="(item,index) in imgTypeList" :key="index">
                        <el-input v-model="item.name" size="small" style="width: 300px;"></el-input>
                        <span class="img-interval">
                            <img :src="require('@/assets/svg/lianjie.svg')"/>
                        </span>
                        <el-select v-model="item.imgID" multiple filterable  collapse-tags size="small" placeholder="请选择" style="width: 300px;" @visible-change="visibleChange($event,index)">
                            <el-option
                            v-for="(itema) in imgList"
                            :key="itema.index"
                            :label="itema.name"
                            :value="itema.index">
                            </el-option>
                        </el-select>
                        <span class="img-delete" @click="imgDelete(index)">
                            <i class="el-icon-delete"></i>
                        </span>
                    </div>
                </div>
            </div>
            <div class="img-foot" v-show="imgExport">
                <el-button  size="mini" type="primary" style="width: 100px;" @click="customBuild" v-show="!imgSelect" >自定义生成</el-button>
                <el-button  size="mini" type="primary" style="width: 90px;" @click="intelligent"  v-show="!imgSelect" >智能生成</el-button>
                <el-button  size="mini" type="primary" style="width: 100px;" @click="imgOk"  v-show="imgSelect">确定</el-button>
                <el-button  size="mini" type="info" style="width: 100px;" @click="imgRest"  v-show="imgSelect">取消</el-button>
            </div>
        </div>
        </el-dialog>
        <el-dialog
            title="提示"
            width="360px"
            top="35vh"
            :visible.sync="okVisible"
        >
        <div class="ok-engineering">
            <div class="ok-top">
                <div class="top-icon">
                    <i class="el-icon-warning"></i>
                </div>
                <div class="top-text1">本次操作将修改页面中的全局替换<span class="fs-top">{{findData}}</span>个属性，组件修改，修改了<span class="fs-top">{{zData}}</span>组件的<span class="fs-top">{{componentCount}}</span>属性。是否确定修改?</div>
                <!-- <div class="top-text">是否确定修改?</div> -->
            </div>
            <div class="ok-foot">
                <el-button  size="mini" type="info" @click="restTwo">取消</el-button>
                <el-button  size="mini" type="primary" @click="okTwo">确定</el-button>
            </div>
        </div>
        </el-dialog>
        <el-dialog
            title="属性配置"
            width="600px"
            top="25vh"
            class="text-pei"
            :visible.sync="attributeFlage"
        >
        <div class="text-engineering">
            <div class="text-top">
                <div class="text-type" @click="getIndexType(1)" :class="indexType===1?'text-type-clolor':''">
                    文本框属性
                </div>
                <div class="text-type" @click="getIndexType(2)" :class="indexType===2?'text-type-clolor':''">
                    参数属性
                </div>
            </div>
            <div class="text-configuration" v-show="indexType===1">
                <div class="text-type">
                    <div class="text-a">
                        <div>字体大小</div>
                        <div>
                            <el-input-number
                                size="mini"
                                v-model="textConfiguration.fontSize"
                                controls-position="right"
                                :min="0"
                                :max="200"
                                style="width: 120px;"
                            />
                        </div>
                
                    </div>
                    <div class="text-a">
                        <div>字体颜色</div>
                        <div>
                            <avue-input-color size="mini" v-model="textConfiguration.color"></avue-input-color>
                        </div>
                    </div>
                </div>
                <div class="text-type">
                    <div class="text-a">
                        <div>边框圆角</div>
                        <div>
                            <el-input-number
                                size="mini"
                                v-model="textConfiguration.borderWidth"
                                controls-position="right"
                                :min="0"
                                :max="200"
                                style="width: 120px;"
                            />
                        </div>
                
                    </div>
                    <div class="text-a">
                        <div>字体背景</div>
                        <div>
                            <avue-input-color size="mini" v-model="textConfiguration.backgroundColor"></avue-input-color>
                        </div>
                    </div>
                </div>
                <div class="text-type" style="position: relative;left: -30px;">
                    <div class="text-a">
                        <div style="width: 80px;">图层名称前缀
                        </div>
                        <div >
                            <el-input
                                size="mini"
                                v-model="textName"
                                style="width: 180px;"
                            />
                        </div>
                    </div>
                </div>
            </div>
            <div class="text-configuration" v-show="indexType===2">
                <div class="text-type" style="padding-left: 50px;">
                    <div class="text-a">
                        <div style="width: 120px;">模糊查询关键词</div>
                        <div>
                            <el-input
                                size="mini"
                                v-model="keyWord"
                                style="width: 250px;"
                            />
                        </div>
                    </div>
                </div>
                <div class="text-type" style="padding-left: 50px;">
                    <div class="text-a">
                        <div style="width: 120px;">删除重复关键词
                            <el-tooltip
                            class="item"
                            effect="dark"
                            content="在生成的文本框中删除输入的关键词，非必填"
                            placement="top-start"
                            >
                            <i class="el-icon-info"></i>
                            </el-tooltip>
                        </div>
                        <div >
                            <el-input
                                size="mini"
                                v-model="replaceWord"
                                style="width: 250px;"
                            />
                        </div>
                    </div>
                </div>
            </div>
            <div class="text-foot">
                <el-button  size="mini" type="primary" style="width: 100px; margin-right: 30px;" @click="okText">确定</el-button>
                <el-button  size="mini" type="info" style="width: 100px;" @click="restText">取消</el-button>
            </div>
        </div>
        </el-dialog>
    </div>
</template>
<script>
import baseList from '@/option/base'
import { deepClone } from '../../echart/util'
import { uuid } from '@/utils/utils.min.js'
import {filterTree,findTree ,uniq} from "xe-utils";
import { getProjectTemplate,getProjectEditType,getProjecTarget,getProjecConfig,getProjecReplaceAll,getProjeQueryAll,getImgNames } from '@/api/visual';
export default {
    name:"engineeringEdit",
    data() {
        return {
            textName:'文本框-',
            indexType:1,
            attributeFlage:false,
            loading:false,
            textConfiguration:{
                fontSize:20,
                color:'rgba(146, 163, 183, 1)',
                borderWidth:20,
                backgroundColor:'rgba(18, 26, 34, 0.5)'
            },
            textarea:'',
            queryImgList:[],
            selectListData:[],
            imgTypeList:[],
            imgList:[],
            oldImgList:[],
            imgNameList:[],
            imgExport:false,
            imgSelect:false,
            zData:0,
            sData:0,
            okVisible:false,
            imgVisible:false,
            overallList:[],
            assemblyList:[],
            assemblyType:['样板组件','操作类型','配置项','修改组件'],
            templateOptions:[],
            templateComponentId:'',
            findData:0,
            componentCount:0,
            optionText:{},
            keyWord:'',
            replaceWord:''
        }
    },
    inject: ["contain"],
    watch:{
        imgVisible(val){
            if(val){
                this.overallList = [];
                this.assemblyList = [];
                this.textarea = '';
                this.imgExport = false;
                this.imgSelect = false;
                this.getProjectTemplate()
            }
        },
    },
    methods:{
        getIndexType(index) {
            this.indexType = index;
        },
        openText() {
            this.attributeFlage = true;
        },
        imgRest() {
            this.queryImgList = deepClone(this.imgList);
            this.imgSelect = false;
        },
        imgDelete(index) {
            this.imgTypeList.splice(index,1)
        },
        okText() {
            this.attributeFlage = false;
            this.imgClick()
        },
        restText() {
            this.attributeFlage = false;
            this.textConfiguration={
                fontSize:20,
                color:'rgba(146, 163, 183, 1)',
                borderWidth:20,
                backgroundColor:'rgba(18, 26, 34, 0.5)'
            }
            this.keyWord = '';
            this.replaceWord='';
            this.textName='文本框-'
        },
        imgClick() {
            let parms = {
                configId:this.contain.obj.config.id,
                keyWord:this.keyWord,
                replaceWord: this.replaceWord
            }
            this.textarea = ''
            getImgNames(parms).then(res=>{
                if(res.data){
                    if(res.data.code==200){
                        let text = res.data.data
                        this.textarea = text.join('\n')
                    }
                }
            })
            this.imgList = []
            let list = filterTree( this.contain.nav, (item) => {
                if(item.component){
                    return item.component.prop === 'img'
                }
            }, { children: 'list'})
            let children = filterTree( this.contain.nav, item => {
                if(item.component){
                    return item.component.prop === 'img'
                }
            }, { children: 'children'})
            this.imgList = [...list,...children]
            this.imgList = uniq(this.imgList)
            this.oldImgList = deepClone(this.imgList);
            this.queryImgList = deepClone(this.imgList);
            this.imgExport = true;
        },
        visibleChange(val,value){
            if(val){
                this.selectListData = [];
                this.imgTypeList.forEach((item,index)=>{
                    if(index!==value){
                        this.selectListData = [...this.selectListData,...item.imgID];
                    }
                })
                // console.log(this.selectListData,'this.selectListData',this.oldImgList)
                let imgList = this.oldImgList.filter((item) =>
                    !this.selectListData.some((ele) => ele === item.index)
                );
                this.imgList = deepClone(imgList);
            }
        },
        intelligent() {
            if(this.textarea){
                this.loading = true;
                let imgName = this.textarea.split('\n')
                let imgNameList = imgName.filter(item=>{
                    return item !==''
                })
                this.imgNameList = imgNameList;
                let imgTypeList = []
                this.imgNameList.forEach(item=>{
                    imgTypeList.push({
                        name:item,
                        imgID:this.getFrontQuery(item)
                    })
                })
                setTimeout(()=>{
                    this.imgTypeList = imgTypeList;
                    this.imgSelect = true;
                    this.loading = false;
                },80)
            }else{
                this.loading = false;
                this.$message.warning('内容不能为空！');
            }
        },
        getFrontQuery(name){
            let text = name.replace(/\(([^).]*)\)/g,"$1")
            // console.log('text',text)
            const pattern = new RegExp(text,'g');
            let selectList = []
            for(let i = 0; i < this.queryImgList.length; i++){
                // if(this.queryImgList[i].name===name){
                //     selectList.push(this.queryImgList[i].index)
                // }
                let text2 = this.queryImgList[i].name.replace(/\(([^).]*)\)/g,"$1")
                if(pattern.test(text2)){
                    selectList.push(this.queryImgList[i].index);
                }
            }
            let newimgList = this.queryImgList.filter((item) =>
                !selectList.some((ele) => ele === item.index)
            );
            this.queryImgList = newimgList
            return selectList
        },
        customBuild() {
            this.loading = true;
            this.imgNameList = [];
            this.imgTypeList = [];
            if(this.textarea){
                let imgName = this.textarea.split('\n')
                let imgNameList = imgName.filter(item=>{
                    return item !==''
                })
                this.imgNameList = imgNameList;
                let imgTypeList = []
                this.imgNameList.forEach((item)=>{
                    imgTypeList.push({
                        name:item,
                        imgID:[],
                    })
                })
                setTimeout(()=>{
                    this.imgTypeList = imgTypeList;
                    this.imgSelect = true;
                    this.loading = false;
                },80)
            }else{
                this.loading = false;
                this.$message.warning('内容不能为空！');
            }
        },
        imgOk() {
            this.loading = true;
            this.geText("文本框",baseList)
            let atextList = [] //下拉框不为空时
            let btextList = [] //下拉框为空时
            if(this.imgTypeList.length>0){
                this.imgTypeList.forEach(item=>{
                    if(item.imgID.length>0){
                        item.imgID.forEach(itema=>{
                            atextList.push(Object.assign({name:item.name,id:itema}))
                        })
                    }else{
                        btextList.push({name:item.name})
                    }
                })
            }
            // 下拉框不为空时
            let renderingList = [] 
            // let bulletLits = []
            console.log('atextList',atextList)
            atextList.forEach(item=>{
                let parms = deepClone(this.optionText);
                let positioning = this.getImgInformation(item.id);
                parms.index = uuid();
                parms.data.value = item.name;
                parms.name = this.textName + item.name;
                parms.comParams = item.name;
                parms.option.fontSize = this.textConfiguration.fontSize
                parms.option.color =  this.textConfiguration.color
                parms.option.backgroundColor =  this.textConfiguration.backgroundColor
                parms.option.borderWidth =  this.textConfiguration.borderWidth
                parms.option.textAlign = 'center'
                // 获取文本框的宽度
                parms.component.width = this.measureText(item.name,parms.option.fontSize)+15;
                parms.component.height = 30
                parms.option.lineHeight = parms.component.height
                parms.intimeLeft = positioning.intimeLeft+(positioning.width-parms.component.width)/2;
                parms.intimeTop = positioning.intimeTop + positioning.height+1;
                parms.left = positioning.intimeLeft+(positioning.width-parms.component.width)/2;
                parms.top = positioning.intimeTop + positioning.height + 1;
                let flist = findTree(this.contain.nav, fitem => fitem.index === item.id, { children: 'list' })
                if(flist){
                    if(flist.parent){
                        this.contain.nav[flist.path[0]].list.push(parms)
                    }else{
                        renderingList.push(parms);
                    }
                }else{
                    renderingList.push(parms);
                }
            })
             // 下拉框为空时
            let noValueList = [];
            let arrange = 20 // 一行多少列
            btextList.forEach((item,index)=>{
                let parms = deepClone(this.optionText);
                parms.index = uuid();
                parms.data.value = item.name;
                parms.name = this.textName + item.name;
                parms.comParams = item.name
                parms.option.fontSize = this.textConfiguration.fontSize
                parms.option.color =  this.textConfiguration.color
                parms.option.backgroundColor =  this.textConfiguration.backgroundColor
                parms.option.borderWidth =  this.textConfiguration.borderWidth
                parms.option.textAlign = 'center'
                 // 获取文本框的宽度
                parms.component.width = this.measureText(item.name,parms.option.fontSize)+15;
                parms.component.height = 30
                parms.option.lineHeight = parms.component.height
                parms.intimeLeft = (parseInt(index/arrange)*200);
                parms.intimeTop = ((index/arrange)-(parseInt(index/arrange)))*(40/(1/arrange))
                parms.left = (parseInt(index/arrange)*200);
                parms.top = ((index/arrange)-(parseInt(index/arrange)))*(40/(1/arrange))
                noValueList.push(parms);
            })
            this.contain.nav = [...noValueList,...renderingList,...this.contain.nav];
            this.loading = false;
            this.imgVisible = false;
        },
        measureText(val,fontSize){
            const font = `${fontSize}px arial`;
            const canvas = document.createElement("canvas");
            const context = canvas.getContext("2d");
            context.font = font;
            // const { width } = context.measureText(state);
            const { width } = context.measureText(val);
            return width
        },
        getImgInformation(id){
            let param = {}
            let imgList = deepClone(this.oldImgList)
            if(imgList.length){
               let datas = imgList.filter(itme=>{
                    return itme.index === id
               })
               if(datas.length>0){
                    param.id = datas[0].index
                    param.intimeLeft = datas[0].left
                    param.intimeTop = datas[0].top
                    param.width = datas[0].component.width
                    param.height = datas[0].component.height
               }
            }
            return param
        },
        geText(label,list){
            if(list){
                list.filter(item=>{
                    if(item.label===label){
                        this.optionText = item.option
                    }
                    if(item.children){
                        this.geText(label,item.children)
                    }
                })
            }
        },
        templateVisible(flage,item,index) {
            if(flage){
                let otherData = this.assemblyList.filter((itemb,aindex)=>{
                    console.log(itemb)
                    return aindex!==index
                })
                let selectTemplate = []
                let selectEditAssembly = []
                if(otherData.length>0){
                    otherData.forEach(itemlist=>{
                        selectTemplate.push(itemlist.template)
                        selectEditAssembly = [...selectEditAssembly,...itemlist.editAssembly]
                    })
                }
                let zSelect = [...selectTemplate,...selectEditAssembly]
                console.log(zSelect,'zSelect')
                if(zSelect.length>0){
                    let arr1 = this.templateOptions.filter((t) => !zSelect.includes(t.id));
                    item.templateOptions = arr1
                }else{
                    item.templateOptions = this.templateOptions;
                }
            }
        },
        editAssemblyVisible(flage,item,index) {
            if(flage){
                let otherData = this.assemblyList.filter((itemb,aindex)=>{
                    console.log(itemb)
                    return aindex!==index
                })
                let selectTemplate = []
                let selectEditAssembly = []
                if(otherData.length>0){
                    otherData.forEach(itema=>{
                        selectTemplate.push(itema.template)
                        selectEditAssembly = [...selectEditAssembly,...itema.editAssembly]
                    })
                }
                let zSelect = [...selectTemplate,...selectEditAssembly]
                if(zSelect.length>0){
                    let arr1 = item.oldEditAssemblyOptions.filter((t) => !zSelect.includes(t.id));
                    item.editAssemblyOptions = arr1
                }
                if(item.editAssembly.length===item.editAssemblyOptions.length){
                    item.editAssemblyChecked = true
                }else{
                    item.editAssemblyChecked = false
                }
            }
        },
        operateSelectAll(falge,item) {
            if(falge){
                item.operate = []
                item.operateOptions.forEach(idem=>{
                    item.operate.push(idem.value)
                })
                item.configurationChecked = false;
                item.configuration=[]
                item.configurationOptions = [];
                if(item.operate.length>0){
                    let parms = {
                        configId:this.contain.obj.config.id,
                        editType: item.operate.join(", "),
                        templateComponentId: this.templateComponentId
                    }
                    getProjecConfig(parms).then(res=>{
                        if(res.data.data){
                            item.configurationOptions = res.data.data;
                        }
                    })
                }
            }else{
                item.configurationChecked = false;
                item.configuration = []
                item.operate = []
                item.configurationOptions = []
            }
        },
        configurationSelectAll(falge,item) {
            if(falge){
                item.configuration = []
                item.configurationLabel = item.configurationOptions;
                item.configurationOptions.forEach(idem=>{
                    item.configuration.push(idem.value)
                })
            }else{
                item.configurationLabel =[]
                item.configuration = []
            }
        },
        editAssemblySelectAll(falge,item) {
            if(falge){
                item.editAssembly = []
                item.editAssemblyOptions.forEach(idem=>{
                    item.editAssembly.push(idem.id)
                })
            }else{
                item.editAssembly = []
            }
        },
        configurationChange(value,item) {
            if(value.length===item.configurationOptions.length){
                item.configurationChecked = true;
            }else{
                item.configurationChecked = false;
            }
            let arr1 = item.configurationOptions.filter((t) => value.includes(t.value));
            item.configurationLabel = arr1
            
        },
        editAssemblyChange(value,item) {
            if(value.length===item.editAssemblyOptions.length){
                item.editAssemblyChecked = true;
            }else{
                item.editAssemblyChecked = false;
            }
        },
        getProjectTemplate() {
            getProjectTemplate(this.contain.obj.config.id).then((res)=>{
                if(res.data.data){
                    this.templateOptions = res.data.data
                }
            })
        },
        templateChange(value,item) {
            console.log(5555,value,item)
            item.editAssemblyChecked=false;
            item.configurationChecked=false;
            item.operateChecked=false;
            item.operate=[];
            item.configuration=[];
            item.editAssembly=[];
            item.operateOptions=[];
            item.configurationOptions=[];
            item.editAssemblyOptions=[];
            item.oldEditAssemblyOptions = [];
            this.templateComponentId = value;
            if(value!==''){
                let parms = {
                    configId:this.contain.obj.config.id,
                    templateComponentId:value
                }
                getProjectEditType(parms).then(res=>{
                    if(res.data.data){
                        item.operateOptions = res.data.data;
                    }
                })
                getProjecTarget(parms).then(res=>{
                    if(res.data.data){
                        item.editAssemblyOptions = res.data.data;
                        item.oldEditAssemblyOptions = res.data.data
                    }
                })
            }
        },
        operateChange(value,item) {
            if(value.length===item.operateOptions.length){
                item.operateChecked = true;
            }else{
                item.operateChecked = false;
            }
            item.configurationChecked = false;
            item.configuration=[]
            item.configurationOptions = [];
            if(value.length>0){
                let parms = {
                    configId:this.contain.obj.config.id,
                    editType: value.join(", "),
                    templateComponentId: this.templateComponentId
                }
                getProjecConfig(parms).then(res=>{
                    if(res.data.data){
                        item.configurationOptions = res.data.data;
                    }
                })
            }
        },
        restOne() {
            this.imgVisible = false;
        },
        restTwo() {
            this.okVisible = false;
        },
        ok(){
            this.findData = 0;
            this.sData = 0;
            this.zData = 0;
            this.componentCount = 0;
            let parms = {
                configId:this.contain.obj.config.id,
                replaceAllEditS:[],
                componentEditDTOS:[]
            }
            if(this.overallList.length===0&&this.assemblyList.length===0 ){
                return  this.$message.warning('内容不能为空！');
            }
            let flage = false;
            let flageOne = false;
            this.overallList.forEach(item=>{
                if(item.oldContent==='' || item.newContent===''){
                    flage = true
                }
                parms.replaceAllEditS.push({
                    findText:item.oldContent,
                    replaceText:item.newContent
                })
            })
            this.assemblyList.forEach((item=>{
                if(item.template==='' || item.operate.length===0 || item.editAssembly.length===0 ){
                    flage = true
                }
                if(item.configurationOptions.length>0){
                    if(item.configuration.length===0){
                        flageOne = true
                    }
                }
                parms.componentEditDTOS.push({
                    targetComponentIdS:item.editAssembly,
                    templateComponentId:item.template,
                    editTypeS:item.operate,
                    configS:item.configurationLabel
                })
                this.zData = this.zData + item.editAssembly.length;
                this.sData = this.sData + item.configuration.length
            }))
            if(flage){
                return this.$message.warning('查找内容、替换内容、样板组件、操作类型、修改组件、不能为空！')
            }
            if(flageOne){
                return this.$message.warning('当操作类型有复用配置时配置项不能为空！')
            }
            if(this.overallList.length>0 || this.assemblyList.length>0 ){
                getProjeQueryAll(parms).then(res=>{
                    if(res.data.data){
                        let findData = res.data.data;
                        this.findData = findData.replaceCount;
                        this.componentCount = findData.componentCount;
                    }
                })
            }
            this.okVisible = true;
        },
        okTwo() {
            let parms = {
                configId:this.contain.obj.config.id,
                replaceAllEditS:[],
                componentEditDTOS:[]
            }
            this.overallList.forEach(item=>{
                parms.replaceAllEditS.push({
                    findText:item.oldContent,
                    replaceText:item.newContent
                })
            })
            this.assemblyList.forEach(item=>{
                parms.componentEditDTOS.push({
                    targetComponentIdS:item.editAssembly,
                    templateComponentId:item.template,
                    editTypeS:item.operate,
                    configS:item.configurationLabel
                })
            })
            getProjecReplaceAll(parms).then(res=>{
                if(res.data.code==200){
                    this.imgVisible = false;
                    this.okVisible = false;
                    this.$emit('engineeringOk')
                    this.$message.success('修改成功！');
                }else{
                    this.$message.error('修改失败！');
                }
            })
            // console.log(parms,66666)
        },
        overallDelete(item,index) {
            console.log(item)
            this.overallList.splice(index,1)
        },
        assemblyDelete(item,index) {
            console.log(item)
            this.assemblyList.splice(index,1)
        },
        verallAdd() {
            this.imgExport = false;
            this.overallList.push({
                oldContent:'',
                newContent:''
            })
        },
        async assemblyAdd() {
            this.imgExport = false;
            this.assemblyList.push({
                editAssemblyChecked:false,
                configurationChecked:false,
                operateChecked:false,
                template:'',
                operate:[],
                configuration:[],
                configurationLabel:[],
                editAssembly:[],
                templateOptions:[],
                operateOptions:[],
                configurationOptions:[],
                editAssemblyOptions:[],
                oldEditAssemblyOptions:[],
            })
            // const { data } = await getProjectTemplate(this.contain.obj.config.id);
            // this.templateOptions = data.data
        },
    }
}
</script>
<style lang="scss" scoped>
.engineering{
    /deep/.el-dialog__header{
        border-bottom: 1px solid #363841;
    }
    /deep/.el-select-dropdown.is-multiple .el-select-dropdown__item.selected::after{
        position: relative !important;
    }
}
/deep/textarea::-webkit-input-placeholder{
    text-align: center;
    line-height: 240px;
}
.ok-engineering{
    position: relative;
    top: -20px;
    display: flex;
    flex-direction: column;
    height: 120px;
    .ok-top{
        flex: 1;
        .top-text{
            margin-top: 4px;
            width: 100%;
            text-align: center;
            font-size: 14px;
            color: #fff;
        }
        .top-text1{
            margin-top: 4px;
            font-size: 14px;
            color: #fff;
            .fs-top{
                color: #409EFF !important;
                padding: 0 2px;
            }
        }
        .top-icon{
            width: 100%;
            text-align: center;
            font-size: 20px;
            color: #E6A23C;
        }
    }
    .ok-foot{
        margin-top: 30px;
        display: flex;
        justify-content: flex-end;
    }
}
.text-pei{
    /deep/.el-dialog__body{
        padding: 0px !important;
    }
    /deep/.el-dialog__header{
        padding: 13px 17px 10px !important;
    }
    .text-engineering{
        height: 340px;
        .text-top{
            height: 40px;
            display: flex;
            .text-type{
                width: 50%;
                text-align: center;
                height: 40px;
                line-height: 40px;
                color: #fff;
                cursor: pointer;
            }
            .text-type-clolor{
                background-color: #66b1ff;
            }
        }
    }
    .text-configuration{
        margin-top: 50px;
        margin-bottom: 10px;
        padding-left: 50px;
        .text-type{
            display: flex;
            margin-bottom: 30px;
        }
        .text-a{
            display: flex;
            margin-right: 10px;
            div{
                color: #fff;
                font-size: 13px;
                margin-right: 5px;
                line-height: 26px;
            }
            /deep/.el-color-picker--mini{
                height: 25px;
            }
            /deep/.el-color-picker--mini .el-color-picker__trigger{
                height: 23px !important;
                width: 23px !important;
            }
        }
    }
    .text-foot{
       margin-top: 35px;
       display: flex;
       justify-content: center;
    }
}
.engineering-dialog{
/deep/.el-loading-mask{
    background-color: rgba(0,0,0,.5);
}
/deep/.el-dialog__body{
    padding: 10px 20px;
}
/deep/.el-select__tags{
    flex-wrap: nowrap !important;
    max-width:120px !important;
}
.engineeringEdit-img{
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%,-50%);
    width: 154px;
    height: 158px;
    background: url('~@/assets/engineeringEdit.png') no-repeat;
}
.engineeringEdit{
    position: relative;
    display: flex;
    flex-direction: column;
    width: 100%;
    height: 450px;
    .engineeringEdit-top{
        height: 30px;
    }
    .engineeringEdit-center{
        .img-name{
            display: flex;
            font-size: 14px;
            color: #fff;
            margin: 10px 0;
            justify-content: space-between;
        }
        .img-type{
            display: flex;
            margin-bottom: 15px;
            .img-interval{
                margin-left: 15px;
                margin-right: 15px;
                margin-top: 4px;
                width: 25px;
                height: 25px;
                img{
                    height: 100%;
                    width: 100%;
                }
            }
            .img-delete{
                margin-left: 10px;
                margin-top: 5px;
                color: #fff;
                cursor: pointer;
            }
        }
        .img-select{
            height: 330px;
            margin-bottom: 20px;
            overflow-x: auto;
        }
        /deep/.el-textarea{
            // margin-top: 25px;
            .el-textarea__inner{
                min-height: 260px !important;
            }
        }
        flex: 1;
        .center-one{
            height: 40%;
            margin-top: 15px;
            color: #fff;
            padding-left: 15px;
            padding-top: 10px;
            // border: 1px solid  #34363f;
            // overflow-y: auto;
            .overall{
                height: 100%;
            }
            .title-name{
                margin-bottom: 10px;
            }
            .overall-text{
                color: #fff;
                display: flex;
                margin-bottom: 8px;
            }
            .overall-delete{
                cursor: pointer;
                margin-left: 10px;
                font-size: 18px;
            }
            .overall-all{
                padding-top: 10px;
                height: 80%;
                border: 1px solid  #34363f;
                overflow-y: auto;
            }
        }
        .center-two{
            margin-top: 10px;
            height: 50%;
            // border: 1px solid  #34363f;
            color: #fff;
            padding-left: 15px;
            padding-top: 10px;
            padding-right: 10px;
            // overflow-y: auto;
            .assembly{
                height: 100%;
            }
            .assembly-all{
                overflow-y: auto;
            }
            .assembly-type{
                display: flex;
                .assembly-name{
                    width: 24%;
                    height: 35px;
                    margin-top: 10px;
                    line-height: 35px;
                    text-align: center;
                    border-left: 1px solid  #34363f;
                    border-top: 1px solid  #34363f;
                }
              .assembly-name:last-child{
                border-right: 1px solid  #34363f;
              }
            
            }
            .assembly-delete{
                cursor: pointer;
                font-size: 18px;
                margin-left: 10px;
            }
            .assembly-select{
                display: flex;
                .select-type{
                    width: 24%;
                }
                /deep/.el-input__inner{
                    border-bottom: 0 !important;
                    border-right: 0 !important;
                    border-left: 1px solid  #34363f !important;
                    border-top: 1px solid  #34363f !important;
                    // border: 1px solid red !important;
                }
                .select-type:last-child{
                    /deep/.el-input__inner{
                        border-right:1px solid  #34363f !important
                    }
                }
            }
            .assembly-select:last-child{
                /deep/.el-input__inner{
                    border-bottom:1px solid  #34363f !important
                }
            }
            
        }
    }
    .engineeringEdit-foot{
        display: flex;
        justify-content: flex-end;
    }
    .img-foot{
        display: flex;
        margin-bottom: 20px;
        justify-content: space-around;
        // /deep/.el-loading-spinner .circular{
        //     height: 15px;
        //     width: 15px;
        // }
        // /deep/  .el-loading-spinner{
        //     top: 50%;
        //     margin-top: -8px;
        //     width: 100%;
        //     text-align: right;
        //     position: absolute;
        //     padding-right: 2px;
        // }
    }
}
}
</style>
<style scoped>
[class*='multiple_select_popper'].el-select-dropdown.is-multiple .el-select-dropdown__item.selected::after {
 position: relative !important;
}
</style>

