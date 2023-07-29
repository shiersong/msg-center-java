package org.msg.msgcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 消息中心
 * @author Scott Soong
 */
@SpringBootApplication
public class MsgCenterApp {
    public static void main(String[] args) {
        SpringApplication.run(MsgCenterApp.class, args);
        System.out.println("消息中心启动成功");
    }
}