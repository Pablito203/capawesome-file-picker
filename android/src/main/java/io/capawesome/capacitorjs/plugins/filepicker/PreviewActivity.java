package io.capawesome.capacitorjs.plugins.filepicker;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class PreviewActivity extends FragmentActivity implements View.OnClickListener {

    private TextView buttonVoltar;
    private final ImageFetcher fetcher = new ImageFetcher();
    private ArrayList<Integer> lstImageIDSelected = new ArrayList();
    private ArrayList<Integer> lstImageRotateSelected = new ArrayList();
    private ViewPager2 viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lstImageIDSelected = getIntent().getIntegerArrayListExtra("lstImageIDSelected");
        lstImageRotateSelected = getIntent().getIntegerArrayListExtra("lstImageRotateSelected");

        setContentView(R.layout.preview_layout);
        setupHeader();

        viewPager = findViewById(R.id.pager);
        ViewPageAdapter pagerAdapter = new ViewPageAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        buttonVoltar = findViewById(R.id.button_back);
        buttonVoltar.setOnClickListener(this);
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
        }
    }

    private class ViewPageAdapter extends FragmentStateAdapter {

        public ViewPageAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new ViewPageFragment(lstImageIDSelected.get(position), lstImageRotateSelected.get(position));
        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    public class ViewPageFragment extends Fragment implements View.OnClickListener {
        private boolean checked = true;
        private RadioCheckView radioCheckView;
        private AppCompatImageView thumbnail;
        private int imageID = 0;
        private int imageRotate = 0;

        public ViewPageFragment(int imageID, int imageRotate) {
            this.imageID = imageID;
            this.imageRotate = imageRotate;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            ViewGroup imagePreview = (ViewGroup) inflater.inflate(R.layout.image_preview, container, false);
            radioCheckView = imagePreview.findViewById(R.id.check_view_preview);
            radioCheckView.setOnClickListener(this);
            thumbnail = imagePreview.findViewById(R.id.media_thumbnail_preview);
            fetcher.fetch(imageID, thumbnail, 0, imageRotate);
            return imagePreview;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.check_view_preview) {
                checked = !checked;
                radioCheckView.setChecked(checked);
            }
        }
    }
}
