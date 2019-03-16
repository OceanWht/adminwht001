package com.wht.admin.demo.test;

import com.wht.admin.demo.pojo.SysConfig;
import com.wht.admin.demo.service.SysService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class TestController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SysService sysService;

    @RequestMapping("/xslsheet")
    @ResponseBody
    public ModelMap xslsheet() {
        logger.trace("I am trace log.");
        logger.debug("I am debug log.");
        logger.warn("I am warn log.");
        logger.error("I am error log.");

        SysConfig sysConfig = sysService.getConfigById(1);
        logger.info("sysConfig :" + sysConfig);
        ModelMap modelMap = new ModelMap();
        modelMap.put("sysConfig", sysConfig);
        return modelMap;
    }

    @RequestMapping("/")
    public String index() {
        return "/index.html";//这里要加"/"，网上说不加/ 不知道为啥
    }

    @RequestMapping("/login")
    public String login() {
        return "/index.html";
    }


}
