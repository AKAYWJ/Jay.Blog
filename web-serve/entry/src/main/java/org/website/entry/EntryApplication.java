package org.website.entry;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "org.website")
@MapperScan("org.website.dao.mapper")
public class EntryApplication {

    public static void main(String[] args) {

        SpringApplication.run(EntryApplication.class, args);
        System.out.println("+++++++++++++++运行成功++++++++++++++++");
    }

}
