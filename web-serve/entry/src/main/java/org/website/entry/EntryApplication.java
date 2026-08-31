package org.website.entry;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@SpringBootApplication
@ComponentScan(value = "org.website")
@MapperScan("org.website.dao.mapper")
public class EntryApplication {

    private static final Logger log = LoggerFactory.getLogger(EntryApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EntryApplication.class, args);
        log.info("+++++++++++++++运行成功++++++++++++++++");
    }

}
