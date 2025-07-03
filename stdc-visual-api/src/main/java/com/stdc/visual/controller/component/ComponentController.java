package com.stdc.visual.controller.component;

import com.alibaba.druid.util.StringUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.log.exception.pojo.BaseException;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.entity.result.ResultCode;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.visual.common.utils.BeanUtils;
import com.stdc.visual.dynamic.base.dataset.dto.component.VisualComponentType;
import com.stdc.visual.dynamic.base.dataset.po.DatasetTable;
import com.stdc.visual.dynamic.base.dataset.request.DataSetTableRequest;
import com.stdc.visual.dynamic.service.DataSetTableService;
import com.stdc.visual.service.comment.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: wang_jie
 * @data: 2022/5/23--15:59
 * @describe: 饼状图数据刷新
 */
@RestController
@RequestMapping("/comment")
@Api(tags = "大屏：组件数据刷新")
@ApiSupport(author = "wangjie",order = 2)
public class ComponentController {

    @Autowired
    private DataSetTableService dataSetTableService;

    @Autowired
    private IVisualKeyValueService iVisualKeyValueService;

    @Autowired
    private IVisualBarService iVisualBarService;

    @Autowired
    private IVisualValueService iVisualValueService;

    @Autowired
    private IVisualRadarService iVisualRadarService;

    @Autowired
    private IVisualTableService iVisualTableService;

    @Autowired
    private IVisualCrossTableService iVisualCrossTableService;

    @Autowired
    private IVisualTreeService iVisualTreeService;

    @Autowired
    private IVisualSelectService iVisualSelectService;

    @Autowired
    private IVisualEventService iVisualEventService;

    @Autowired
    private IVisualScatterService iVisualScatterService;
    
    /***
     * 通过选择相应的维度、度量、图例刷新组件数据
     * @param dataSetTableRequest
     * @param type 组件类型
     * @return 相应type组件刷新需要的不同格式数据
     */
    @ApiOperationSupport(order = 1)
    @PostMapping("/db/refresh/{type}")
    @ApiOperation(value = "数据库数据源:通过选择相应的维度、度量、图例刷新组件数据", notes = "相应type组件刷新需要的不同格式数据")
    public R refresh(@ApiParam(value = "组件刷新请求", required = true) @RequestBody DataSetTableRequest dataSetTableRequest, @PathVariable String type) throws Exception{
        if(StringUtils.isEmpty(dataSetTableRequest.getId())){
            BaseException.throwException(ResultCode.get("data_set_id_empty"));
        }
        String dataSetTableId = dataSetTableRequest.getId();
        //获取数据集信息
        DatasetTable datasetTable = dataSetTableService.get(dataSetTableId);
        if (ObjectUtil.isEmpty(datasetTable)){
            BaseException.throwException(ResultCode.get("get_dataset_info_error"));
        }
        BeanUtils.copyBean(dataSetTableRequest,datasetTable);
        switch (type){
            case VisualComponentType.PIE_CHART://饼状图
            case VisualComponentType.STEREOSCOPIC_PIE://3D饼状图
            case VisualComponentType.FUNNEL_CHART://漏斗图
            case VisualComponentType.PICTOGRAM://象形图
            case VisualComponentType.CHAR://字符云
                return R.data(iVisualKeyValueService.refresh(dataSetTableRequest));
            case VisualComponentType.SCATTER_CHART://散点图
                return R.data(iVisualScatterService.refresh(dataSetTableRequest));
            case VisualComponentType.BAR_CHART://柱状图
            case VisualComponentType.newPictorialBar://斑马柱状图
            case VisualComponentType.LINE_CHART://折线图<>
            case VisualComponentType.COMMON://自定义组件
            case VisualComponentType.STEREOSCOPIC_BAR://立体柱状图
            case VisualComponentType.PEAK_BAR://山峰柱状图
                return R.data(iVisualBarService.refresh(dataSetTableRequest));
            case VisualComponentType.LINE_BAR://柱状折线图
            case VisualComponentType.BASIC_AREA://预测面积图
                return R.data(iVisualBarService.refreshLineBar(dataSetTableRequest));
            case VisualComponentType.BUTTON://按钮
            case VisualComponentType.TEXT://文本框
            case VisualComponentType.IFRAME://iframe标签
                return R.data(iVisualValueService.refreshForListByMeasureFieldAndDimensionField(dataSetTableRequest));
            case VisualComponentType.GAUGE://仪表盘
            case VisualComponentType.PROGRESS_VIEW://进度条-环形
            case VisualComponentType.PROGRESS_BAR://进度条-条形
                return R.data(iVisualValueService.refreshForOneByMeasureField(dataSetTableRequest));
            case VisualComponentType.TABLE://表格
                return R.data(iVisualTableService.refreshByTableWithTableHead(dataSetTableRequest));
            case VisualComponentType.DATA_STORAGE://数据源组件
                return R.data(iVisualTableService.refreshByTable(dataSetTableRequest));
            case VisualComponentType.MULTIPLE_TABS://多选卡
            case VisualComponentType.SWIPER://轮播图
                return R.data(iVisualValueService.refreshForListByMeasureField(dataSetTableRequest));
            case VisualComponentType.TREE://层级树
            case VisualComponentType.NEW_TREE://新层级树
                return R.data(iVisualTreeService.refreshByTree(dataSetTableRequest));
            case VisualComponentType.RADAR_CHART://雷达图
                return R.data(iVisualRadarService.refreshByRadar(dataSetTableRequest));
            case VisualComponentType.CROSS_TABLE://交叉表
                return R.data(iVisualCrossTableService.refreshByCrossTable(dataSetTableRequest));
            case VisualComponentType.SELECT://下拉框
                return R.data(iVisualSelectService.refreshWithDimensionField(dataSetTableRequest));
            case VisualComponentType.TABS://选项卡
            case VisualComponentType.SCROLL_RANKING://滚动排行
            case VisualComponentType.UNIVERSAL_TABS://TABS
                return R.data(iVisualSelectService.refreshWithDimensionFieldAndMeasureField(dataSetTableRequest));
            case VisualComponentType.ZEBRA_PROGRESS_BAR://斑马进度条
                return R.data(iVisualSelectService.refreshByOne(dataSetTableRequest));
            case VisualComponentType.EVENT_RANKING_LIST://事件排行组件
                return R.data(iVisualEventService.refresh(dataSetTableRequest));
            default:
                return R.fail(ResultCode.get("fresh_data_error"));
        }
    }

}
