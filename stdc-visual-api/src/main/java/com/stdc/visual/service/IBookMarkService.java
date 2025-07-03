package com.stdc.visual.service;

import com.stdc.visual.entity.po.BookMark;
import com.stdc.visual.entity.po.BookMarkType;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2022/6/23--15:00
 * @describe: 大屏收藏
 */
public interface IBookMarkService {

    /***
     * 通过name获取收藏详情（name为唯一值）
     * @return
     */
    List<BookMark> queryBookMarkByName(String name);


    /**
     * 通过id获取收藏详情（id为唯一值）
     * @param id
     * @return
     */
    BookMark queryBookMarkById(String id);

    /**
     * 通typeId获取收藏详情（id为唯一值）
     * @param id
     * @return
     */
    List<BookMark> queryVisualBookMarkByType(String id);

    /***
     * 获取所有收藏详情
     * @return
     */
    List<BookMark> queryAllBookMark();

    /**
     * 更新收藏
     * @param BookMark
     * @return
     */
    Boolean updateBookMarkById(BookMark BookMark);

    /**
     * 删除收藏
     * @param id
     * @return
     */
    Boolean delBookMarkById(String id);

    /**
     * 新增收藏
     * @return
     */
    Boolean saveWithHandlerDetail(BookMark BookMark);


    /**
     *  通过id获取收藏分类详情
     * @param id
     * @return
     */
    BookMarkType queryBookMarkTypeById(String id);

    /**
     *  通过name获取收藏分类详情
     * @param name
     * @return
     */
    BookMarkType queryBookMarkTypeByName(String name);

    /**
     * 查询所有收藏分类
     * @return
     */
    List<BookMarkType> queryAllBookMarkTypes();

    /**
     * 新增收藏分类
     * @return
     */
    Boolean saveBookMarkType(BookMarkType type);


    /**
     * 删除收藏分类
     */
    Boolean delBookMarkType(String id);

    /**
     * 修改收藏分类名称
     * @param type
     * @return
     */
    Boolean updateBookMarkType(BookMarkType type);
}
