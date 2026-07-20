package com.eai.browser;

import java.util.regex.Pattern;

public class MatchPattern {
    /**
     * Convert a wildcard match pattern into a regex.
     * Supports: scheme://host/path with '*' wildcards.
     * Examples:
     *   *.tokopedia.com/*      -> matches any subdomain/path
     *   https://*.example.com/*-> scheme restricted
     *   *://*/*                -> everything
     */
    public static boolean matches(String pattern, String url) {
        if (pattern == null || url == null) return false;
        if (pattern.equals("*")) return true;

        String p = pattern.trim();
        // Normalize: if no scheme given, accept any scheme
        if (!p.contains("://")) {
            p = "*://" + p;
        }
        // Normalize: if path omitted, match any path
        if (!p.substring(p.indexOf("://") + 3).contains("/")) {
            p = p + "/*";
        }

        StringBuilder sb = new StringBuilder("^");
        for (int i = 0; i < p.length(); i++) {
            char ch = p.charAt(i);
            if (ch == '*') {
                sb.append("[^:/]*");
            } else if (".+?^${}()|[]\\".indexOf(ch) >= 0) {
                sb.append('\\').append(ch);
            } else {
                sb.append(ch);
            }
        }
        sb.append('$');

        try {
            return Pattern.compile(sb.toString()).matcher(url).matches();
        } catch (Exception e) {
            return false;
        }
    }
}
