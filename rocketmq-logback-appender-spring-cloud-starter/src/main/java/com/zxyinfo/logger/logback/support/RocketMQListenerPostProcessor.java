package com.zxyinfo.logger.logback.support;

import java.lang.annotation.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

/**
 * @author joewee
 * @version 1.0.0
 */
public class RocketMQListenerPostProcessor implements BeanPostProcessor, Ordered {

  private static final Logger log = LoggerFactory.getLogger(RocketMQListenerPostProcessor.class);
  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
    final Annotation[] annotations = clazz.getAnnotations();
    for (Annotation annotation : annotations) {
      if (isRocketMQMessageListenerAnnotation(annotation)) {
        log.info("post process {} bean of {}  ",bean.getClass().getName(),beanName);
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        // 配置增强类advisor
        pointcut.setExpression("execution(* org.apache.rocketmq.spring.core.RocketMQListener.onMessage(*))");
        int maxReconsumeTimes = -1;
        final Object max = getValueFromAnnotation(annotation, "maxReconsumeTimes", -1);
        if (max instanceof Integer){
          maxReconsumeTimes = (int) max;
        }
        final Object consumerGroup = getValueFromAnnotation(annotation, "consumerGroup","unknown");
        final RocketMQListenerAdvice advice = new RocketMQListenerAdvice(maxReconsumeTimes,clazz, consumerGroup.toString());
        final DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(bean);
        factory.addAdvisor(advisor);
        return factory.getProxy();
      }
  }
    return bean;
  }
  private  boolean isRocketMQMessageListenerAnnotation(Annotation annotation){
    return "org.apache.rocketmq.spring.annotation.RocketMQMessageListener".equals(
        annotation.annotationType().getName());
  }
  private Object getValueFromAnnotation(Annotation annotation,String method,Object defaultValue){
    Assert.notNull(method,"method不能为空");
    Assert.notNull(annotation,"annotation不能为空");
    try {
      final Object value = annotation.annotationType().getMethod(method).invoke(annotation);
      if(value!=null){
        return value;
      }
    } catch (Exception e) {
      log.error(e.getMessage(),e);
    }
    return defaultValue;
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
