package com.stdc.visual.entity.request;

import com.stdc.visual.entity.po.OssFile;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @author: wang_jie
 * @data: 2022/5/30--11:23
 * @describe: 文件存储请求类
 */
@Data
public class OssFileRequest extends OssFile implements Serializable {

    private static final long serialVersionUID = 1L;

}
