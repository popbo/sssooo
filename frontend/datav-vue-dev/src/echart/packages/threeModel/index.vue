<template>
<div class="three-model">
  <div :id='id+"3d"'>
  </div>
</div>
</template>
<script>
import create from '../../create'
import components from '@/components/'
import common from '@/config'
import { getFunction } from '@/utils/utils.min.js'
import _ from 'lodash'
import * as THREE from "three";
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls'
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader'
export default create({
  name: 'threeModel',
  inject: ['main','activeObj'],
  data() {
    return {
        scene:null,
        getRenderer:null,
        camera:null,
        common: common,
        getFunction: getFunction,
        model:'',
        gltfScene:[],
        dIndex:0,
    }
  },
  props: {
    option: Object,
    component: Object,
    id:String
  },
  watch:{
    dataChart:{
      handler(val,old){
        console.log(99999,val,old)
        if(val.code){
          if(val.code===200){
            this.model = val.data || ''
          }
        }else{
          this.model = val.model || '';
        }
        console.log(val,this.model ,"3d-----------------------------",this.$route)
        if(this.$route.query.deviceId){
          // console.log('old.model',old.model)
          if(old.model!=='undefined' || old.data!=='undefined'){
            this.clearScene()
          }
          // setTimeout(()=>{
          //   this.clearScene()
          // },400)
        }else{
          this.clearScene()
        }
      },
      deep:true
    },
    component:{
      handler(){
        if(this.$route.name==='build'){
          this.clearScene()
        }
      },
       deep: true,
    },
    'option.model':{
      handler(){
        if(this.$route.name==='build'){
          this.clearScene()
        }
      },
    },
    'option.Mbgcolor':{
      handler(){
        if(this.$route.name==='build'){
          this.clearScene()
        }
      },
    },
  },
  mounted() {
    console.log(666)
  },
  methods: {
    getThreeModel(){
      let self = this
      self.scene = new THREE.Scene();
      const loader = new GLTFLoader();
      loader.load(`${self.model}`,function(gltf){
        let box = new THREE.Box3().setFromObject(gltf.scene); // 获取模型的包围盒
        let mdlen = box.max.x - box.min.x; // 模型长度
        let mdwid = box.max.z - box.min.z; // 模型宽度
        let mdhei = box.max.y - box.min.y; // 模型高度
        let x1 = box.min.x + mdlen / 2; // 模型中心点坐标X
        let y1 = box.min.y + mdhei / 2; // 模型中心点坐标Y
        let z1 = box.min.z + mdwid / 2; // 模型中心点坐标Z
        gltf.scene.position.set(-x1, -y1, -z1); 
        gltf.scene.rotation.set(0,-THREE.MathUtils.degToRad(35),0); 
        // gltf.scene.position.set(0,-3,0)
        // console.log('_____________________________',gltf);
        self.scene.add(gltf.scene);
      })
      //创建一个长方体几何对象Geometry
      // const geometry = new THREE.BoxGeometry(100, 100, 100); 
      // //创建一个材质对象Material
      // const material = new THREE.MeshLambertMaterial({
      //     color: 0xff0000,//0xff0000设置材质颜色为红色
      //     // transparent: true,
      //     // opacity:0.2,

      // }); 
      // const mesh = new THREE.Mesh(geometry, material); //网格模型对象Mesh
      // //设置网格模型在三维空间中的位置坐标，默认是坐标原点
      // mesh.position.set(0,0,0);
      const axesHelper = new THREE.AxesHelper( 200 );
      // scene.add(axesHelper);
      // scene.add(mesh); 
      //点光源
      const pointlightx1 = new THREE.PointLight(0xffffff,0.5);
      pointlightx1.position.set(100,0,0);
      // const pointlightx2 = new THREE.PointLight(0xffffff,0.5);
      // pointlightx2.position.set(-100,0,0);
      const pointlightx3 = new THREE.PointLight(0xffffff,0.5);
      pointlightx3.position.set(100,-100,100);
      // const pointlightx4 = new THREE.PointLight(0xffffff,0.5);
      // pointlightx4.position.set(100,-100,-100);


      const pointlighty1 = new THREE.PointLight(0xffffff,0.5);
      pointlighty1.position.set(0,100,0);
      const pointlighty2 = new THREE.PointLight(0xffffff,0.5);
      pointlighty2.position.set(0,-100,0);
      // const pointlighty3 = new THREE.PointLight(0xffffff,0.5);
      // pointlighty3.position.set(-100,100,100);
      // const pointlighty4 = new THREE.PointLight(0xffffff,0.5);
      // pointlighty4.position.set(-100,-100,100);

      // const pointlightz1 = new THREE.PointLight(0xffffff,0.5);
      // pointlightz1.position.set(0,0,100);
      // const pointlightz2 = new THREE.PointLight(0xffffff,0.5);
      // pointlightz2.position.set(0,0,-100);
      // const pointlightz3 = new THREE.PointLight(0xffffff,0.5);
      // pointlightz3.position.set(100,100,100);
      // const pointlightz4 = new THREE.PointLight(0xffffff,0.5);
      // pointlightz4.position.set(100,100,-100);
      self.scene.add(pointlightx1);
      // self.scene.add(pointlightx2);
      self.scene.add(pointlightx3);
      // self.scene.add(pointlightx4);

      self.scene.add(pointlighty1);
      self.scene.add(pointlighty2);
      // self.scene.add(pointlighty3);
      // self.scene.add(pointlighty4);

      // self.scene.add(pointlightz1);
      // self.scene.add(pointlightz2);
      // self.scene.add(pointlightz3);
      // self.scene.add(pointlightz4);
      //平行光
      // const directLight1 = new THREE.DirectionalLight(0xffffff,3.0);
      // // const help1 = new THREE.DirectionalLightHelper(directLight1,100)
      // directLight1.position.set(40,20,30);
      // self.scene.add(directLight1);
      // scene.add(help1)
      // const directLight2 = new THREE.DirectionalLight(0xffffff,1.0);
      // const help2 = new THREE.DirectionalLightHelper(directLight2,100)
      // directLight1.position.set(1,0,0);
      // scene.add(directLight2);
      // scene.add(help2)
      // const directLight3 = new THREE.DirectionalLight(0xffffff,1.0);
      // const help3 = new THREE.DirectionalLightHelper(directLight3,100)
      // directLight1.position.set(0,0,1);
      // scene.add(directLight3);
      // scene.add(help3)

      //环境光
      const ambient = new THREE.AmbientLight(0xffffff,2.3)
      self.scene.add(ambient);

      // width和height用来设置Three.js输出的Canvas画布尺寸(像素px)
      // 30:视场角度, width / height:Canvas画布宽高比, 1:近裁截面, 3000：远裁截面
      self.camera = new THREE.PerspectiveCamera(30, this.component.width / this.component.height, 0.1, 3000);

      // 实例化一个透视投影相机对象
      // const camera = new THREE.PerspectiveCamera();
      //相机在Three.js三维坐标系中的位置
      // 根据需要设置相机位置具体值
      // camera.position.set(-1000, 0, 0); 
      // //相机观察目标指向Threejs 3D空间中某个位置
      // camera.lookAt(4000, 0, 0); //坐标原点
      // camera.lookAt(0, 10, 0);  //y轴上位置10
      // camera.lookAt(mesh.position);//指向mesh对应的位置


      // 相机位置xyz坐标：0,10,0
      // mesh.position.set(0,10,0);
      // 相机位置xyz坐标：200, 200, 200
      self.camera.position.set(1, 2, 15);
      self.camera.lookAt(0,0,0);  
      // scene.add(camera);
      // 创建渲染器对象
      self.getRenderer = new THREE.WebGLRenderer();
      // 定义threejs输出画布的尺寸(单位:像素px)
      self.getRenderer.setSize(this.component.width, this.component.height); //设置three.js渲染区域的尺寸(像素px)
      self.getRenderer.setClearColor(`${self.option.Mbgcolor}`,1.0);
      // renderer.setClearColor('#ffffff',1.0);
      self.getRenderer.render(self.scene, self.camera); //执行渲染操作
      // document.body.appendChild(renderer.domElement); 

      document.getElementById(self.id+'3d').appendChild(self.getRenderer.domElement);
      function render(){
          self.getRenderer.render(self.scene,self.camera);
          requestAnimationFrame(render);
      }
      render();
      const controls = new OrbitControls(self.camera,self.getRenderer.domElement);
      controls.addEventListener('change',function(){
          self.getRenderer.render(self.scene,self.camera);
          console.log("camera.position",self.camera.position);
      })
    },
    clearScene() {
      // cancelAnimationFrame('webgl');
      if(this.getRenderer){
        cancelAnimationFrame(this.id+'3d')
        document.getElementById(this.id+'3d').removeChild(this.getRenderer.domElement)
        this.scene.traverse((child) => {
          if (child.material) {
            child.material.dispose();
          }
          if (child.geometry) {
            child.geometry.dispose();
          }
          child = null;
        });
        this.getRenderer.forceContextLoss();
        this.getRenderer.dispose();
        this.scene.clear();
        this.scene = null;
        this.camera = null;
        this.getRenderer.domElement = null;
        this.getRenderer = null;
        this.getThreeModel()
      }else{
        this.getThreeModel()
      }
    },
  },
  components: components,
})
</script>
<style lang="scss" scoped>
</style>
