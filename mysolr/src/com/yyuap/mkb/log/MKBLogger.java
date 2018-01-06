package com.yyuap.mkb.log;

import org.apache.log4j.Logger;

public class MKBLogger {

    private static Logger logger_info = null;
    private static Logger logger_error = null;
    private static Logger logger_debug = null;

    public static void info(String log) {
        if (logger_info == null) {
            logger_info = Logger.getLogger("bot_kb_info_logger");
        }
        logger_info.info(log);
    }

    public static void error(String log) {
        if (logger_error == null) {
            logger_error = Logger.getLogger("bot_kb_error_logger");
        }
        logger_error.error(log);
    }

    public static void debug(String log) {
        if (logger_debug == null) {
            logger_debug = Logger.getLogger("bot_kb_debug_logger");
        }
        logger_debug.error(log);
    }

}