package com.xiafei.tools.spring.springboot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.springframework.web.servlet.view.velocity.VelocityLayoutViewResolver;

import java.util.Properties;

/**
 * <P>Description: 配置velocity. </P>
 * <P>CALLED BY:   齐霞飞 </P>
 * <P>UPDATE BY:   齐霞飞 </P>
 * <P>CREATE DATE: 2017/10/22</P>
 * <P>UPDATE DATE: 2017/10/22</P>
 *
 * @author 齐霞飞
 * @version 1.0
 * @since java 1.7.0
 */
@Configuration
public class VelocityConfig {

    @Bean
    public VelocityLayoutViewResolver velocityViewResolver() {
        VelocityLayoutViewResolver bean = new VelocityLayoutViewResolver();
        bean.setLayoutUrl("layout/default.vm");
        bean.setCache(false);
        bean.setSuffix(".vm");
        bean.setExposeSpringMacroHelpers(true);
        bean.setRequestContextAttribute("rc");
        bean.setDateToolAttribute("dateTool");
        bean.setNumberToolAttribute("numberTool");
        bean.setContentType("text/html;charset=UTF-8");
        bean.setToolboxConfigLocation("/WEB-INF/toolbox.xml");
        return bean;
    }

    @Bean
    public VelocityConfigurer velocityConfigurer() {
        VelocityConfigurer bean = new VelocityConfigurer();
        bean.setResourceLoaderPath("/WEB-INF/vm/");
        Properties properties = new Properties();
        properties.setProperty("input.encoding", "UTF-8");
        properties.setProperty("output.encoding", "UTF-8");
        properties.setProperty("contentType", "text/html;charset=UTF-8");
        bean.setVelocityProperties(properties);
        return bean;
    }

}
