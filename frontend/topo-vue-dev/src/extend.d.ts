/**
 * declare告诉编译器知道Window是啥类型，并且和全局的Window类型自动合并
 */

interface Window {
    MyGlobalConfigObject: any;//全局变量名
    FileObj:{
        name:string,
        folder:string,
        folderType:string
        class:string,
        categoryValue:string,
        categoryKey:string,
    }
}