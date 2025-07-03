package com.stdc.visual.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.auth.entity.role.dto.SourceType;
import com.stdc.visual.auth.service.RoleSourceService;
import com.stdc.visual.entity.po.BookMark;
import com.stdc.visual.entity.po.BookMarkType;
import com.stdc.visual.mapper.BookMarkMapper;
import com.stdc.visual.mapper.BookMarkTypeMapper;
import com.stdc.visual.service.IBookMarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/6/23--15:02
 * @describe:
 */
@Service
public class BookMarkServiceImpl implements IBookMarkService {

    @Autowired
    private BookMarkMapper bookMarkMapper;
    
    @Autowired
    private BookMarkTypeMapper bookMarkTypeMapper;

    @Autowired
    private RoleSourceService roleSourceService;

    @Override
    public List<BookMark> queryBookMarkByName(String name) {
        List<BookMark> bookMarkS = bookMarkMapper.selectList(new LambdaQueryWrapper<BookMark>().eq(BookMark::getName, name));
        return bookMarkS;
    }

    @Override
    public BookMark queryBookMarkById(String id) {
        BookMark bookMark = bookMarkMapper.selectOne(new LambdaQueryWrapper<BookMark>().eq(BookMark::getId, id));
        return bookMark;
    }

    @Override
    public List<BookMark> queryVisualBookMarkByType(String id) {

        LambdaQueryWrapper<BookMark> queryWrapper = new LambdaQueryWrapper<BookMark>().eq(BookMark::getTypeId, id);
        return bookMarkMapper.selectList(queryWrapper);
    }

    @Override
    public List<BookMark> queryAllBookMark() {
        List<String> ids = roleSourceService.queryCurrentRoleSourceId(SourceType.BOOKMARK);
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        List<BookMark> bookMarkS = bookMarkMapper.selectList(new LambdaQueryWrapper<BookMark>().in(BookMark::getId,ids));
        return bookMarkS;
    }

    @Override
    public Boolean updateBookMarkById(BookMark bookMark) {
        int update = bookMarkMapper.updateById(bookMark);
        return update > 0;
    }

    @Override
    public Boolean delBookMarkById(String id) {
        int del = bookMarkMapper.deleteById(id);
        return del > 0;
    }

    @Override
    public Boolean saveWithHandlerDetail(BookMark bookMark) {
        String id = StringUtil.randomUUID();
        bookMark.setId(id);
        if (bookMark.getDetail() instanceof String){
            Boolean tempRs = roleSourceService.saveRoleSource(id, SourceType.BOOKMARK);
            return bookMarkMapper.insert(bookMark) > 0 && tempRs;
        }else {
            throw new RuntimeException("detail类型应为String");
        }
    }

    @Override
    public BookMarkType queryBookMarkTypeById(String id) {
        return bookMarkTypeMapper.selectById(id);
    }

    @Override
    public BookMarkType queryBookMarkTypeByName(String name) {
        LambdaQueryWrapper<BookMarkType> queryWrapper = new LambdaQueryWrapper<BookMarkType>().eq(BookMarkType::getName, name);
        return bookMarkTypeMapper.selectOne(queryWrapper);
    }

    @Override
    public List<BookMarkType> queryAllBookMarkTypes() {
        List<String> ids = roleSourceService.queryCurrentRoleSourceId(SourceType.BOOKMARK_TYPE);
        if (CollectionUtils.isEmpty(ids)){
            return new ArrayList<>();
        }
        return bookMarkTypeMapper.selectList(new LambdaQueryWrapper<BookMarkType>().in(BookMarkType::getId,ids));
    }

    /**
     * 判断名称是否已存在
     * @param type
     * @return
     */
    @Override
    public Boolean saveBookMarkType(BookMarkType type) {
        String id = StringUtil.randomUUID();
        type.setId(id);
        BookMarkType visualBookMarkType = this.queryBookMarkTypeByName(type.getName());
        if (ObjectUtil.isNotEmpty(visualBookMarkType)){
            BaseException.throwException(ResultCode.get("bookmark_type_exit"));
        }
        Boolean tempRs = roleSourceService.saveRoleSource(id, SourceType.BOOKMARK_TYPE);
        return bookMarkTypeMapper.insert(type) > 0 && tempRs;
    }

    @Override
    public Boolean delBookMarkType(String id) {
        LambdaQueryWrapper<BookMark> queryWrapper = new LambdaQueryWrapper<BookMark>().eq(BookMark::getTypeId, id);
        List<BookMark> visualBookMarks = bookMarkMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(visualBookMarks)){
            BaseException.throwException(ResultCode.get("there_are_bookmarks_under_the_type"));
        }
        return bookMarkTypeMapper.deleteById(id) > 0;
    }

    @Override
    public Boolean updateBookMarkType(BookMarkType type) {
        BookMarkType visualBookMarkType = this.queryBookMarkTypeByName(type.getName());
        if (ObjectUtil.isNotEmpty(visualBookMarkType)){
            BaseException.throwException(ResultCode.get("type_name_exits"));
        }
        return bookMarkTypeMapper.updateById(type) > 0;
    }
}
