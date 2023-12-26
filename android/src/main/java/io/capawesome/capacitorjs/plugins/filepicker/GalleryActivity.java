package io.capawesome.capacitorjs.plugins.filepicker;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GalleryActivity extends AppCompatActivity implements OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private ImageAdapter ia;
    private Cursor imagecursor, actualimagecursor;
    private int image_column_index, image_column_orientation;
    private int colWidth;
    private static final int CURSORLOADER_THUMBS = 0;
    private final ImageFetcher fetcher = new ImageFetcher();
    private boolean shouldRequestThumb = true;

    private Map<String, Integer> fileNames = new HashMap<String, Integer>();

    private SparseBooleanArray checkStatus = new SparseBooleanArray();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid);

        int maximumFilesCount = 3;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(String.format("Limite de %d arquivos", maximumFilesCount));
        dialogBuilder.setMessage(String.format("Você pode selecionar até %d arquivos", maximumFilesCount));
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.create();
        dialogBuilder.show();

        setupHeader();

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();

        colWidth = width / 4;

        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setOnItemClickListener(this);
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int lastFirstItem = 0;
            private long timestamp = System.currentTimeMillis();

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    shouldRequestThumb = true;
                    ia.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                float dt = System.currentTimeMillis() - timestamp;
                if (firstVisibleItem != lastFirstItem) {
                    double speed = 1 / dt * 1000;
                    lastFirstItem = firstVisibleItem;
                    timestamp = System.currentTimeMillis();

                    // Limit if we go faster than a page a second
                    shouldRequestThumb = speed < visibleItemCount;
                }
            }
        });

        ia = new ImageAdapter();
        gridView.setAdapter(ia);

        LoaderManager.enableDebugLogging(false);
        LoaderManager.getInstance(this).initLoader(CURSORLOADER_THUMBS, null, this);
    }

    private void setupHeader() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View header = inflater.inflate(R.layout.actionbar, null);

        // Show the custom action bar view and hide the normal Home icon and title.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(
                    ActionBar.DISPLAY_SHOW_CUSTOM,
                    ActionBar.DISPLAY_SHOW_CUSTOM
                            | ActionBar.DISPLAY_SHOW_HOME
                            | ActionBar.DISPLAY_SHOW_TITLE
            );
            actionBar.setCustomView(header, new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = getImageName(position);
        int rotation = getImageRotation(position);

        if (name == null) {
            return;
        }

        boolean isChecked = !isChecked(position);

        if (isChecked && 3 == 1) {
            isChecked = false;
        } else if (isChecked) {
            fileNames.put(name, rotation);

            if (3 == 1) {
                //selectClicked();

            } else {
                //maxImages--;
                ImageGridView imageGridView = (ImageGridView) view;

                imageGridView.mThumbnail.setImageAlpha(128);
                imageGridView.mThumbnail.setBackgroundColor(Color.BLACK);
                imageGridView.mRadioCheckView.setChecked(true);
            }
        } else {
            fileNames.remove(name);
            //maxImages++;
            ImageGridView imageGridView = (ImageGridView) view;

            imageGridView.mThumbnail.setImageAlpha(255);
            imageGridView.mThumbnail.setBackgroundColor(Color.TRANSPARENT);
            imageGridView.mRadioCheckView.setChecked(false);
        }

        checkStatus.put(position, isChecked);
        //updateAcceptButton();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        ArrayList<String> img = new ArrayList<String>();
        switch (id) {
            case CURSORLOADER_THUMBS:
                img.add(MediaStore.Images.Media._ID);
                img.add(MediaStore.Images.Media.ORIENTATION);
                break;
        }

        return new CursorLoader(
                this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                img.toArray(new String[img.size()]),
                null,
                null,
                "DATE_MODIFIED DESC"
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null) {
            // NULL cursor. This usually means there's no image database yet....
            return;
        }

        switch (loader.getId()) {
            case CURSORLOADER_THUMBS:
                imagecursor = cursor;
                image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
                image_column_orientation = imagecursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);
                ia.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        switch (loader.getId()) {
            case CURSORLOADER_THUMBS:
                imagecursor = null;
                break;
        }
    }


    private String getImageName(int position) {
        imagecursor.moveToPosition(position);
        String name = null;

        try {
            name = imagecursor.getString(image_column_index);
        } catch (Exception e) {
            // Do something?
        }

        return name;
    }

    private int getImageRotation(int position) {
        imagecursor.moveToPosition(position);
        int rotation = 0;

        try {
            rotation = imagecursor.getInt(image_column_orientation);
        } catch (Exception e) {
            // Do something?
        }

        return rotation;
    }

    public boolean isChecked(int position) {
        return checkStatus.get(position);
    }

    private class SquareImageView extends androidx.appcompat.widget.AppCompatImageView {
        public SquareImageView(Context context) {
            super(context);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        }
    }

    private class ImageGridView extends FrameLayout {
        ImageView mThumbnail;
        RadioCheckView mRadioCheckView;

        public ImageGridView(Context context) {
            super(context);
            init(context);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        }

        private void init(Context context) {
            LayoutInflater.from(context).inflate(R.layout.image_grid_view, this, true);

            mThumbnail = (ImageView) findViewById(R.id.media_thumbnail);
            mRadioCheckView = (RadioCheckView) findViewById(R.id.check_view);
        }
    }

    private class ImageAdapter extends BaseAdapter {

        public int getCount() {
            if (imagecursor != null) {
                return imagecursor.getCount();
            } else {
                return 0;
            }
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = View .inflate (GalleryActivity.this , R .layout .image_grid_view , null );
            }

            ImageView thumbnail = convertView .findViewById (R .id .media_thumbnail );
            RadioCheckView radioCheckView = convertView .findViewById (R .id .check_view );
            thumbnail .setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumbnail.setImageBitmap(null);

            if (!imagecursor.moveToPosition(position)) {
                return convertView;
            }

            if (image_column_index == -1) {
                return convertView;
            }

            final int id = imagecursor.getInt(image_column_index);
            final int rotate = imagecursor.getInt(image_column_orientation);

            if (isChecked(position)) {
                thumbnail.setImageAlpha(128);
                thumbnail.setBackgroundColor(Color.BLACK);
                radioCheckView.setChecked(true);
            } else {
                thumbnail.setImageAlpha(255);
                thumbnail.setBackgroundColor(Color.TRANSPARENT);
                radioCheckView.setChecked(false);
            }

            if (shouldRequestThumb) {
                fetcher.fetch(id, thumbnail, colWidth, rotate);
            }

            return convertView;
        }
    }
}
