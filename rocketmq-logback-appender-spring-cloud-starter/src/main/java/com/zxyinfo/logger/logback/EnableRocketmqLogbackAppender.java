package com.zxyinfo.logger.logback;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * @author joewee
 * @version 1.0.0
 * @date 2021/10/29 11:13
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RocketmqLogbackAppenderRegistrar.class})
public @interface EnableRocketmqLogbackAppender {
  String nameServerAddress() default "";
  String topic() default "alert_topic";
  String tag() default "";

  String producerGroup() default "";

  /**
   * 日志格式，默认 json格式
   * eg:
   * {
   *                 "date":"%d",
   *                 "level": "%level",
   *                 "logger": "%logger{40}",
   *                 "thread": "%t",
   *                 "location": "%L",
   *                 "message": "%message",
   *                 "ip":"%ip",
   *                 "stack_trace": "%rootException",
   *                 "token":"%mdc{kb.userToken}"
   *                 }
   * @return 日志格式
   */
  String pattern() default "{\"date\":\"%d\",\"level\":\"%level\",\"logger\":\"%logger{40}\","
      + "\"thread\":\"%t\",\"location\":\"%line\",\"message\":\"%message\",\"ip\":\"%ip\","
      + "\"stack_trace\":\"%rootException{10}\",\"token\":\"%mdc{kb.userToken}\"}";

  boolean includeCallerData() default false;
}
