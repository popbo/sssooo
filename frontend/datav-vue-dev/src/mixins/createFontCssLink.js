import { getFontCssUrl } from '@/api/style.js'
import { url } from '@/config'
export default {
  methods: {
    async createFontCssLink() {
      try {
        const res = await getFontCssUrl()
        if (res.data.success) {
          const linkUrl = url + res.data.data.replace('/stdc-visual', '')
          const link = document.createElement('link')
          link.setAttribute('rel', 'stylesheet')
          link.setAttribute('type', 'text/css')
          link.setAttribute('href', linkUrl)
          document.getElementsByTagName('head')[0].appendChild(link)
        }
      } catch (error) {}
    },
  },
  mounted() {
    this.createFontCssLink()
  }
}