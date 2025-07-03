package com.stdc.topology2d.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.mp.utils.Condition;
import com.stdc.core.mp.utils.Query;
import com.stdc.core.oss.utils.FileUtil;
import com.stdc.core.oss.utils.MinioTemplate;
import com.stdc.core.oss.utils.OssTemplate;
import com.stdc.core.oss.vo.OssFileVO;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.topology2d.constants.ModelConstants;
import com.stdc.topology2d.constants.TopologyConstants;
import com.stdc.topology2d.entity.dto.TopologyFileDto;
import com.stdc.topology2d.entity.po.TopologyFile;
import com.stdc.topology2d.entity.vo.TopologyFileListVo;
import com.stdc.topology2d.service.ITopologyFileService;
import com.stdc.topology2d.service.Topology2dOssFileService;
import com.stdc.visual.auth.entity.role.dto.SourceType;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.service.RoleSourceService;
import com.stdc.visual.common.utils.StdcVisualConstant;
import com.stdc.visual.entity.po.OssFile;
import com.stdc.visual.mapper.OssFileMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

import static com.stdc.visual.entity.po.OssFile.FILE_SOURCE_TOPOLOGY_2D;

/**
 * @author: wang_jie
 * @data: 2024/12/31
 * @describe: 2d组态平台文件存储服务minio实现
 */
@Slf4j
@Data
@Service
public class Topology2dOssFileServiceMinioImpl implements Topology2dOssFileService {

    //2d组态平台,minio默认存储文件夹
    private static final String TOPOLOGY_2D_DEFAULT_DIR = "2d";

    @Autowired
    private MinioTemplate minioTemplate;

    @Autowired
    private OssFileMapper ossFileMapper;

    @Autowired
    private RoleSourceService roleSourceService;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private ITopologyFileService topologyFileService;


    /**
     * @param dir  文件夹目录,以 / 开头 例如 : "/this/is/a/dir"
     * @param file
     * @return
     */
    @Override
    public TopologyFileDto save(String dir, MultipartFile file) {
//        String fileName = dir + "/" + StringUtil.randomUUID() + "_" + file.getOriginalFilename();
        String fileName = "";
        String id = StringUtil.randomUUID();
        String originalFilename = file.getOriginalFilename();
        if (!originalFilename.contains(".")) {
            fileName = dir + "/" + id + "_thumb.png";
        } else {
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            if (TopologyConstants.TOPOLOGY_FILE_TYPE_THUMBNAIL.equals(dir)) {
                fileName = dir + "/" + id + "_thumb" + extension;
            } else if (TopologyConstants.TOPOLOGY_FILE_TYPE_SVG.equals(dir)) {
                fileName = dir + "/" + id + extension;
            } else {
                fileName = TopologyConstants.TOPOLOGY_FILE_TYPE_OTHER + "/" + id + extension;
            }
        }

        OssFileVO ossFileVO = minioTemplate.putFile(StdcVisualConstant.OSS_TOPOLOGY_PREFIX_BUCKET, fileName, file);
        if (ObjectUtil.isEmpty(ossFileVO)) {
            BaseException.throwException(ResultCode.get("upload_file_error"));
        }
        OssFile ossFile = new OssFile();
        long timeMillis = System.currentTimeMillis();
        ossFile.setId(id)
                .setNamePrefix(FileUtil.getNameWithoutExtension(file.getOriginalFilename()))
                .setNameSuffix(FileUtil.getFileExtension(file.getOriginalFilename()))
                .setLink(ossFileVO.getLink())
                .setDoMain(ossFileVO.getDoMain())
                .setSource(FILE_SOURCE_TOPOLOGY_2D)
                .setIsDeleted(0)
                .setCreateTime(timeMillis)
                .setUpdateTime(timeMillis);
        int i = ossFileMapper.insert(ossFile);
        if (i > 0) {
            SourceType saveType = SourceType.OSS_FILE_TOPOLOGY_2D;
            roleSourceService.saveRoleSource(id, saveType);
            OssFile resOssfile = ossFileMapper.selectById(id);

            //存入stdc_topology_file表
            TopologyFile topologyFile = new TopologyFile();
            topologyFile.setId(id);
            topologyFile.setDirectory(dir);
            topologyFile.setName(FileUtil.getNameWithoutExtension(file.getOriginalFilename()));
            topologyFile.setFilename(fileName.substring(fileName.lastIndexOf('/') + 1));
            topologyFile.setUrl(resOssfile.getLink());
            topologyFile.setUploadDate(new Date());
            topologyFile.setUsername("admin");
            topologyFileService.save(topologyFile);

            TopologyFileDto res = new TopologyFileDto();
            res.setFilename(originalFilename);
            res.setUrl(resOssfile.getLink());
            res.setImagename(resOssfile.getLink().substring(resOssfile.getLink().lastIndexOf("/") + 1));
            return res;
        }
        return null;
    }

    @Override
    public boolean del(String path) {
        if (StringUtil.isBlank(path)) {
            BaseException.throwException(ResultCode.get("path_is_empty"));
        }
        String id = "";

        if (path.endsWith(TopologyConstants.TOPOLOGY_FILE_ENDWITH_THUMB)) {
            id = path.substring(0, path.lastIndexOf(TopologyConstants.TOPOLOGY_FILE_ENDWITH_THUMB));
        } else {
            id = path.substring(0, path.lastIndexOf("."));
        }

        OssFile ossFile = ossFileMapper.selectById(id);
        if (ObjectUtil.isEmpty(ossFile)) {
            return true;
        }

        topologyFileService.removeById(id);

        //删除minio文件
        String[] doMain = ossFile.getDoMain().split(StringPool.SLASH, 2);
        ossTemplate.removeFile(StdcVisualConstant.OSS_TOPOLOGY_PREFIX_BUCKET, doMain[0] + StringPool.SLASH + doMain[1]);
        int i = ossFileMapper.deleteById(id);
        return i > 0;
    }

    @Override
    public R<IPage<TopologyFile>> fileList(TopologyFileListVo topologyFileListVo) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ModelConstants.TOPOLOGY_DATA_USERNAME_PROPERTY, AuthUtils.getUser().getUsername());

        if (!StringUtils.isEmpty(topologyFileListVo.getDirectory())) {
            queryWrapper.eq(ModelConstants.TOPOLOGY_FILE_DIRECTORY, topologyFileListVo.getDirectory());
        }
        Query query = new Query();
        query.setCurrent(topologyFileListVo.getCurrent());
        query.setSize(topologyFileListVo.getPageSize());
        IPage<TopologyFile> pages = topologyFileService.page(Condition.getPage(query), queryWrapper);
        return R.data(pages);
    }
}
