package com.eai.browser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.List;

public class ExtensionAdapter extends RecyclerView.Adapter<ExtensionAdapter.VH> {
    public interface Listener {
        void onToggle(Extension e, boolean on);
        void onEdit(Extension e);
        void onDelete(Extension e);
    }

    private final List<Extension> items;
    private final Listener listener;

    public ExtensionAdapter(List<Extension> items, Listener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_extension, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Extension e = items.get(pos);
        h.name.setText(e.name);
        h.meta.setText("v" + e.version + "  •  " + e.type + "  •  " + e.match + "  •  " + e.runAt);
        h.toggle.setChecked(e.enabled);
        h.toggle.setOnCheckedChangeListener((btn, on) -> listener.onToggle(e, on));
        h.editBtn.setOnClickListener(v -> listener.onEdit(e));
        h.delBtn.setOnClickListener(v -> listener.onDelete(e));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, meta;
        MaterialSwitch toggle;
        android.widget.Button editBtn, delBtn;
        VH(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            meta = v.findViewById(R.id.meta);
            toggle = v.findViewById(R.id.toggle);
            editBtn = v.findViewById(R.id.editBtn);
            delBtn = v.findViewById(R.id.delBtn);
        }
    }
}
