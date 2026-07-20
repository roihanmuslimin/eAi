package com.eai.browser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ExtensionDb extends SQLiteOpenHelper {
    private static final String DB_NAME = "eai_ext.db";
    private static final int DB_VER = 1;
    private static final String TBL = "extensions";

    public ExtensionDb(Context ctx) {
        super(ctx, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TBL + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "version TEXT," +
                "match TEXT," +
                "run_at TEXT," +
                "type TEXT," +
                "code TEXT," +
                "enabled INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL);
        onCreate(db);
    }

    public long insert(Extension e) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("name", e.name);
        v.put("version", e.version);
        v.put("match", e.match);
        v.put("run_at", e.runAt);
        v.put("type", e.type);
        v.put("code", e.code);
        v.put("enabled", e.enabled ? 1 : 0);
        long id = db.insert(TBL, null, v);
        db.close();
        return id;
    }

    public void update(Extension e) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put("name", e.name);
        v.put("version", e.version);
        v.put("match", e.match);
        v.put("run_at", e.runAt);
        v.put("type", e.type);
        v.put("code", e.code);
        v.put("enabled", e.enabled ? 1 : 0);
        db.update(TBL, v, "id=?", new String[]{String.valueOf(e.id)});
        db.close();
    }

    public void delete(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TBL, "id=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Extension> getAll() {
        List<Extension> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TBL + " ORDER BY id DESC", null);
        while (c.moveToNext()) {
            Extension e = new Extension();
            e.id = c.getLong(c.getColumnIndexOrThrow("id"));
            e.name = c.getString(c.getColumnIndexOrThrow("name"));
            e.version = c.getString(c.getColumnIndexOrThrow("version"));
            e.match = c.getString(c.getColumnIndexOrThrow("match"));
            e.runAt = c.getString(c.getColumnIndexOrThrow("run_at"));
            e.type = c.getString(c.getColumnIndexOrThrow("type"));
            e.code = c.getString(c.getColumnIndexOrThrow("code"));
            e.enabled = c.getInt(c.getColumnIndexOrThrow("enabled")) == 1;
            list.add(e);
        }
        c.close();
        db.close();
        return list;
    }
}
