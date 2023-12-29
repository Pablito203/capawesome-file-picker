package io.capawesome.capacitorjs.plugins.filepicker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

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
        getSupportActionBar().hide();
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
}
