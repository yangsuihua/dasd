package com.videoplatform.interaction;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.videoplatform.interaction.mapper")
public class InteractionApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(InteractionApplication.class, args);
        System.out.println("========== 互动服务启动成功 ==========");
    }
}
