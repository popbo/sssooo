package com.stdc.visual.util;

import com.stdc.core.tool.utils.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2024/5/20--17:11
 * @describe:
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageData<T> {


    /**
     * 元素
     */
    private List<T> records;

    /**
     * 总数
     */
    private int total;

    /**
     * 当前第几页
     */
    private int current;

    /**
     * 每一页个数
     */
    private int size;

    /**
     * 总页数
     */
    private int pages;


    /**
     * 获取分页对象
     * @param data
     * @param pageNumber
     * @param pageSize
     * @return
     */
    public static PageData getPagedData(List data, int pageNumber, int pageSize) {
        pageSize = ObjectUtil.isEmpty(pageSize) ? 10 : pageSize;
        pageNumber = ObjectUtil.isEmpty(pageNumber)  ?  1 : pageNumber;
        PageData pageData = new PageData();
        try {
            pageData.setTotal(data.size());
            if ((data.size()%pageSize) > 0){
                pageData.setPages((data.size()/pageSize)+1);
            }else {
                pageData.setPages(data.size()/pageSize);
            }
            pageSize = pageSize < data.size() ? pageSize :data.size();
            pageData.setSize(pageSize);
            pageData.setCurrent(pageNumber);
            int start = (pageNumber - 1) * pageSize;
            int end = Math.min(start + pageSize, data.size());
            pageData.setRecords(data.subList(start, end));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return null;
        }
        return pageData;
    }

}
