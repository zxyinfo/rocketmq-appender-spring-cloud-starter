package com.zxyinfo.logger.logback;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.CoreConstants;
import com.zxyinfo.logger.logback.support.DynamicLogbackFilter;
import com.zxyinfo.logger.logback.support.HostClassicConverter;
import java.util.HashMap;
import java.util.Map;
import org.apache.rocketmq.logappender.logback.RocketmqLogbackAppender;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * @author joewee
 * @version 1.0.0
 * @date 2021/10/29 22:45
 */
public class RocketmqLogbackAppenderRefresher implements EnvironmentAware {

  final String nameServerAddress;
  final String producerGroup;
  final String tag;
  final String topic;
  final String pattern;
  private Environment environment;
  public static final String APPENDER_NAME = "mqAsyncAppender";

  public RocketmqLogbackAppenderRefresher(String nameServerAddress, String producerGroup,
      String tag, String topic, String pattern) {
    this.nameServerAddress = nameServerAddress;
    this.producerGroup = producerGroup;
    this.tag = tag;
    this.topic = topic;
    this.pattern = pattern;
  }

  @EventListener
  public void onEnvironmentChange(EnvironmentChangeEvent event) {
    ConfigurableApplicationContext context = (ConfigurableApplicationContext)event.getSource();
    this.environment = context.getEnvironment();
    //环境配置发生改变需重新刷新日志appender，如配置中心配置发生改变将触发重新初始化日志系统
    refresh();
  }
  public void refresh(){
    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    Logger rootLogger = lc.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    final Appender<ILoggingEvent> appender = rootLogger.getAppender(APPENDER_NAME);
    if(appender ==null ){
      Map<String, String> ruleRegistry = (Map) lc.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
      if (ruleRegistry == null) {
        ruleRegistry = new HashMap<>();
      }
      lc.putObject(CoreConstants.PATTERN_RULE_REGISTRY, ruleRegistry);
      ruleRegistry.put("ip", HostClassicConverter.class.getName());
      final RocketmqLogbackAppender mqAppender = new RocketmqLogbackAppender();
      mqAppender.setNameServerAddress(resolve(nameServerAddress));
      mqAppender.setProducerGroup(resolve(producerGroup));
      mqAppender.setTag(resolve(tag));
      mqAppender.setTopic(resolve(topic));
      PatternLayout layout = new PatternLayout();
      layout.setContext(lc);
      layout.setPattern(resolve(pattern));
      mqAppender.setLayout(layout);
      mqAppender.setName("mqAppender");
      final DynamicLogbackFilter filter = new DynamicLogbackFilter();
      mqAppender.addFilter(filter);
      mqAppender.setContext(lc);
      AsyncAppender mqAsyncAppender = new AsyncAppender();
      mqAsyncAppender.setQueueSize(1024);
      mqAsyncAppender.setDiscardingThreshold(80);
      mqAsyncAppender.setMaxFlushTime(2000);
      mqAsyncAppender.setNeverBlock(true);
      mqAsyncAppender.setContext(lc);
      mqAsyncAppender.setName(APPENDER_NAME);
      mqAsyncAppender.addAppender(mqAppender);
      rootLogger.addAppender(mqAsyncAppender);
      filter.start();
      layout.start();
      mqAppender.start();
      mqAsyncAppender.start();
    }
  }
  private String resolve(String value) {
    return StringUtils.hasText(value) ? this.environment.resolvePlaceholders(value) : value;
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }
}
