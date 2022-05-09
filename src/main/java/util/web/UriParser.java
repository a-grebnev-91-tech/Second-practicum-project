package util.web;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;

public class UriParser {

    public static Map<String, String> splitQuery(URI uri) throws UnsupportedEncodingException {
        final Map<String, String> queryPairs = new HashMap<>();
        final String query = uri.getQuery();
        if (query == null) {
            return Map.of();
        }
        final String[] pairs = uri.getQuery().split("&");
        for (String pair : pairs) {
            final int index = pair.indexOf("=");
            final String key = pair.substring(0, index);
            final String value = pair.substring(index + 1);
            queryPairs.put(key,value);
        }
        return queryPairs;
    }
}
