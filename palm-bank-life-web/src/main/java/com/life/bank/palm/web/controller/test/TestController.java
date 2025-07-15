package com.life.bank.palm.web.controller.test;

import com.life.bank.palm.common.result.CommonResponse;
import com.life.bank.palm.dao.user.mapper.UserMapper;
import com.life.bank.palm.dao.user.pojo.UserPO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "测试接口")
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired(required = false)
    private UserMapper userMapper;

    @ApiOperation("测试接口连通性")
    @GetMapping("/hello")
    public CommonResponse<String> hello() {
        return CommonResponse.buildSuccess("Hello, Palm Bank!");
    }

    @ApiOperation("测试数据库连接")
    @GetMapping("/db")
    public CommonResponse<String> testDb() {
        try {
            System.out.println("开始测试数据库连接...");

            if (userMapper == null) {
                System.out.println("userMapper 为空！");
                return CommonResponse.buildError("UserMapper 注入失败");
            }

            UserPO user = userMapper.selectOneByPhoneAndIsDelete("13800138000", 0);

            if (user == null) {
                return CommonResponse.buildSuccess("数据库连接成功，暂无数据");
            }

            return CommonResponse.buildSuccess("找到用户：" + user.getNickname());

        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponse.buildError("数据库查询失败：" + e.getMessage());
        }
    }
}