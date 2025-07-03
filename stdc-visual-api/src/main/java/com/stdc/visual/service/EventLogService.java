package com.stdc.visual.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.stdc.visual.entity.po.EventLogPO;

import java.util.List;

/**
 * @author: wang_jie
 * @data: 2024/1/4--15:32
 * @describe: 组件日志详情操作
 */
public interface EventLogService {

    /**
     * 新增组件日志详情
     * @return
     */
    Boolean addLog(EventLogPO eventLog);

    /**
     * 删除组件日志详情
     * @return
     */
    Boolean delLog(Integer id);

    /**
     * 新增组件日志详情
     * @return
     */
    Boolean updateLogById(EventLogPO eventLog);


    /**
     * 查询日志详情(列表)
     * @return
     */
    List<EventLogPO> queryEventLogs(EventLogPO eventLog);

    /**
     * 查询日志详情
     * @return
     */
    EventLogPO queryOneById(Integer id);
}
