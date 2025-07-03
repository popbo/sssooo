package com.stdc.visual.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.stdc.core.tool.utils.DateUtil;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringUtil;
import com.stdc.visual.entity.po.EventLogPO;
import com.stdc.visual.entity.po.Visual;
import com.stdc.visual.mapper.EventLogMapper;
import com.stdc.visual.mapper.VisualMapper;
import com.stdc.visual.service.EventLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author: wang_jie
 * @data: 2024/1/4--15:32
 * @describe: 组件日志详情操作
 */
@Service
public class EventLogServiceImpl implements EventLogService {

    @Autowired
    private EventLogMapper eventLogMapper;

    @Autowired
    private VisualMapper visualMapper;

    @Override
    public Boolean addLog(EventLogPO eventLog) {
        //设置创建时间,格式化的字符串(yyyy-MM-dd HH:mm:ss)
        String updateTime = DateUtil.formatDateTime(new Date());
        eventLog.setCreateTime(updateTime);
        return eventLogMapper.insert(eventLog) > 0;
    }

    @Override
    public Boolean delLog(Integer id) {
        return eventLogMapper.deleteById(id) > 0;
    }

    @Override
    public Boolean updateLogById(EventLogPO eventLog) {
        return eventLogMapper.updateById(eventLog) > 0;
    }

    @Override
    public List<EventLogPO> queryEventLogs(EventLogPO eventLog) {
        LambdaQueryWrapper<EventLogPO> queryWrapper = new LambdaQueryWrapper<>();
        //主键id
        if ( ObjectUtil.isNotEmpty(eventLog.getId()) && eventLog.getId() > 0){
            queryWrapper.eq(EventLogPO::getId,eventLog.getId());
        }
        //大屏id
        if ( ObjectUtil.isNotEmpty(eventLog.getVisualId()) && eventLog.getVisualId() > 0){
            queryWrapper.eq(EventLogPO::getVisualId,eventLog.getVisualId());
        }
        //页面id
        if ( ObjectUtil.isNotEmpty(eventLog.getConfigId()) && eventLog.getConfigId() > 0){
            queryWrapper.eq(EventLogPO::getConfigId,eventLog.getConfigId());
        }
        //组件id
        if (StringUtil.hasText(eventLog.getComponentId())){
            queryWrapper.eq(EventLogPO::getComponentId,eventLog.getComponentId());
        }
        //事件id
        if ( StringUtil.hasText(eventLog.getEventId())){
            queryWrapper.eq(EventLogPO::getEventId,eventLog.getEventId());
        }
        List<EventLogPO> eventLogPOS = eventLogMapper.selectList(queryWrapper);
        eventLogPOS.forEach(eventLogPO -> {
            Visual visual = visualMapper.selectById(eventLogPO.getVisualId());
            eventLogPO.setVisualName(visual.getTitle());
        });
        return eventLogPOS;
    }

    @Override
    public EventLogPO queryOneById(Integer id) {
        EventLogPO eventLogPO = eventLogMapper.selectById(id);
        Visual visual = visualMapper.selectById(eventLogPO.getVisualId());
        eventLogPO.setVisualName(visual.getTitle());
        return eventLogPO;
    }
}
