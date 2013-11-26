package info.haxahaxa;

import java.util.Map;

public class JsonGenerator {

	public static String toJson(Object... kvpair) {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (int i = 0; i < kvpair.length; i += 2) {
			sb.append('"');
			sb.append(kvpair[i]);
			sb.append('"');
			sb.append(':');
			if (kvpair[i + 1] instanceof Number) {
				sb.append('"');
				sb.append(kvpair[i + 1]);
				sb.append('"');
			} else {
				sb.append('"');
				sb.append(kvpair[i + 1]);
				sb.append('"');
			}
			sb.append(',');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append('}');
		return sb.toString();
	}

	public static String toJson(Map<String, Object> data) {
		StringBuilder sb = new StringBuilder();
		return sb.toString();
	}
}
