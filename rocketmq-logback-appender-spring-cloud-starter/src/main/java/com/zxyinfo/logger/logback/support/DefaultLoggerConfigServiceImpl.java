package com.zxyinfo.logger.logback.support;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * @author joewee
 * @version 1.0.0
 * @date 2021/10/27 14:20
 */
@Service
@RefreshScope
public class DefaultLoggerConfigServiceImpl implements LoggerConfigService {
  @Value("#{${alert.logger:null}}")
  private Map<String,String> logger;
  @Override
  public Map<String,String> getLogger(){
    return logger;
  }
}
