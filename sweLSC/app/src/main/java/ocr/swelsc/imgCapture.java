package ocr.swelsc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by tguic on 4/13/2016.
 */
public class imgCapture extends Fragment implements View.OnClickListener {

    View view;
    private File imageFile;
    private static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private String recognizedText;
    public static final String PACKAGE_NAME = "ocr.swelsc";
    public static final String DATA_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();


    public static final String lang = "eng";

    private static final String TAG = "imgCapture.java";

    protected Button _button;

    protected EditText printOCR;
    protected String _path;
    protected boolean _taken;
    private int count = 0;
    protected static final String PHOTO_TAKEN = "photo_taken";
    private String imagePath;
    private EditText showText;
    final Intent intentCapture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    final Intent intent = new Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    String selectedImagePath;
    boolean boolFour = false;
    boolean boolImport4 = false;

    public imgCapture() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.imgcapture, container, false);


        String[] paths = new String[]{DATA_PATH, DATA_PATH + "/tessdata/"};

        for (String path : paths) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.v(TAG, "ERROR: Creation of directory " + path + " on sdcard failed");

                } else {
                    Log.v(TAG, "Created directory " + path + " on sdcard");
                }
            }

        }

        if (!(new File(DATA_PATH + "/tessdata/" + lang + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = view.getContext().getAssets();
                InputStream in = assetManager.open("tessdata/" + lang + ".traineddata");

                //GZIPInputStream gin = new GZIPInputStream(in);
                OutputStream out = new FileOutputStream(DATA_PATH + "/tessdata/" + lang + ".traineddata");

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                //while ((lenf = gin.read(buff)) > 0) {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                //gin.close();
                out.close();

                Log.v(TAG, "Copied " + lang + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + lang + " traineddata " + e.toString());
            }
        }

        super.onCreate(savedInstanceState);

        printOCR = (EditText) view.findViewById(R.id.ocrText);
        selectedImagePath = DATA_PATH + "/image.jpg";

        Button btnCapture = (Button) view.findViewById(R.id.capture4);
        btnCapture.setOnClickListener(this);
        Button btnCapture3 = (Button) view.findViewById(R.id.capture3);
        btnCapture3.setOnClickListener(this);
        Button btnCal = (Button) view.findViewById(R.id.calendar);
        btnCal.setOnClickListener(this);
        Button btnImport = (Button) view.findViewById(R.id.importButton3);
        btnImport.setOnClickListener(this);
        Button btnImport4 = (Button) view.findViewById(R.id.importButton4);
        btnImport4.setOnClickListener(this);
        return view;
    }


    protected void startCameraActivity() {
//        File file = new File(selectedImagePath);
//        if (file.exists()) {
//            while (file.exists()) {
//                ++count;
//                selectedImagePath = DATA_PATH + "/image" + count + ".jpg";
//                file = new File(selectedImagePath);
//            }
//            Uri outputFileUri = Uri.fromFile(file);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//        } else {
//            Uri outputFileUri = Uri.fromFile(file);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//        }
        startActivityForResult(intentCapture, 1);
    }

    protected void importPicture() {
//        File file = new File(selectedImagePath);
//        if (file.exists()) {
//            while (file.exists()) {
//                ++count;
//                selectedImagePath = DATA_PATH + "/image" + count + ".jpg";
//                file = new File(selectedImagePath);
//            }
//            Uri outputFileUri = Uri.fromFile(file);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//        } else {
//            Uri outputFileUri = Uri.fromFile(file);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//        }
        startActivityForResult(intent, 1);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        onPhotoTaken();
//        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_OK && null != data) {
//
//                // Image captured and saved to fileUri specified in the Intent
//                Toast.makeText(view.getContext(), "Image saved to:\n" +
//                        data.getData(), Toast.LENGTH_LONG).show();
//            }
//        }
//    }


    public void setRecognizedText(String recognizedText) {
        this.recognizedText = recognizedText;
    }

    @SuppressLint("ValidFragment")
    public imgCapture(String recognizedText) {

        this.recognizedText = recognizedText;
    }

    protected void onPhotoTaken4() {
        _taken = true;
        boolImport4 = false;
        boolFour = false;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);

        try {
            ExifInterface exif = new ExifInterface(selectedImagePath);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v(TAG, "Orient: " + exifOrientation);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }

            // Convert to ARGB_8888, required by tess
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        } catch (IOException e) {
            Log.e(TAG, "Couldn't correct orientation: " + e.toString());
        }

        // _image.setImageBitmap( bitmap );

        Log.v(TAG, "Before baseApi");

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(DATA_PATH, lang);
        baseApi.setImage(bitmap);

        recognizedText = baseApi.getUTF8Text();
        baseApi.end();

        if (lang.equalsIgnoreCase("eng")) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }

        recognizedText = recognizedText.trim();
        Toast.makeText(view.getContext(), recognizedText, Toast.LENGTH_LONG).show();
        //GATHER INSTRUCTOR NAME////////////////////////////////////
        boolean stop = false;
        int spaceCount = 0;
        int dashCount = 0;

        recognizedText = recognizedText.toLowerCase();
        int fInstructor = recognizedText.indexOf("instructor");
        if (fInstructor == -1) {
            printOCR.setText(printOCR.getText() + "Instructor not found. \n");
        } else {
            StringBuffer gather = new StringBuffer();
            char[] testChar = recognizedText.toCharArray();
            for (int i = fInstructor; i < recognizedText.length() - fInstructor; i++) {
                if (testChar[i] == ' ') {
                    spaceCount++;
                    if (spaceCount == 3) {
                        break;
                    }
                }
                gather.append(testChar[i]);
            }
            spaceCount = 0;
            printOCR.setText(gather + "\n");
            //END GATHER INSTRUCTOR NAME////////////////////////////////
        }
        int fPhone = recognizedText.indexOf("404");
        if (fPhone == -1) {
            printOCR.setText(printOCR.getText() + "phone not found \n");
        } else {
            StringBuffer gather = new StringBuffer();
            char[] testChar = recognizedText.toCharArray();
            for (int i = fPhone; i < recognizedText.length() - 2; i++) {
                if (testChar[i] == ' ') {
                    spaceCount++;
                    if (spaceCount == 2) {
                        for (int j = 0; j < 5; j++) {
                            gather.append(testChar[i]);
                            i++;
                        }
                        break;
                    }
                }
                gather.append(testChar[i]);
            }
            dashCount = 0;
            printOCR.setText(printOCR.getText() + "Phone: " + gather + "\n");
            //END GATHER INSTRUCTOR NAME////////////////////////////////
        }

        int fEmail = recognizedText.indexOf("mail");
        if (fEmail == -1) {
            printOCR.setText(printOCR.getText() + "email not found \n");
        } else {
            StringBuffer gatherEmail = new StringBuffer();
            char[] testChar = recognizedText.toCharArray();
            int spaceCountEmail = 0;
            for (int i = fEmail + 4; i < recognizedText.length(); i++) {
                if (testChar[i] == ' ') {
                    spaceCountEmail++;
                    if (spaceCountEmail == 2) {
                        break;
                    }
                }
                gatherEmail.append(testChar[i]);

            }
            printOCR.setText(printOCR.getText() + "E-mail: " + gatherEmail + "@student.gsu.edu" + "\n");
            //END GATHER INSTRUCTOR NAME////////////////////////////////
        }
        int fISBN = recognizedText.indexOf("isbn");
        if (fISBN == -1) {
            printOCR.setText(printOCR.getText() + "ISBN not found \n");
        } else {
            StringBuffer gather = new StringBuffer();
            char[] testChar = recognizedText.toCharArray();
            spaceCount = 0;
            for (int i = fISBN; i < recognizedText.length() - 2; i++) {
                if (testChar[i] == ' ') {
                    spaceCount++;
                    if (spaceCount == 4) {
                        for (int j = 0; j < 5; j++) {
                            gather.append(testChar[i]);
                            i++;
                        }
                        break;
                    }
                }
                gather.append(testChar[i]);
            }


            String ISBN = gather.toString();
            dashCount = 0;
            printOCR.setText(printOCR.getText() + "ISBN: " + ISBN + "\n");
            //END GATHER INSTRUCTOR NAME////////////////////////////////
        }


    }

    public String getRecognizedText() {
        return recognizedText;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.capture4:
                boolFour = true;
                startCameraActivity();
                break;
            case R.id.calendar:
                calendarEvent();
                break;
            case R.id.importButton3:
                importPicture();
                break;
            case R.id.importButton4:
                boolImport4 = true;
                importPicture();
                break;
            case R.id.capture3:
                startCameraActivity();
                break;

        }
    }

    private void calendarEvent() {
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", true);
        intent.putExtra("rrule", "FREQ=DAILY");
        intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
        intent.putExtra("title", "Enter Event Name Here");
        startActivity(intent);
    }


    protected void onPhotoTaken3() {
        _taken = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);

        try {
            ExifInterface exif = new ExifInterface(selectedImagePath);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            Log.v(TAG, "Orient: " + exifOrientation);

            int rotate = 0;

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }

            Log.v(TAG, "Rotation: " + rotate);

            if (rotate != 0) {

                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);

                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
            }

            // Convert to ARGB_8888, required by tess
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        } catch (IOException e) {
            Log.e(TAG, "Couldn't correct orientation: " + e.toString());
        }

        // _image.setImageBitmap( bitmap );

        Log.v(TAG, "Before baseApi");

        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(DATA_PATH, lang);
        baseApi.setImage(bitmap);

        recognizedText = baseApi.getUTF8Text();
        baseApi.end();

        if (lang.equalsIgnoreCase("eng")) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }

        recognizedText = recognizedText.trim();
        Toast.makeText(view.getContext(), recognizedText, Toast.LENGTH_LONG).show();
        //GATHER INSTRUCTOR NAME////////////////////////////////////
        boolean stop = false;
        int spaceCount = 0;
        int dashCount = 0;

        recognizedText = recognizedText.toLowerCase();
        int fInstructor = recognizedText.indexOf("instructor");
        if (fInstructor == -1) {
            printOCR.setText(printOCR.getText() + "Instructor not found. \n");
        } else {
            StringBuffer gather = new StringBuffer();
            char[] testChar = recognizedText.toCharArray();
            for (int i = fInstructor; i < recognizedText.length() - fInstructor; i++) {
                if (testChar[i] == ' ') {
                    spaceCount++;
                    if (spaceCount == 3) {
                        break;
                    }
                }
                gather.append(testChar[i]);
            }
            spaceCount = 0;
            printOCR.setText(gather + "\n");
            //END GATHER INSTRUCTOR NAME////////////////////////////////
        }
        int fPhone = recognizedText.indexOf("404");
        if (fPhone == -1) {
            printOCR.setText(printOCR.getText() + "phone not found \n");
        } else {
            StringBuffer gather = new StringBuffer();
            char[] testChar = recognizedText.toCharArray();
            for (int i = fPhone; i < recognizedText.length() - 2; i++) {
                if (testChar[i] == ' ') {
                    spaceCount++;
                    if (spaceCount == 2) {
                        for (int j = 0; j < 5; j++) {
                            gather.append(testChar[i]);
                            i++;
                        }
                        break;
                    }
                }
                gather.append(testChar[i]);
            }
            dashCount = 0;
            printOCR.setText(printOCR.getText() + "Phone: " + gather + "\n");
            //END GATHER INSTRUCTOR NAME////////////////////////////////
        }

        int fEmail = recognizedText.indexOf("mail");
        if (fEmail == -1) {
            printOCR.setText(printOCR.getText() + "email not found \n");
        } else {
            StringBuffer gatherEmail = new StringBuffer();
            char[] testChar = recognizedText.toCharArray();
            int spaceCountEmail = 0;
            for (int i = fEmail + 4; i < recognizedText.length(); i++) {
                if (testChar[i] == ' ') {
                    spaceCountEmail++;
                    if (spaceCountEmail == 2) {
                        break;
                    }
                }
                gatherEmail.append(testChar[i]);

            }
            printOCR.setText(printOCR.getText() + "E-mail: " + gatherEmail + "@student.gsu.edu" + "\n");
            //END GATHER INSTRUCTOR NAME////////////////////////////////
        }
        int fISBN = recognizedText.indexOf("isbn");
        if (fISBN == -1) {
            printOCR.setText(printOCR.getText() + "ISBN not found \n");
        } else {
            StringBuffer gather = new StringBuffer();
            char[] testChar = recognizedText.toCharArray();
            spaceCount = 0;
            for (int i = fISBN + 5; i < recognizedText.length(); i++) {
                if (spaceCount == 4) {
//                    for (int j = 0; j < 1; j++) {
//                        if(testChar[i] == ' '){
//
//                        }else
//                        gather.append(testChar[i]);
//                        i++;
//                    }
                    break;
                }
                if (testChar[i] == ' ') {
                    spaceCount++;
                }
                if (spaceCount > 0 && testChar[i] == ' ') {
                    gather.append("-");
                }

                if (!((char) testChar[i] == ' ')) {
                    gather.append(testChar[i]);
                }
            }


            String ISBN = gather.toString();
            dashCount = 0;
            printOCR.setText(printOCR.getText() + "ISBN: " + ISBN + "\n");
            //END GATHER INSTRUCTOR NAME////////////////////////////////
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    public static String getPath(final Context context, final Uri uri) {

        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 1) {
                Toast.makeText(view.getContext(), "im in here", Toast.LENGTH_LONG).show();

                if (null == data)
                    return;


                Uri selectedImageUri = data.getData();
                System.out.println(selectedImageUri.toString());
                // MEDIA GALLERY
                selectedImagePath = imgCapture.getPath(
                        getActivity(), selectedImageUri);
                Log.i("Image File Path", "" + selectedImagePath);
                System.out.println("Image Path =" + selectedImagePath);
                if (boolFour == true || boolImport4 == true) {
                    onPhotoTaken4();
                } else {
                    onPhotoTaken3();
                }
//        if(selectedImagePath!=null&&!selectedImagePath.equals(""))
//        {
//        uploadImageOnServer upImg = new uploadImageOnServer();
//        upImg.execute();
//        }
//        else
//        {
//        AlertDialogManager alDailog = new AlertDialogManager(getActivity(), "Image Upload", "Please Select Valid Image", 0);
//        alDailog.showDialog();
//        }


            }
        }

    }

    public String ISBN4(String s) {
        int spaceCount = 0;
        int fISBN = recognizedText.indexOf("ISBN");
        if (fISBN == -1) {
            printOCR.setText(printOCR.getText() + "ISBN not found \n");
        } else {
            StringBuffer gather = new StringBuffer();
            char[] testChar = recognizedText.toCharArray();
            spaceCount = 0;
            for (int i = fISBN; i < recognizedText.length() - 2; i++) {
                if (testChar[i] == ' ') {
                    spaceCount++;
                    if (spaceCount == 4) {
                        for (int j = 0; j < 5; j++) {
                            gather.append(testChar[i]);
                            i++;
                        }
                        break;
                    }
                }
                gather.append(testChar[i]);
            }
        }
        return s;
    }
}


