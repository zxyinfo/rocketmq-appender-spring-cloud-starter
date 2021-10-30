package com.zxyinfo.logger.logback.support;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.zxyinfo.logger.logback.util.BeanFactoryUtils;
import com.zxyinfo.logger.logback.util.ThrowableUtils;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author joewee
 * @version 1.0.0
 * @date 2021/7/21 10:22
 */
public class DynamicLogbackFilter extends Filter<ILoggingEvent> {

  private static final Logger log = LoggerFactory.getLogger(DynamicLogbackFilter.class);

  public static final String ALL = "all";

  @Override
  public FilterReply decide(ILoggingEvent iLoggingEvent) {
    if (!this.isStarted()) {
      return FilterReply.NEUTRAL;
    }
    final Level level = iLoggingEvent.getLevel();
    try {
      final String loggerName = iLoggingEvent.getLoggerName();
      final LoggerConfigService configService;
      configService = BeanFactoryUtils.getBean(LoggerConfigService.class);
      if(configService==null){
        return FilterReply.DENY;
      }
      final Map<String,String> loggers = configService.getLogger();
      if (loggers==null|| (!loggers.containsKey(loggerName)&& !loggers.containsKey(ALL))) {
        return FilterReply.DENY;
      }else{
        final String logLevel =StringUtils.firstNonBlank(loggers.get(loggerName),"ERROR");
        if (level.isGreaterOrEqual(Level.toLevel(logLevel))) {
          return  FilterReply.ACCEPT;
        }else{
          return FilterReply.DENY;
        }
      }
    }catch (Exception e) {
      log.error(ThrowableUtils.findRootCause(e).getMessage());
    }
    return FilterReply.DENY;
  }
}
