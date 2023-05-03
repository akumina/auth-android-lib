package com.akumina.android.auth.akuminalib.utils;

import com.akumina.android.auth.akuminalib.listener.LoggingHandler;
import com.microsoft.identity.common.internal.util.StringUtil;

public final class ValidationUtils {

    public static boolean isNull(Object object) {
        return  object == null;
    }


    public static void isEmpty(String string, String exceptionMessage, LoggingHandler loggingHandler) {
        if(StringUtil.isEmpty(string)){
            if(loggingHandler !=null) {
                loggingHandler.handleMessage(exceptionMessage, true);
            }
            throw  new IllegalStateException(exceptionMessage);
        }
    }
}
