package com.stdc.visual.controller.oss;

import com.stdc.core.tool.utils.StringPool;
import com.stdc.visual.common.utils.HttpServletUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

import static com.stdc.visual.service.IVisualOssFileService.CachePath;

/**
 * @author: wang_jie
 * @data: 2022/12/6--16:57
 * @describe: 路由经过视图返回 username_font.css 文件
 */
@Controller
public class FontCssController {

    @ApiIgnore
    @RequestMapping("/css/{username}/font.css")
    public String getFontCss(@PathVariable("username")String username){
        HttpServletUtil.getCurrentResp().setHeader("content-type","text/css; charset=utf-8");
        return String.format(CachePath,username);
    }

}
