package kr.engsoft.autodesk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;


@Configuration
public class WebConfig implements WebMvcConfigurer, CommandLineRunner {

    @Value("${uploadPath}")
    private String uploadPath;

    @Override
    public void run(String... args) {
        File file = new File(uploadPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:///D:/upload/");
    }

}
