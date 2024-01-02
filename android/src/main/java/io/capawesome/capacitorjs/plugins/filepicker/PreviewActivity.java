package io.capawesome.capacitorjs.plugins.filepicker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class PreviewActivity extends FragmentActivity implements View.OnClickListener {

    private TextView buttonVoltar;
    private RadioCheckView radioCheckView;
    private boolean checked = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.preview_layout);
        setupHeader();

        buttonVoltar = findViewById(R.id.button_back);
        buttonVoltar.setOnClickListener(this);

        radioCheckView = findViewById(R.id.check_view_preview);
        radioCheckView.setOnClickListener(this);
    }

    private void setupHeader() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_back) {
            finish();
        } else if (v.getId() == R.id.check_view_preview) {
            checked = !checked;
            radioCheckView.setChecked(checked);
        }
    }

    private class ViewPageAdapter extends FragmentStateAdapter {

        public ViewPageAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return null;
        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
