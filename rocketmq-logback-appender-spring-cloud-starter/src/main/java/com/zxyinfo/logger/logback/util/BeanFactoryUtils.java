package com.zxyinfo.logger.logback.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * @author joewee
 * @version 1.0.0
 * @date 2021/10/29 10:39
 */
public class BeanFactoryUtils implements BeanFactoryPostProcessor {
  private static ConfigurableListableBeanFactory beanFactory;
  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    BeanFactoryUtils.beanFactory = beanFactory;
  }
  /**
   * 获取对象
   *
   * @param name 名称
   * @return Object 一个以所给名字注册的bean的实例
   * @throws NoSuchBeanDefinitionException if no bean of the given type was found
   * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
   * @throws BeansException if the bean could not be created
   *
   */
  @SuppressWarnings("unchecked")
  public static <T> T getBean(String name) throws BeansException
  {
    if(beanFactory==null){
      return null;
    }
    return (T) beanFactory.getBean(name);
  }
  /**
   * 获取类型为requiredType的对象
   *
   * @param clazz 类型
   * @return bean
   * @throws NoSuchBeanDefinitionException if no bean of the given type was found
   * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
   * @throws BeansException if the bean could not be created
   *
   */
  public static <T> T getBean(Class<T> clazz) throws BeansException
  {
    if(beanFactory==null){
      return null;
    }
    return beanFactory.getBean(clazz);
  }
}
