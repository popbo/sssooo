import { myStorage } from '@/utils/storage.js'
import { getFile } from '@/api/dataCollection'
export default {
  data() {
    return {
      allFontFamilyArr: []
    }
  },
  created() {
    if(!myStorage.get('allFontFamilyArr')) {
      console.log('请求字体')
      getFile('custom', 'font').then((res) => {
        if (res.data.success) {
          const updataFont = []
          res.data.data.records.forEach((el) => {
            updataFont.push({
              label: el.namePrefix,
              value: el.namePrefix,
              id: el.id
            })
          })
          myStorage.set('allFontFamilyArr', this.allFontFamilyArr = this.dicOption.fontFamily.concat(updataFont))
          // this.SET_ALL_FONTFAMILY_ARR(this.deepClone(this.allFont))
        } else {
          this.loading = false
          this.$message.error('获取字体资源失败')
        }
      })
    } else {
      console.log('不请求字体')
      this.allFontFamilyArr = myStorage.get('allFontFamilyArr')
    }
  },
}