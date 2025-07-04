<template>
  <div>
    <el-input
      style="width:15%;float:left"
      v-model="input"
      placeholder="请输入字体文件名"
    ></el-input>
    <el-upload
      style="width:28%;padding:4px"
      class="upload-demo"
      ref="upload"
      accept=""
      action=""
      :before-upload="beforeUpload"
      :on-preview="handlePreview"
      :on-remove="handleRemove"
      :file-list="fileList"
      :auto-upload="false"
      :http-request="httpRequest"
    >
      <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
      <el-button
        style="margin-left: 10px"
        size="small"
        type="success"
        @click="submitUpload"
        >上传到服务器</el-button
      >
    </el-upload>
  </div>
</template>
<script>
import { getFontList } from "@/api/dataCollection";
import { dicOption } from "@/option/config";
import { url } from '@/config'
export default {
  data() {
    return {
      fileList: [],
      input: "",
      dicOption: dicOption,
    };
  },
  methods: {
    beforeUpload(file) {},
    httpRequest(param) {
      const formData = new FormData();
      formData.append("file", param.file);
      formData.append("fileName", this.input);
      axios
        .post(url + "/visual/put-otf", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then((res) => {
          if (res.status === 200) {
            this.$message.success("上传文件成功!");
            getFontList().then((res) => {
              dicOption.fontFamily.splice(4, 1000);
              res.data.data.forEach((el) => {
                dicOption.fontFamily.push({
                  label: el.name.slice(40, -4),
                  value: el.originalName.split(".")[0],
                });
              });
            });
          }
        })
        .catch((err) => {
          this.$message.error("上传失败!");
        });
    },
    submitUpload() {
      this.$refs.upload.submit();
    },
  },
};
</script>
<style lang="scss" scoped>
</style>