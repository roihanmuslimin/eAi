package com.eai.browser;

import android.webkit.WebView;

import java.util.List;

public class InjectionEngine {

    /**
     * Inject all matching, enabled extensions into the given WebView.
     * @param start true if this is document_start, false for document_end
     */
    public static void inject(WebView view, List<Extension> extensions, String url, boolean start) {
        if (url == null) return;
        for (Extension e : extensions) {
            if (!e.enabled) continue;
            if (!MatchPattern.matches(e.match, url)) continue;
            boolean atStart = "document_start".equals(e.runAt);
            if (atStart != start) continue;

            if ("css".equals(e.type)) {
                injectCss(view, e.code);
            } else {
                injectJs(view, e.code);
            }
        }
    }

    private static void injectJs(WebView view, String js) {
        String wrapped = "javascript:(function(){" +
                js.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("'", "\\'") +
                "})();";
        view.evaluateJavascript(wrapped, null);
    }

    private static void injectCss(WebView view, String css) {
        String escaped = css.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        String js = "(function(){" +
                "var s=document.createElement('style');" +
                "s.textContent=\"" + escaped + "\";" +
                "document.documentElement.appendChild(s);" +
                "})();";
        view.evaluateJavascript(js, null);
    }
}
