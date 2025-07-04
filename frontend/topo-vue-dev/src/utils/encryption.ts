
import CryptoJS from 'crypto-js'
import { cloneDeep } from 'lodash';
const aesKey = "59abbe5e10adf20f883ec3949ba6e057";  
// 将秘钥转换成Utf8字节数组  
const key = CryptoJS.enc.Utf8.parse(aesKey);  
const iv =CryptoJS.enc.Utf8.parse(aesKey.substring(0, 16)); // 十六位十六进制数作为密钥偏移量

// 解密方法
const Decrypt = function (word) {
  if(word===undefined||null||'') return ''
  let encryptedHexStr = CryptoJS.enc.Hex.parse(word);
  let srcs = CryptoJS.enc.Base64.stringify(encryptedHexStr);
  let decrypt = CryptoJS.AES.decrypt(srcs, key, {
    iv: iv,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7
  });
  let decryptedStr = decrypt.toString(CryptoJS.enc.Utf8);
  return decryptedStr.toString();
};

// 加密方法
const Encrypt = function (word) {  
  if(word===undefined||null||'') return ''
  let srcs = CryptoJS.enc.Utf8.parse(word);
  let encrypted = CryptoJS.AES.encrypt(srcs, key, {
    iv: iv,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7
  });
  return encrypted.ciphertext.toString().toUpperCase();
};

    // 加密配置项密码
    const encryptPassword=(arr:any[],str:String)=>{
        let obj:{password:string}={
            password: '',
        }
     return  arr.map(item=>{
          obj=cloneDeep(item)
          if((str==='http'||'websocket')&&(item.authenticationMethod==='2')){
            obj.password=Encrypt(item.password)
          }else if(str==='mqtt'){
            if(item?.mqttOptions?.password){
              (obj as any).mqttOptions.password=Encrypt((item.mqttOptions?.password))   
            }
            // return (obj as any).mqttOptions
          }else if(str==='realtimedatabase'){
            obj.password=Encrypt(item.password)
          }     
           return obj
        })
    }
    //解密配置项密码
    const decryptPassword=(arr:any[],str:String)=>{
        let obj:{password:string}={
            password: ''
        }
     return  arr?.map(item=>{
         obj=cloneDeep(item)
          if((str==='http'||'websocket')&&(item.authenticationMethod==='2')){
            obj.password=Decrypt(item.password)
          }else if(str==='mqtt'){
            (obj as any).mqttOptions.password=Decrypt(cloneDeep(item.mqttOptions.password))
          }else if(str==='realtimedatabase'){
            obj.password=Decrypt(item.password)
          }     
           return obj
        })
    }

export {
  Decrypt,
  Encrypt,
  encryptPassword,
  decryptPassword
};


