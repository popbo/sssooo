package com.stdc.visual.controller.visual;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.stdc.core.tool.entity.result.R;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.visual.common.utils.BeanUtils;
import com.stdc.visual.entity.po.BookMark;
import com.stdc.visual.entity.po.BookMarkType;
import com.stdc.visual.entity.po.BookMarkTypeLess;
import com.stdc.visual.service.IBookMarkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: wang_jie
 * @data: 2022/6/23--14:59
 * @describe: 大屏收藏
 */
@RestController
@AllArgsConstructor
@RequestMapping("/visual")
@ApiSupport(author = "wangjie",order = 11)
@Api(tags = "收藏:大屏收藏")
public class BookMarkController {

    private final IBookMarkService bookMarkService;

    /**
     * 获取收藏文件
     */
    @GetMapping("/get/bookmark/{name}")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "根据组件名称获取收藏组件详情")
    public R getBookMark(@PathVariable("name") String name){
        List<BookMark> bookMarks = bookMarkService.queryBookMarkByName(name);
        return R.data(bookMarks);
    }

    /**
     * 通过类型获取收藏文件
     */
    @GetMapping("/get/bookmarkbytype/{type}")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "根据类型获取收藏组件详情")
    public R getVisualBookMarkByType(@PathVariable("type") String type){
        List<BookMark> visualBookMarks = bookMarkService.queryVisualBookMarkByType(type);
        return R.data(visualBookMarks);
    }

    /**
     * 获取所有收藏文件详情
     */
    @GetMapping("/get/bookmark")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "获取所有收藏组件列表")
    public R getBookMark(){
        //获取所有收藏组件
        return R.data(bookMarkService.queryAllBookMark());
    }

    /**
     * 上传收藏文件
     */
    @PostMapping("/save/bookmark")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "上传收藏文件")
    public R addBookMark(@ApiParam(value = "收藏文件信息", required = true) @RequestBody BookMark bookMark){
        List<BookMark> isExit = bookMarkService.queryBookMarkByName(bookMark.getName());
        if (CollectionUtils.isNotEmpty(isExit)){
            return R.fail("名称已存在");
        }
        Boolean save = bookMarkService.saveWithHandlerDetail(bookMark);
        if(save){
            return R.data("收藏成功");
        }else{
            return R.fail("收藏失败");
        }
    }

    /**
     * 修改收藏文件
     */
    @PostMapping("/edit/bookmark")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改收藏文件:修改文件时,需要收藏信息id")
    public R delBookMark(@ApiParam(value = "收藏文件信息", required = true) @RequestBody BookMark bookMark){
        List<BookMark> isExit = bookMarkService.queryBookMarkByName(bookMark.getName());
        BookMark bookMarkByIdId = bookMarkService.queryBookMarkById(bookMark.getId());
        if (ObjectUtil.isEmpty(bookMarkByIdId)){
            return R.fail("收藏文件不存在");
        }
        isExit.remove(bookMarkByIdId);
        if (CollectionUtils.isNotEmpty(isExit)){
            return R.fail("名称已存在");
        }
        Boolean update = bookMarkService.updateBookMarkById(bookMark);
        if(update){
            return R.data("修改成功");
        }else{
            return R.fail("修改失败");
        }
    }

    /**
     * 删除收藏文件
     */
    @PostMapping("/del/bookmark")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "删除收藏文件")
    public R editBookMark(@ApiParam(value = "收藏文件信息", required = true) @RequestBody BookMark bookMark){
        Boolean del = bookMarkService.delBookMarkById(bookMark.getId());
        if(del){
            return R.data("删除成功");
        }else{
            return R.fail("删除失败");
        }
    }

    /**
     * 获取收藏文件所有分类
     */
    @GetMapping("/get/bookmarktype/{isLess}")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "获取收藏文件所有分类")
    public R getVisualBookMarkTypeS(@ApiParam(value = "名称是否缩略处理", required = true) @PathVariable("isLess") Boolean isLess){
        List<BookMarkType> bookMarkTypes = bookMarkService.queryAllBookMarkTypes();
        List<BookMarkTypeLess> bookMarkTypesLess = new ArrayList<>();
        if (isLess){
            bookMarkTypes.forEach(type -> {
                BookMarkTypeLess less = new BookMarkTypeLess();
                BeanUtils.copyBean(less,type);
                String lessName = null;
                if (type.getName().length() > 3){
                    lessName = type.getName().substring(0,3) + "...";
                }else {
                    lessName = type.getName();
                }
                less.setLessName(lessName);
                bookMarkTypesLess.add(less);
            });
        }else {
            bookMarkTypes.forEach(type -> {
                BookMarkTypeLess less = new BookMarkTypeLess();
                BeanUtils.copyBean(less,type);
                less.setLessName(type.getName());
                bookMarkTypesLess.add(less);
            });
        }
        return R.data(bookMarkTypesLess);
    }

    /**
     * 新增收藏文件分类
     */
    @PostMapping("/save/bookmarktype")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "新增收藏文件分类")
    public R addVisualBookMarkType(@ApiParam(value = "收藏文件分类信息", required = true) @RequestBody BookMarkType type){
        Boolean aBoolean = bookMarkService.saveBookMarkType(type);
        if (aBoolean){
            return R.success("新增成功");
        }else {
            return R.success("新增失败");
        }
    }

    /**
     * 修改收藏文件分类
     */
    @PostMapping("/edit/bookmarktype")
    @ApiOperationSupport(order = 9)
    @ApiOperation(value = "修改收藏文件分类")
    public R delVisualBookMarkType(@ApiParam(value = "收藏文件分类信息", required = true) @RequestBody BookMarkType type){
        Boolean aBoolean = bookMarkService.updateBookMarkType(type);
        if (aBoolean){
            return R.success("修改成功");
        }else {
            return R.success("修改失败");
        }
    }

    /**
     * 删除收藏文件分类
     */
    @PostMapping("/del/bookmarktype/{id}")
    @ApiOperationSupport(order = 10)
    @ApiOperation(value = "删除收藏文件分类")
    public R editVisualBookMarkType(@ApiParam(value = "收藏文件分类id", required = true) @PathVariable("id") String id){
        Boolean del = bookMarkService.delBookMarkType(id);
        if(del){
            return R.data("删除成功");
        }else{
            return R.fail("删除失败");
        }
    }

}
