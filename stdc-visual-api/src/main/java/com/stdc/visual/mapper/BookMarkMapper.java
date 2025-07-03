package com.stdc.visual.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stdc.visual.entity.po.BookMark;

/**
 * @author: wang_jie
 * @data: 2022/6/21--10:12
 * @describe:
 */
public interface BookMarkMapper extends BaseMapper<BookMark> {

	Boolean saveWithHandlerDetail(BookMark BookMark);

	String queryDetailById(String id);
}
