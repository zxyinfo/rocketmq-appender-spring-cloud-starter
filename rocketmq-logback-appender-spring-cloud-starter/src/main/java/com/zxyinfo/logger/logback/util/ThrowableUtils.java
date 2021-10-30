package com.zxyinfo.logger.logback.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author joewee
 * @version 1.0.0
 * @date 2021/10/29 10:32
 */
public class ThrowableUtils {
  private ThrowableUtils() {
  }

  public static Throwable findRootCause(Throwable cause) {
    Throwable rootCause;
    for(rootCause = cause; rootCause != null && rootCause.getCause() != null; rootCause = rootCause.getCause()) {
    }

    return rootCause;
  }

  public static String printStackTrace(Throwable cause, boolean rootCauseOnly) {
    if (rootCauseOnly) {
      cause = findRootCause(cause);
    }

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    if (cause != null) {
      cause.printStackTrace(pw);
    }

    return sw.toString();
  }
}
