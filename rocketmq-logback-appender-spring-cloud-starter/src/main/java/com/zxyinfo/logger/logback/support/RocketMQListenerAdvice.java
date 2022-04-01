package com.zxyinfo.logger.logback.support;

import com.zxyinfo.logger.logback.util.ThrowableUtils;
import java.lang.reflect.Method;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;

/**
 * @author joewee
 * @version 1.0.0
 */
public class RocketMQListenerAdvice implements ThrowsAdvice, Ordered {
  private static final Logger log = LoggerFactory.getLogger(RocketMQListenerAdvice.class);

  private int maxReconsumeTimes = 16;
  private final Class<?> consumerClass;
  private final String consumerGroup;
  public RocketMQListenerAdvice (int maxReconsumeTimes,Class<?> clazz,String consumerGroup){
    if(maxReconsumeTimes>-1){
      this.maxReconsumeTimes = maxReconsumeTimes;
    }
    this.consumerClass = clazz;
    this.consumerGroup = consumerGroup;
  }
  public void afterThrowing(@NonNull Method method, Object[] args, Object target, Exception ex){
    if(args.length>0){
      final Object argument = args[0];
      if (argument instanceof MessageExt) {
        MessageExt messageExt = (MessageExt) argument;
        final int reconsumeTimes = messageExt.getReconsumeTimes();
        if(reconsumeTimes>=maxReconsumeTimes){
          final Throwable rootCause = ThrowableUtils.findRootCause(ex);
          final String keys = messageExt.getKeys();
          final String tags = messageExt.getTags();
          log.error("消费组{}消费消息失败. messageId:{},key:{},tags:{}, topic:{}, reconsumeTimes:{}",
              consumerGroup,messageExt.getMsgId(), keys,tags,messageExt.getTopic(),
              messageExt.getReconsumeTimes(),rootCause);
        }
      }
    }
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
