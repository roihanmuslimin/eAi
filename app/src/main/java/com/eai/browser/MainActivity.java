package com.eai.browser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private TextInputEditText urlInput;
    private ProgressBar progress;
    private ExtensionDb db;
    private List<Extension> extensions;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_extensions) {
                startActivity(new android.content.Intent(this, ExtensionsActivity.class));
                return true;
            } else if (id == R.id.menu_reload) {
                webView.reload();
                return true;
            } else if (id == R.id.menu_home) {
                loadUrl("https://example.com");
                return true;
            }
            return false;
        });

        db = new ExtensionDb(this);
        extensions = db.getAll();

        webView = findViewById(R.id.webview);
        urlInput = findViewById(R.id.urlInput);
        progress = findViewById(R.id.progress);

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setUserAgentString(ws.getUserAgentString() + " eAiBrowser");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                urlInput.setText(url);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                progress.setVisibility(ProgressBar.VISIBLE);
                // document_start injection
                InjectionEngine.inject(view, extensions, url, true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progress.setVisibility(ProgressBar.GONE);
                // document_end injection
                InjectionEngine.inject(view, extensions, url, false);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progress.setProgress(newProgress);
            }
        });

        urlInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_GO) {
                loadUrl(v.getText().toString());
                return true;
            }
            return false;
        });

        loadUrl("https://example.com");
    }

    private void loadUrl(String url) {
        if (url == null || url.isEmpty()) return;
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        urlInput.setText(url);
        webView.loadUrl(url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // refresh extensions list in case it changed
        extensions = db.getAll();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
