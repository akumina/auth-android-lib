package com.akumina.android.auth.akuminalib.utils;

import com.microsoft.identity.common.internal.util.StringUtil;

public final class ValidationUtils {

    public static boolean isNull(Object object) {
        return  object == null;
    }


    public static void isEmpty(String string, String exceptionMessage) {
        if(StringUtil.isEmpty(string)){
            throw  new IllegalStateException(exceptionMessage);
        }
    }
}
