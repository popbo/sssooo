const souceDate={
     namespaced: true,
     state:()=>({
        http:[],//http数据池变化时
        BindDate:[],//绑定的图元，属性和设备id,
     // [{
     //   id:props.pen.id,
     //   data: [
     //   {
     //     dataIds: {
     //       dataId: '', //设备id
     //       http: '', //协议id
     //       name:''//设备名称
     //       communicationProtocolId:''//协议类型
     //       communicationProtocolName:''//协议类型
     //     },//只有绑定时才会有
     //     key: '', //属性
     //     name:'', //属性名称
     //     type:'', //属性类型
     //   },
     //   {
     //     dataIds: {
     //       dataId: '', //设备id
     //       http: '', //协议id
     //     },
     //     key: '', //属性
     //   },
     // ];
     // }];
        RealTimeDataBase:[],//RealTimeDataBase数据池
        TDengine: [],//TDengine
        // BindTag:[],//绑定的图元，属性和设备id,
        // arr:[
        //     {
        //         tag:'tag1',
        //         RealTimeDataBaseId:'',//实时数据库的id
        //         dataId:''//设备编号
        //     }
        // ]
     }),
     mutations:{
        setHttp(state,paylaod){
        // 判断是否已经存在
        let num=-1
        state.http.forEach((item,index)=>{
           if(item.id===paylaod.id){
            num = index
           }
        })
        if(num===-1){
            state.http.push({
            id:paylaod.id,
            data:paylaod.data
            })
        }else{
            state.http[num]={
            id:paylaod.id,
            data:paylaod.data
            }
         }
        },//保存Http数据池

        setRealTimeDataBase(state,paylaod){
        // 判断是否已经存在
        let num=-1
        state.RealTimeDataBase.forEach((item,index)=>{
           if(item.id===paylaod.id){
            num = index
           }
        })
        if(num===-1){
            state.RealTimeDataBase.push({
            id:paylaod.id,
            data:paylaod.data
            })
        }else{
            state.RealTimeDataBase[num]={
            id:paylaod.id,
            data:paylaod.data
            }
         }
        },//保存RealTimeDataBase数据池

        setTDengine(state, paylaod) {
          // 判断是否已经存在
          let num = -1;
          state.TDengine.forEach((item, index) => {
            if (item.id === paylaod.id) {
              num = index;
            }
          });
          if (num === -1) {
            state.TDengine.push({
              id: paylaod.id,
              data: paylaod.data,
            });
          } else {
            state.TDengine[num] = {
              id: paylaod.id,
              data: paylaod.data,
            };
          }
        }, //保存RealTimeDataBase数据池

        setBindDate(state,paylaod){
        // 判断图元是否已经存在
        let num=-1
        state.BindDate.forEach((item,index)=>{
           if(item.id===paylaod.id){
            num = index
           }
        })
        if(num===-1){
            state.BindDate.push(paylaod)
        }else{
             // 如果有就覆盖更新
             state.BindDate[num]={...paylaod}
         }
 
        },//设置的同时也传入Meta2d.store中,当获取数据时也
        
     },
     getters:{
        getHttp:(state)=>(index,id)=>{
            //index是协议id,id是设备id
            
            let value:any =null;
            // 如果有对应数据集那value就不为空
            
            state.http.forEach(item=>{
              if(item.id===index){//对应数据集内查找设备id对应的值
                value = item.data.find(item2=>item2.dataId===id)
              }
            })     
 
           return value
        },
        getRealTimeDataBase:(state)=>(index,id)=>{
            //index是协议id,id是设备id
            
            let value:any =null;
            // 如果有对应数据集那value就不为空
            
            state.RealTimeDataBase.forEach(item=>{
              if(item.id===index){//对应数据集内查找设备id对应的值
                value = item.data.find(item2=>item2.dataId===id)
              }
            })     
 
           return value
        },
        getDataPool:(state)=>(index,id,str)=>{
            //index是协议id ,str协议类型,id是设备id
            
            let value:any =null;
            let dataPool:any[]
            if(str==='HTTP'){
                dataPool=state.http
            }else if(str==='RealTimeDataBase'){
                dataPool=state.RealTimeDataBase
            }else if (str === 'TDengine') {
                dataPool = state.TDengine;
            } else{
              return value
            }
            // 如果有对应数据集那value就不为空
            dataPool.find(item=>{
              if(item.id===index){//对应数据集内查找设备id对应的值
                value = item.data.find(item2=>item2.dataId===id)
              }
            })     
            
           return value
        },
        getBindDate:(state)=>{
            return state.BindDate
        },
        //根据绑定的信息，解析出，需要监听的协议，和对应协议下的图元
        getWatchData:(state)=>{
            // [
            //     {
            //         id:[
            //             {
            //                 mapId:'图元id',
            //                 dataItem:[{
            //                     key:'属性',
            //                     dataId:'对应的变量'//协议对应的变量
            //                 }]
            //             }
            //         ]//协议id:
            //     }
            // ]
            let obj={}
            state.BindDate.forEach(item=>{//item 各个图元项
                item.data.forEach(formItem=>{ //图元项下面的各个配置项
                    // formItem.dataIds.http//协议--key
                    // 先判断formItem.dataIds上面是否为空（有绑定时不为空）
                    if(JSON.stringify(formItem.dataIds)!==undefined){
                        //  console.log(JSON.stringify(formItem.dataIds)); // { dataId: "d-1-a-001"http: 1name: "车辆"}可能是对象也有可能是数组
                        if(obj.hasOwnProperty(formItem.dataIds.http)){//判断obj上是否有这个属性即协议，有true
                        // 有-> 遍历obj看看图元是否已经存在
                            let num:number=null
                            obj[formItem.dataIds.http].find((idItem,index)=>{//idItem是指协议下面的不同图元项
                                if(idItem.mapId===item.id){
                                    num=index
                                }
                            })                        
                            if(num!==null){ //如果已经存在该图元-》追加属性
                                obj[formItem.dataIds.http][num].dataItem.push({
                                    key:formItem.key,//属性名字
                                    dataId:formItem.dataIds.dataId //设备id
                                })
                            }else{
                            // 相同协议下 ,图元不存在-》追加图元
                                obj[formItem.dataIds.http].push({
                                    mapId:item.id,
                                    dataItem:[{
                                        key:formItem.key,//属性名字
                                        dataId:formItem.dataIds.dataId //设备id
                                    }]
                                })
                            }

                        }else{
                        // 没有这个协议-->加这个图元--->这个属性还有这个设备id
                        obj[formItem.dataIds.http]=[{
                            mapId:item.id,
                            dataItem:[{
                                key:formItem.key,//属性名字
                                dataId:formItem.dataIds.dataId //设备id
                            }]
                        }]
                      }
                    }
                   
                })
                
            })
            return obj
        }  
    }



}

export default souceDate;