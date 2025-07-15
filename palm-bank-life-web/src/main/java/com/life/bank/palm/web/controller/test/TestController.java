//package com.life.bank.palm.web.controller.test;
//
//import com.life.bank.palm.common.result.CommonResponse;
//import com.life.bank.palm.dao.user.mapper.UserMapper;
//import com.life.bank.palm.dao.user.pojo.UserPO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/test")
//public class TestController {
//
//    @Autowired
//    private UserMapper userMapper;
//
//    @GetMapping("/db")
//    public CommonResponse<String> testDb() {
//        UserPO user = userMapper.selectOneByPhoneAndIsDelete("13800138000", 0);
//        if (user == null) {
//            return CommonResponse.buildSuccess("数据库连接成功，暂无数据");
//        }
//        return CommonResponse.buildSuccess("找到用户：" + user.getNickname());
//    }
//}

//package com.life.bank.palm.web.controller.test;
//
//import com.life.bank.palm.common.result.CommonResponse;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/test")
//public class TestController {
//
//    @GetMapping("/hello")
//    public CommonResponse<String> hello() {
//        return CommonResponse.buildSuccess("Hello, Palm Bank!");
//    }
//}

package com.life.bank.palm.web.controller.test;

import com.life.bank.palm.common.result.CommonResponse;
import com.life.bank.palm.dao.user.mapper.UserMapper;
import com.life.bank.palm.dao.user.pojo.UserPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired(required = false)  // 添加 required = false 防止注入失败导致启动失败
    private UserMapper userMapper;

    /**
     * 简单的测试接口，不依赖数据库
     */
    @GetMapping("/hello")
    public CommonResponse<String> hello() {
        return CommonResponse.buildSuccess("Hello, Palm Bank!");
    }

    /**
     * 测试数据库连接
     */
    @GetMapping("/db")
    public CommonResponse<String> testDb() {
        try {
            System.out.println("开始测试数据库连接...");

            if (userMapper == null) {
                System.out.println("userMapper 为空！");
                return CommonResponse.buildError("UserMapper 注入失败");
            }

            System.out.println("userMapper 注入成功，开始查询数据库...");

            UserPO user = userMapper.selectOneByPhoneAndIsDelete("13800138000", 0);

            if (user == null) {
                System.out.println("查询成功，但没有找到数据");
                return CommonResponse.buildSuccess("数据库连接成功，暂无数据");
            }

            System.out.println("查询成功，找到用户：" + user.getNickname());
            return CommonResponse.buildSuccess("找到用户：" + user.getNickname());

        } catch (Exception e) {
            System.err.println("数据库查询出错：");
            e.printStackTrace();
            return CommonResponse.buildError("数据库查询失败：" + e.getMessage());
        }
    }

    /**
     * 测试项目配置信息
     */
    @GetMapping("/info")
    public CommonResponse<String> info() {
        StringBuilder info = new StringBuilder();
        info.append("项目运行正常！\n");
        info.append("UserMapper 注入状态：").append(userMapper != null ? "成功" : "失败");
        return CommonResponse.buildSuccess(info.toString());
    }
}