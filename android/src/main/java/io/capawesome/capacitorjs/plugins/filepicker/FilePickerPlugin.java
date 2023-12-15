package io.capawesome.capacitorjs.plugins.filepicker;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;

@CapacitorPlugin(name = "FilePicker")
public class FilePickerPlugin extends Plugin {

    public static final String TAG = "FilePickerPlugin";

    public static final String ERROR_PICK_FILE_FAILED = "pickFiles failed.";
    public static final String ERROR_PICK_FILE_CANCELED = "pickFiles canceled.";
    private FilePicker implementation;

    public void load() {
        implementation = new FilePicker(this.getBridge());
    }

    @PluginMethod
    public void convertHeicToJpeg(PluginCall call) {
        call.unimplemented("Not implemented on Android.");
    }

    @PluginMethod
    public void pickFiles(PluginCall call) {
        try {
            JSArray types = call.getArray("types", null);
            boolean multiple = call.getBoolean("multiple", false);
            int maximumFilesCount = call.getInt("maximumFilesCount", 15);
            String[] parsedTypes = parseTypesOption(types);

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiple);
            intent.putExtra("MAX_FILES", maximumFilesCount);
            if (multiple == false && parsedTypes != null && parsedTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, parsedTypes);
            }

            startActivityForResult(call, intent, "pickFilesResult");
        } catch (Exception ex) {
            String message = ex.getMessage();
            Log.e(TAG, message);
            call.reject(message);
        }
    }

    @PluginMethod
    public void pickImages(PluginCall call) {
        try {
            boolean multiple = call.getBoolean("multiple", false);
            int maximumFilesCount = call.getInt("maximumFilesCount", 15);

            Intent intent = new Intent(this.getActivity(), GalleryActivity.class);
            intent.setAction(MediaStore.ACTION_PICK_IMAGES);
            intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, maximumFilesCount);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] { "image/*" });

            Matisse.from(this.getActivity())
                    .choose(MimeType.allOf())
                    .countable(true)
                    .maxSelectable(9)
                    .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                    .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngine())
                    .showPreview(false) // Default is `true`
                    .forResult(REQUEST_CODE_CHOOSE);
            //startActivityForResult(call, intent, "pickFilesResult");
        } catch (Exception ex) {
            String message = ex.getMessage();
            Log.e(TAG, message);
            call.reject(message);
        }
    }

    @PluginMethod
    public void pickMedia(PluginCall call) {
        try {
            boolean multiple = call.getBoolean("multiple", false);
            int maximumFilesCount = call.getInt("maximumFilesCount", 15);
            
            Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiple);
            intent.setType("*/*");
            intent.putExtra("multi-pick", multiple);
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] { "image/*", "video/*" });

            startActivityForResult(call, intent, "pickFilesResult");
        } catch (Exception ex) {
            String message = ex.getMessage();
            Log.e(TAG, message);
            call.reject(message);
        }
    }

    @PluginMethod
    public void pickVideos(PluginCall call) {
        try {
            boolean multiple = call.getBoolean("multiple", false);
            int maximumFilesCount = call.getInt("maximumFilesCount", 15);

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiple);
            intent.setType("video/*");
            intent.putExtra("multi-pick", multiple);
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] { "video/*" });

            startActivityForResult(call, intent, "pickFilesResult");
        } catch (Exception ex) {
            String message = ex.getMessage();
            Log.e(TAG, message);
            call.reject(message);
        }
    }

    @Nullable
    private String[] parseTypesOption(@Nullable JSArray types) {
        if (types == null) {
            return null;
        }
        try {
            List<String> typesList = types.toList();
            if (typesList.contains("text/csv")) {
                typesList.add("text/comma-separated-values");
            }
            return typesList.toArray(new String[0]);
        } catch (JSONException exception) {
            Logger.error("parseTypesOption failed.", exception);
            return null;
        }
    }

    @ActivityCallback
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            Log.d("Matisse", "mSelected: " + mSelected);
        }
    }

    private JSObject createPickFilesResult(@Nullable Intent data, boolean readData) {
        JSObject callResult = new JSObject();
        List<JSObject> filesResultList = new ArrayList<>();
        if (data == null) {
            callResult.put("files", JSArray.from(filesResultList));
            return callResult;
        }
        List<Uri> uris = new ArrayList<>();
        if (data.getClipData() == null) {
            Uri uri = data.getData();
            uris.add(uri);
        } else {
            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                Uri uri = data.getClipData().getItemAt(i).getUri();
                uris.add(uri);
            }
        }
        for (int i = 0; i < uris.size(); i++) {
            Uri uri = uris.get(i);
            if (uri == null) {
                continue;
            }
            JSObject fileResult = new JSObject();
            if (readData) {
                fileResult.put("data", implementation.getDataFromUri(uri));
            }
            Long duration = implementation.getDurationFromUri(uri);
            if (duration != null) {
                fileResult.put("duration", duration);
            }
            FileResolution resolution = implementation.getHeightAndWidthFromUri(uri);
            if (resolution != null) {
                fileResult.put("height", resolution.height);
                fileResult.put("width", resolution.width);
            }
            fileResult.put("mimeType", implementation.getMimeTypeFromUri(uri));
            Long modifiedAt = implementation.getModifiedAtFromUri(uri);
            if (modifiedAt != null) {
                fileResult.put("modifiedAt", modifiedAt);
            }
            fileResult.put("name", implementation.getNameFromUri(uri));
            fileResult.put("path", implementation.getPathFromUri(uri));
            fileResult.put("size", implementation.getSizeFromUri(uri));
            filesResultList.add(fileResult);
        }
        callResult.put("files", JSArray.from(filesResultList.toArray()));
        return callResult;
    }

    private boolean checkMaxFiles(Intent data, int maximumFilesCount) {
        if (data.getClipData() == null) {
            return true;
        }

        int countFilesSelected = data.getClipData().getItemCount();

        return countFilesSelected > maximumFilesCount;
    }
    private void showLimitDialog(int maximumFilesCount) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getActivity());
        dialogBuilder.setTitle(String.format("Limite de %d arquivos", maximumFilesCount));
        dialogBuilder.setMessage(String.format("Você pode selecionar até %d arquivos", maximumFilesCount));
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.create();
        dialogBuilder.show();
    }
}
