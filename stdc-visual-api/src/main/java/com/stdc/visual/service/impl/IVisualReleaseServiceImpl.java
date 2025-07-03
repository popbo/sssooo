package com.stdc.visual.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.ObjectUtil;


import com.stdc.core.tool.utils.StringPool;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.user.AuthUtils;
import com.stdc.visual.auth.entity.visual.VisualRelease;
import com.stdc.visual.auth.mapper.VisualReleaseMapper;
import com.stdc.visual.entity.dto.VisualDTO;
import com.stdc.visual.entity.po.Visual;
import com.stdc.visual.entity.po.VisualConfig;
import com.stdc.visual.service.IVisualConfigService;
import com.stdc.visual.service.IVisualService;
import com.stdc.visual.service.IVisualReleaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/10/18--18:31
 * @describe: 大屏发布
 */
@Service
public class IVisualReleaseServiceImpl extends ServiceImpl<VisualReleaseMapper, VisualRelease> implements IVisualReleaseService {

    /**
     * 新发布的大屏，新增的版本号的后缀
     */
    private static final String RELEASE_REMARK = "大屏发布" + StringPool.DOT + "V";

    @Autowired
    private VisualReleaseMapper releaseMapper;

    @Autowired
    private IVisualService visualService;

    @Autowired
    private IVisualConfigService configService;


    @Override
    @Transactional
    public Boolean releaseVisual(VisualRelease release) {
        if (release.getIsRelease() != 1){
            BaseException.throwException(ResultCode.get("this_release_path_is_not_release"));
        }
        //发布大屏操作，即新增一个版本
        VisualDTO dto = new VisualDTO();
        Visual visual = visualService.getById(release.getId());
        //1.0.11版本设置为必传version
        QueryWrapper queryWrapper = new QueryWrapper<>().eq("visual_id",release.getId()).eq("version", release.getVersion());
        VisualConfig config =configService.getOne(queryWrapper);
        if (ObjectUtil.isEmpty(visual) || ObjectUtil.isEmpty(config)){
            return false;
        }
        //发布新版本，不修改原来的大屏封面id
        config.setBackgroundId(visual.getBackgroundId());
        dto.setVisual(visual);
        dto.setConfig(config);
        //大屏发布信息
        VisualRelease visualRelease = releaseMapper.selectById(release.getId());
        //备份配置快照
        release.setConfigId(config.getId());
        release.setDetail(config.getDetail());
        release.setComponent(config.getComponent());
        //发布者用户名
        release.setUsername(AuthUtils.getUserDetails().getUsername());
        // 未存在 即保存
        if (ObjectUtil.isEmpty(visualRelease)){
            //筛序地址是否重复
            VisualRelease isExit = releaseMapper.selectOne(new LambdaQueryWrapper<VisualRelease>()
                    .eq(VisualRelease::getPath, release.getPath())
                    .eq(VisualRelease::getSource, 0));
            if (ObjectUtil.isNotEmpty(isExit)){
                BaseException.throwException(ResultCode.get("this_release_path_is_exit"));
            }
            return (releaseMapper.insert(release) > 0);
        }
        //大屏发布信息已存在 即修改
        else {
            //筛序地址是否重复
            VisualRelease isExit = releaseMapper.selectOne(new LambdaQueryWrapper<VisualRelease>()
                    .eq(VisualRelease::getPath, release.getPath())
                    .eq(VisualRelease::getSource, 0)
                    .ne(VisualRelease::getId,release.getId()));
            if (ObjectUtil.isNotEmpty(isExit)){
                BaseException.throwException(ResultCode.get("this_release_path_is_exit"));
            }
            return (releaseMapper.updateById(release) > 0);
        }
    }

    @Override
    public Boolean delReleaseById(VisualRelease release) {
        return releaseMapper.deleteById(release.getId()) > 0;
    }

    @Override
    public Boolean delReleaseById(Long id) {
        return releaseMapper.deleteById(id) > 0;
    }

    @Override
    public Boolean updateReleaseById(VisualRelease release) {
        if (StringUtil.hasText(release.getPath())){
            //筛序地址是否重复
            VisualRelease isExit = releaseMapper.selectOne(new LambdaQueryWrapper<VisualRelease>().eq(VisualRelease::getPath, release.getPath()));
            if (ObjectUtil.isNotEmpty(isExit)){
                BaseException.throwException(ResultCode.get("this_release_path_is_exit"));
            }
        }
        return releaseMapper.updateById(release) > 0;
    }

    @Override
    public List<VisualRelease> queryAll() {
        return releaseMapper.selectList(Wrappers.emptyWrapper());
    }

    @Override
    public VisualRelease queryOneById(String id) {
        return releaseMapper.selectById(id);
    }

    @Override
    public VisualRelease queryOneByPath(String path, Integer source) {
        VisualRelease release = releaseMapper.selectOne(
                new LambdaQueryWrapper<VisualRelease>()
                .eq(VisualRelease::getPath, path)
                .eq(VisualRelease::getSource, source));
        return release;
    }

    @Override
    public VisualRelease queryOneByVisualId(Long visualId) {
        VisualRelease release = releaseMapper.selectOne(new LambdaQueryWrapper<VisualRelease>().eq(VisualRelease::getId, visualId));
        if (ObjectUtil.isEmpty(release)) return release;
        if (!StringUtil.hasText(release.getVersion())) {
            VisualConfig config = configService.getById(release.getConfigId());
            if (ObjectUtil.isNotEmpty(config)) release.setVersion(config.getVersion());
        }
        release.setComponent(null);
        release.setDetail(null);
        return release;
    }
}
