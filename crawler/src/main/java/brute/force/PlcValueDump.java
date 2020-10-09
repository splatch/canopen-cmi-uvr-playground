package brute.force;

import java.util.Map;
import org.apache.plc4x.java.api.value.PlcValue;

public class PlcValueDump {


    public static String generateDump(PlcValue value) {
        return generateDump(value, 0);
    }

    public static String generateDump(PlcValue value, int level) {
        String prefix = repeat("  ", level);
        StringBuffer buffer = new StringBuffer().append(prefix).append(value.isList() ? "[" : "{").append("{\n");

        if (value.isStruct()) {
            Map<String, ? extends PlcValue> struct = value.getStruct();
            for (String key : struct.keySet()) {
                buffer.append(prefix).append(key).append("\n");
                buffer.append(generateDump(struct.get(key), level + 1));
            }
        } else if (value.isList()) {
            for (PlcValue vl : value.getList()) {
                buffer.append(generateDump(vl, level + 1));
            }
        } else if (value.isNull()) {
            buffer.append(simple(prefix, null, "null"));
        } else if (value.isInteger()) {
            buffer.append(simple(prefix, value.getInteger(), "int"));
        } else if (value.isShort()) {
            buffer.append(simple(prefix, value.getInteger(), "short"));
        } else if (value.isLong()) {
            buffer.append(simple(prefix, value.getLong(), "long"));
        } else if (value.isFloat()) {
            buffer.append(simple(prefix, value.getFloat(), "float"));
        } else if (value.isBigDecimal()) {
            buffer.append(simple(prefix, value.getBigDecimal(), "big decimal"));
        } else if (value.isByte()) {
            buffer.append(simple(prefix, value.getByte(), "byte"));
        } else if (value.isString()) {
            buffer.append(simple(prefix, value.getString(), "string"));
        } else if (value.isBoolean()) {
            buffer.append(simple(prefix, value.getBoolean(), "bool"));
        } else {
            buffer.append(simple(prefix, value, value.getClass()));
        }

        return buffer.append(prefix).append(value.isList() ? "]" : "{").append("{\n").toString();
    }

    private static String repeat(String prefix, int times) {
        String out = "";
        for (int i = 0; i < times; i++) {
            out += prefix;
        }
        return out;
    }

    private static String simple(String prefix, Object value, Class<?> type) {
        return simple(prefix, value, type.getSimpleName());
    }

    private static String simple(String prefix, long value, String type) {
        return prefix + value + " 0x" + Long.toHexString(value) + " (" + type + ")\n";
    }

    private static String simple(String prefix, short value, String type) {
        return prefix + value + " 0x" + Integer.toHexString(value) + " (" + type + ")\n";
    }

    private static String simple(String prefix, int value, String type) {
        return prefix + value + " 0x" + Integer.toHexString(value) + " (" + type + ")\n";
    }

    private static String simple(String prefix, Object value, String type) {
        return prefix + value + " (" + type + ")\n";
    }

}
