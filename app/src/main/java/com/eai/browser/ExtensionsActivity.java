package com.eai.browser;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ExtensionsActivity extends AppCompatActivity implements ExtensionAdapter.Listener {
    private ExtensionDb db;
    private List<Extension> list;
    private ExtensionAdapter adapter;
    private RecyclerView recycler;
    private android.widget.TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extensions);

        MaterialToolbar tb = findViewById(R.id.toolbar);
        tb.setNavigationOnClickListener(v -> finish());

        db = new ExtensionDb(this);
        list = db.getAll();

        recycler = findViewById(R.id.list);
        empty = findViewById(R.id.empty);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExtensionAdapter(list, this);
        recycler.setAdapter(adapter);

        findViewById(R.id.addBtn).setOnClickListener(v ->
                startActivity(new android.content.Intent(this, ExtensionEditActivity.class)));

        refresh();
    }

    private void refresh() {
        list.clear();
        list.addAll(db.getAll());
        adapter.notifyDataSetChanged();
        empty.setVisibility(list.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    @Override
    public void onToggle(Extension e, boolean on) {
        e.enabled = on;
        db.update(e);
    }

    @Override
    public void onEdit(Extension e) {
        android.content.Intent i = new android.content.Intent(this, ExtensionEditActivity.class);
        i.putExtra("id", e.id);
        startActivity(i);
    }

    @Override
    public void onDelete(Extension e) {
        db.delete(e.id);
        refresh();
        Toast.makeText(this, "Dihapus", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
}
