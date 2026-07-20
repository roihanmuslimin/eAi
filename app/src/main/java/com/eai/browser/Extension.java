package com.eai.browser;

import org.json.JSONException;
import org.json.JSONObject;

public class Extension {
    public long id;
    public String name;
    public String version;
    public String match;
    public String runAt;   // document_start | document_end
    public String type;    // js | css
    public String code;    // the JS or CSS content
    public boolean enabled;

    public Extension() {
        this.runAt = "document_end";
        this.type = "js";
        this.enabled = true;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("name", name);
        o.put("version", version);
        o.put("match", match);
        o.put("run_at", runAt);
        o.put("type", type);
        o.put("enabled", enabled);
        return o;
    }

    public static Extension fromJson(JSONObject o) throws JSONException {
        Extension e = new Extension();
        e.name = o.optString("name", "Untitled");
        e.version = o.optString("version", "1.0");
        e.match = o.optString("match", "*://*/*");
        e.runAt = o.optString("run_at", "document_end");
        e.type = o.optString("type", "js");
        e.enabled = o.optBoolean("enabled", true);
        return e;
    }
}
