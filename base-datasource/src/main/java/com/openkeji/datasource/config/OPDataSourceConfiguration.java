package com.openkeji.datasource.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

/**
 * @program: base
 * @description:
 * @author: kyle.hou
 * @create: 2023-10-08-10
 */
@Slf4j
@Configuration
@EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
@Import({OPDruidDataSourceConfiguration.class, MybatisPlusAutoConfiguration.class})
public class OPDataSourceConfiguration {

    @PostConstruct
    public void init() {
        log.info("[OPDataSourceConfiguration.init] init");
    }
}
