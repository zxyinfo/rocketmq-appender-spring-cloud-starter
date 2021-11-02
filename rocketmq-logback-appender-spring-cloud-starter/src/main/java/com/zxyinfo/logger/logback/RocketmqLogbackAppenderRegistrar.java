package com.zxyinfo.logger.logback;

import com.zxyinfo.logger.logback.support.DefaultLoggerConfigServiceImpl;
import com.zxyinfo.logger.logback.util.BeanFactoryUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;


/**
 * @author joewee
 * @version 1.0.0
 * @date 2021/10/29 11:10
 */

public class RocketmqLogbackAppenderRegistrar implements ImportBeanDefinitionRegistrar {
  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
      BeanDefinitionRegistry registry) {
    final AnnotationAttributes attributes = AnnotationAttributes.fromMap(
        importingClassMetadata.getAnnotationAttributes(
            EnableRocketmqLogbackAppender.class.getName(), false));
    if(attributes==null){
      throw new BeanCreationException("请为@EnableRocketmqLogbackAppender设置相关的属性");
    }
    final String nameServerAddress = attributes.getString("nameServerAddress");
    final String producerGroup = attributes.getString("producerGroup");
    final String tag = attributes.getString("tag");
    final String topic = attributes.getString("topic");
    final String pattern = attributes.getString("pattern");
    final boolean includeCallerData = attributes.getBoolean("includeCallerData");
    Assert.hasText(nameServerAddress,"@EnableRocketmqLogbackAppender的nameServerAddress不能为空");
    Assert.hasText(producerGroup,"@EnableRocketmqLogbackAppender的producerGroup不能为空");
    final AbstractBeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition(
            RocketmqLogbackAppenderRefresher.class)
        .addConstructorArgValue(nameServerAddress)
        .addConstructorArgValue(producerGroup)
        .addConstructorArgValue(tag)
        .addConstructorArgValue(topic)
        .addConstructorArgValue(pattern)
        .addConstructorArgValue(includeCallerData)
        .setInitMethodName("refresh")
        .getBeanDefinition();
    registry.registerBeanDefinition("rocketmqLoggerAppenderRefresher",definition);
    final AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(
        BeanFactoryUtils.class).getBeanDefinition();
    registry.registerBeanDefinition("beanFactoryUtils",beanDefinition);
    registry.registerBeanDefinition("loggerConfigService",
        BeanDefinitionBuilder.genericBeanDefinition(DefaultLoggerConfigServiceImpl.class).getBeanDefinition());

  }
}
