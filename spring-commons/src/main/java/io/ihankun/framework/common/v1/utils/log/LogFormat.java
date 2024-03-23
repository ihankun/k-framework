package io.ihankun.framework.common.v1.utils.log;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author hankun
 */
public class LogFormat {

    public LogFormat() {
    }

    public static String formatMsg(String methodName, String id, Object info) {
        StringBuffer buffer = getLogBuffer(methodName, id, info == null ? null : info.toString());
        return buffer.toString();
    }

    public static String formatMsg(String methodName, String id, String formatStr, Object... args) {
        String info = formatStr != null && args != null ? String.format(formatStr, args) : formatStr;
        StringBuffer buffer = getLogBuffer(methodName, id, info == null ? null : info.toString());
        return buffer.toString();
    }

    public static String formatMsg(String methodName, long id, Object info) {
        StringBuffer buffer = getLogBuffer(methodName, String.valueOf(id), info == null ? null : info.toString());
        return buffer.toString();
    }

    public static String formatChargeInfo(String methodName, String coperateCode, Map<String, String> chargeInfo) {
        StringBuffer info = new StringBuffer();
        if (chargeInfo != null) {
            Set<String> keySet = chargeInfo.keySet();
            Iterator var5 = keySet.iterator();

            while (var5.hasNext()) {
                String name = (String) var5.next();
                info.append(name);
                info.append("=");
                info.append((String) chargeInfo.get(name));
                info.append("^");
            }
        }

        StringBuffer buffer = getLogBuffer(methodName, coperateCode, info.toString());
        return buffer.toString();
    }

    public static String formatError(String methodName, Exception e, String info) {
        StringBuilder eBuffer = new StringBuilder();
        StackTraceElement[] elements = e.getStackTrace();
        StackTraceElement[] var5 = elements;
        int var6 = elements.length;

        for (int var7 = 0; var7 < var6; ++var7) {
            StackTraceElement ste = var5[var7];
            eBuffer.append(ste.toString());
            eBuffer.append("/r/n");
        }

        eBuffer.append(info);
        StringBuffer buffer = getLogBuffer(methodName, eBuffer.toString(), info);
        return buffer.toString();
    }

    public static String formatCaptailMsg(String method, long userId, String operateType, long tradeId, double money, String info) {
        StringBuffer infoBuffer = new StringBuffer();
        infoBuffer.append(" -userId:");
        infoBuffer.append(userId);
        infoBuffer.append("-tradeId:");
        infoBuffer.append(tradeId);
        infoBuffer.append(" [ ");
        infoBuffer.append(operateType);
        infoBuffer.append("  ");
        infoBuffer.append(money);
        if (info != null) {
            infoBuffer.append(info);
        }

        StringBuffer buffer = getLogBuffer(method, String.valueOf(userId), infoBuffer.toString());
        return buffer.toString();
    }

    private static StringBuffer getLogBuffer(String methodName, String id, String info) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(methodName);
        buffer.append(";");
        buffer.append(id);
        buffer.append(";");
        buffer.append(info);
        return buffer;
    }
}
