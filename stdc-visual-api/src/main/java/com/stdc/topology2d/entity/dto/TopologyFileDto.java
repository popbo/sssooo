package com.stdc.topology2d.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: feng_qiang
 * @date: 2025/05/06--16:46
 * @describe:
 */
@Data
public class TopologyFileDto implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 文件名
     */
    @JsonProperty
    private String filename;

    /**
     * URL
     */
    @JsonProperty
    private String url;

    /**
     * 图片名
     */
    @JsonProperty
    private String imagename;

    /**
     * 字节长度
     */
    @JsonProperty
    private long length;

    /**
     * 元数据
     */
    @JsonProperty
//    private MetadataInfo metadata;
    private String metadata;

    /**
     * 文件内容
     */
    @JsonIgnore
    private byte[] fileContent;

    /**
     * 上传日期
     */
    @JsonProperty
    private String uploadDate;

    /**
     * miniod自定义封面地址sharedimage
     */
    @JsonProperty
    private String sharedimage;
}