package com.yyuap.mkb.log;

import org.apache.log4j.Logger;

public class MKBLogger3 {
    private static Logger logger_info = null;
    private static Logger logger_error = null;

    public static Logger getInfoLogger() {
        if (logger_info == null) {
            logger_info = Logger.getLogger("bot_kb_info_logger");
        }
        return logger_info;
    }

    public static Logger getErrorLogger() {
        if (logger_error == null) {
            logger_error = Logger.getLogger("bot_kb_error_logger");
        }
        return logger_error;
    }
}
