package com.zxyinfo.logger.logback.support;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志中输出ip地址
 * @author joewee
 * @version 1.0.0
 * @date 2021/7/30 14:07
 */
public class HostClassicConverter extends ClassicConverter {
  Logger logger = LoggerFactory.getLogger(HostClassicConverter.class);
  @Override
  public String convert(ILoggingEvent event) {
    String hostIp = null;
    String hostName = null;
    try {
      Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
      InetAddress ip;
      while (allNetInterfaces.hasMoreElements()) {
        NetworkInterface netInterface = allNetInterfaces.nextElement();
        if (!netInterface.isLoopback() && !netInterface.isVirtual() && netInterface.isUp()) {
          Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
          while (addresses.hasMoreElements()) {
            ip = addresses.nextElement();
            if (ip instanceof Inet4Address) {
              hostIp= ip.getHostAddress();
              hostName = ip.getHostName();
            }
          }
        }
      }
    } catch (SocketException e) {
      logger.error(e.getMessage(),e);
    }
    return StringUtils.firstNonBlank(hostIp,hostName,"未知");
  }
}
