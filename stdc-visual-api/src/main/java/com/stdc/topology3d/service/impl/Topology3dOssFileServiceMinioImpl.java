package com.stdc.topology3d.service.impl;

import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.oss.utils.FileUtil;
import com.stdc.core.oss.utils.MinioTemplate;
import com.stdc.core.oss.vo.OssFileVO;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.topology3d.service.Topology3dOssFileService;
import com.stdc.visual.auth.entity.role.dto.SourceType;
import com.stdc.visual.auth.service.RoleSourceService;
import com.stdc.visual.common.utils.StdcVisualConstant;
import com.stdc.visual.entity.po.OssFile;
import com.stdc.visual.mapper.OssFileMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.stdc.visual.entity.po.OssFile.FILE_SOURCE_TOPOLOGY_3D;

/**
 * @author: wang_jie
 * @data: 2024/12/31
 * @describe: 2d组态平台文件存储服务minio实现
 */
@Slf4j
@Data
@Service
public class Topology3dOssFileServiceMinioImpl implements Topology3dOssFileService {

    //2d组态平台,minio默认存储文件夹
    private static final String TOPOLOGY_3D_DEFAULT_DIR = "3d";

    @Autowired
    private MinioTemplate minioTemplate;

    @Autowired
    private OssFileMapper ossFileMapper;

    @Autowired
    private RoleSourceService roleSourceService;


    /**
     *
     * @param dir 文件夹目录,以 / 开头 例如 : "/this/is/a/dir"
     * @param file
     * @return
     */
    @Override
    public OssFile save(String dir, MultipartFile file) {
        String fileName = TOPOLOGY_3D_DEFAULT_DIR + dir + "/" + StringUtil.randomUUID() + "_" + file.getOriginalFilename();
        OssFileVO ossFileVO = minioTemplate.putFile(StdcVisualConstant.OSS_TOPOLOGY_PREFIX_BUCKET, fileName, file);
        if (ObjectUtil.isEmpty(ossFileVO)){
            BaseException.throwException(ResultCode.get("upload_file_error"));
        }
        OssFile ossFile = new OssFile();
        String id = StringUtil.randomUUID();
        long timeMillis = System.currentTimeMillis();
        ossFile.setId(id)
                .setNamePrefix(FileUtil.getNameWithoutExtension(file.getOriginalFilename()))
                .setNameSuffix(FileUtil.getFileExtension(file.getOriginalFilename()))
                .setLink(ossFileVO.getLink())
                .setDoMain(ossFileVO.getDoMain())
                .setSource(FILE_SOURCE_TOPOLOGY_3D)
                .setIsDeleted(0)
                .setCreateTime(timeMillis)
                .setUpdateTime(timeMillis);
        int i = ossFileMapper.insert(ossFile);
        if (i > 0){
            SourceType saveType = SourceType.OSS_FILE_TOPOLOGY_3D;
            roleSourceService.saveRoleSource(id,saveType);
            return ossFileMapper.selectById(id);
        }
        return null;
    }
}
