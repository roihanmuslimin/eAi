package com.eai.browser;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class ExtensionEditActivity extends AppCompatActivity {
    private static final int REQ_PICK_SCRIPT = 1;
    private static final int REQ_PICK_MANIFEST = 2;

    private ExtensionDb db;
    private Extension editing;
    private String pickedCode = null;

    private TextInputEditText nameInput, verInput, matchInput, codeInput;
    private AutoCompleteTextView typeInput, runAtInput;
    private TextView scriptStatus;

    private final ActivityResultLauncher<String> pickScript =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onScriptPicked);
    private final ActivityResultLauncher<String> pickManifest =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onManifestPicked);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extension_edit);

        db = new ExtensionDb(this);
        long id = getIntent().getLongExtra("id", -1);
        if (id > 0) {
            for (Extension e : db.getAll()) {
                if (e.id == id) { editing = e; break; }
            }
        }

        nameInput = findViewById(R.id.nameInput);
        verInput = findViewById(R.id.verInput);
        matchInput = findViewById(R.id.matchInput);
        codeInput = findViewById(R.id.codeInput);
        typeInput = findViewById(R.id.typeInput);
        runAtInput = findViewById(R.id.runAtInput);
        scriptStatus = findViewById(R.id.scriptStatus);

        typeInput.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new String[]{"js", "css"}));
        runAtInput.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{"document_end", "document_start"}));

        if (editing != null) {
            nameInput.setText(editing.name);
            verInput.setText(editing.version);
            matchInput.setText(editing.match);
            typeInput.setText(editing.type, false);
            runAtInput.setText(editing.runAt, false);
            codeInput.setText(editing.code);
            pickedCode = editing.code;
            scriptStatus.setText("Kode tersimpan (" + (editing.code != null ? editing.code.length() : 0) + " chars)");
        }

        findViewById(R.id.uploadScriptBtn).setOnClickListener(v ->
                pickScript.launch("*/*"));

        findViewById(R.id.saveBtn).setOnClickListener(v -> save());
    }

    private void onScriptPicked(Uri uri) {
        if (uri == null) return;
        String text = readUri(uri);
        if (text != null) {
            pickedCode = text;
            codeInput.setText(text);
            scriptStatus.setText("File dipilih: " + uri.getLastPathSegment()
                    + " (" + text.length() + " chars)");
            // guess type by extension
            String name = uri.getLastPathSegment();
            if (name != null) {
                if (name.endsWith(".css")) typeInput.setText("css", false);
                else if (name.endsWith(".js")) typeInput.setText("js", false);
            }
        }
    }

    private void onManifestPicked(Uri uri) {
        if (uri == null) return;
        String text = readUri(uri);
        if (text == null) return;
        try {
            JSONObject m = new JSONObject(text);
            if (m.has("name")) nameInput.setText(m.getString("name"));
            if (m.has("version")) verInput.setText(m.getString("version"));
            if (m.has("match")) matchInput.setText(m.getString("match"));
            if (m.has("type")) typeInput.setText(m.getString("type"), false);
            if (m.has("run_at")) runAtInput.setText(m.getString("run_at"), false);
            scriptStatus.setText("Manifest dibaca. Sekarang pilih file script-nya.");
            Toast.makeText(this, "Manifest terbaca", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Manifest JSON tidak valid", Toast.LENGTH_SHORT).show();
        }
    }

    private String readUri(Uri uri) {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(
                getContentResolver().openInputStream(uri), StandardCharsets.UTF_8))) {
            return r.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            Toast.makeText(this, "Gagal membaca file", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void save() {
        String name = nameInput.getText().toString().trim();
        if (name.isEmpty()) { nameInput.setError("Wajib diisi"); return; }
        String match = matchInput.getText().toString().trim();
        if (match.isEmpty()) { matchInput.setError("Wajib diisi"); return; }
        String code = codeInput.getText().toString();
        if (code.isEmpty()) { codeInput.setError("Kode wajib diisi"); return; }

        Extension e = editing != null ? editing : new Extension();
        e.name = name;
        e.version = verInput.getText().toString().trim().isEmpty()
                ? "1.0" : verInput.getText().toString().trim();
        e.match = match;
        e.type = typeInput.getText().toString().trim();
        e.runAt = runAtInput.getText().toString().trim();
        e.code = code;

        if (editing == null) {
            db.insert(e);
            Toast.makeText(this, "Extension ditambahkan", Toast.LENGTH_SHORT).show();
        } else {
            db.update(e);
            Toast.makeText(this, "Disimpan", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
