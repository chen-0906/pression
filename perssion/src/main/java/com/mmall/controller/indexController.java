package com.mmall.controller;

import com.mmall.common.JsonData;
import com.mmall.param.TestVo;
import com.mmall.util.BeanValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author BaiYe
 * @create 2020-09-23 20:58
 */
@Controller
@RequestMapping("/test")
@Slf4j
public class indexController {

    @RequestMapping("/hello.json")
    @ResponseBody
    public JsonData hello(){
        log.info("hello");
        return JsonData.success("hello, permission");
    }

    @RequestMapping("/validate.json")
    @ResponseBody
    public JsonData validate(TestVo vo){
        log.info("validate");
        try {
            Map<String, String> map = BeanValidator.validateObject(vo);
            if (map!=null && map.entrySet().size() > 0){
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    log.info("{}->{}", entry.getKey(), entry.getValue());
                }
            }
        }catch (Exception e){

        }
        return JsonData.success("test, validate");
    }
}
