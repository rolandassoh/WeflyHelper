package com.wedevgroup.weflyhelper.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wedevgroup.weflyhelper.R;
import com.wedevgroup.weflyhelper.activity.SettingsActivity;
import com.wedevgroup.weflyhelper.presenter.DataBasePresenter;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import dmax.dialog.SpotsDialog;

/**
 * Created by Obrina.KIMI on 1/31/2018.
 */

public class CacheImage {
    private String directoryName = "WeflyHelperCache";
    private String fileKey;
    private String fileExtension = ".png";
    private final String fileTempName  = "temp";
    private Context context;
    private boolean external;
    public AlertDialog dialog;
    private String stExternal = "";
    private View view;
    private OnMovingCompleteListener listener;
    private final  String TAG = getClass().getSimpleName();

    public CacheImage(Context context) {
        this.context = context;
        try {
            external = shouldUserExternal(context);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public CacheImage(@NonNull Context context, @NonNull View view) {
        this.context = context;
        this.view = view;
        try {
            external = shouldUserExternal(context);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean shouldUserExternal(final Context context) {

        boolean isInternal = Save.defaultLoadBoolean(Constants.PREF_PREFERE_STORAGE_IS_INTERNAL,context);
        // SD card removed
        if (!isInternal){
            if (!externalMemoryAvailable(context))
                return false;
        }
        return !isInternal;

    }

    public void onSDRemove(@NonNull final Context ctx){
        saveOnInternalNow(ctx);
        notifyOnMovingCompleteListener(false, false, false, true, false);
    }

    public CacheImage setFileKey(String fileName) {
        this.fileKey = fileName;
        return this;
    }

    public CacheImage setExternal(boolean external) {
        this.external = external;
        return this;
    }

    public CacheImage setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
        return this;
    }

    public boolean save(@NonNull String key,  Bitmap bitmapImage) {
        this.fileKey = key;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(createFile());
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean saveExternal( int key,  Bitmap bitmapImage, @NonNull String dir) throws Exception {
        FileOutputStream fileOutputStream = null;
        File img = null;
        File directory = getExternalStorageDir(dir);
        try {
            if(!directory.exists() && !directory.mkdirs()){
                Log.v(Constants.APP_NAME, TAG + "Error creating directory " + directory);
            }
             img = new File(directory, fileKey + fileExtension);
            fileOutputStream = new FileOutputStream(img);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                    bitmapImage.recycle();
                    bitmapImage = null;
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

    }

    public boolean saveInternal( int key,  Bitmap bitmapImage, @NonNull String dir) {
        FileOutputStream fileOutputStream = null;
        File directory  = getInterDirectoryOnly(context, dir);
        try {
            if(!directory.exists() && !directory.mkdirs()){
                Log.v(Constants.APP_NAME, TAG + "Error creating directory " + directory);
            }
            File img = new File(dir, fileKey + fileExtension);
            fileOutputStream = new FileOutputStream(img);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

    }

    public boolean deleteFile(@NonNull String key){
        this.fileKey = key;
        File file = createFile();
        boolean isDone = file.delete();
        return isDone;
    }

    public Bitmap load(@NonNull String key) {
        this.fileKey = key;
        FileInputStream inputStream = null;

        try {
            // tragic logic created OOME, but we can blame it on lack of memory
            inputStream = new FileInputStream(createFile());
            return BitmapFactory.decodeStream(inputStream);
        } catch(OutOfMemoryError e) {
            // but what the hell will you do here :)
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            // get ready to be fired by your boss
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Contener loadImgAndFile(@NonNull String key){
        Contener ctn = new Contener();
        this.fileKey = key;
        FileInputStream inputStream = null;
        try {
            File f = createFile();
            inputStream = new FileInputStream(f);

            ctn.setBitmap(BitmapFactory.decodeStream(inputStream));
            ctn.setImgFile(f);
            return ctn;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean saveTemp(@NonNull Bitmap bp){
        // file is Ecrase if is exist
        return save(fileTempName, bp);
    }

    public boolean saveFromTemp(@NonNull final String key){
        Bitmap bmp = load(fileTempName);
        if (bmp!= null){
            boolean isTempDel = false;
            isTempDel = deleteFile(fileTempName);
            if (isTempDel){
                if (save(key, bmp)){
                    return true;
                }
            }
        }
        return false;

    }

    public boolean isInternalSpaceEnough(@NonNull Bitmap bmp){
        long imageSize = byteSizeOf(bmp);
        return isMemorySizeAvailableAndroid(imageSize, false);
    }



    @NonNull
    private File createFile() {
        File directory;
        File img;
        if(external){
            directory = getExternalStorageDir(directoryName);
        }
        else {
            directory = getInterDirectoryOnly(context, directoryName);
        }
        if(!directory.exists() && !directory.mkdirs()){
            Log.v(Constants.APP_NAME, TAG + "Error creating directory " + directory);
        }

        img = new File(directory, fileKey + fileExtension);


        return img ;
    }

//    private @NonNull File getInternalDirectory() {
//        //String internal = Environment.getDataDirectory() + "/WeflyHelperCache/";
//        String internal = "/storage/emulated/0/" + "/WeflyHelperCache/";
//        return new File(internal);
//    }

    private @NonNull File getInterDirectoryOnly(@NonNull final Context ctx, @NonNull String dName) {
        File  directory = ctx.getDir(directoryName, Context.MODE_PRIVATE);
        //File directory = new File(ctx.getFilesDir(), dName);
//        File[] files = context.getFilesDir().listFiles();
//        if(files != null)
//            for(File file : files) {
//                Log.v(Constants.APP_NAME, TAG + "FILE NAME " + file.getAbsolutePath());
//            }
        return directory;
    }

    @NonNull
    public String getImagePath(@NonNull String key) {
        this.fileKey = key;
        File directory = null;
        File imgFile = null;
        try {
            if(external){
                directory = getExternalStorageDir(directoryName);
            }
            else {
                directory = getInterDirectoryOnly(context, directoryName);
            }
            if(!directory.exists() && !directory.mkdirs()){
                Log.v(Constants.APP_NAME, TAG + "Error creating directory " + directory);
            }
            imgFile = new File(directory, fileKey + fileExtension);

            return imgFile.getAbsolutePath();
        }catch (Exception e){
            e.printStackTrace();
        }

        String err = "";
        return err;
    }

    private @NonNull File getExternalStorageDir (@NonNull String albumName) {
        String externalPath = "";
        if (stExternal != null && (!stExternal.trim().equals(""))){
            externalPath = stExternal+ File.separator + albumName + File.separator;

        }else{
            externalPath = Environment.getExternalStorageDirectory().getPath() + File.separator + albumName + File.separator;
        }

        return new File(externalPath);

    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private static void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();

    }

    public void onSDmissing(){
        if (context != null && view != null){
            Utils.showToast(context, R.string.err_storage_unavaible, view);
            saveOnInternalNow(context);
            notifyOnMovingCompleteListener(false, false, false, false, true);
        }
    }

    private static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    private static boolean isMemorySizeAvailableAndroid(long download_bytes, boolean isExternalMemory) {
        boolean isMemoryAvailable = false;
        long freeSpace = 0;

        // if isExternalMemory get true to calculate external SD card available size
        if(isExternalMemory){
            try {
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                freeSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
                if(freeSpace > download_bytes){
                    isMemoryAvailable = true;
                }else{
                    isMemoryAvailable = false;
                }
            } catch (Exception e) {e.printStackTrace(); isMemoryAvailable = false;}
        }else{
            // find phone available size
            try {
                StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
                freeSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
                if(freeSpace > download_bytes){
                    isMemoryAvailable = true;
                }else{
                    isMemoryAvailable = false;
                }
            } catch (Exception e) {e.printStackTrace(); isMemoryAvailable = false;}
        }

        return isMemoryAvailable;
    }

    public void onMoveToExternal(){
        // get Directory Size
        if (context != null && view != null){
            if (externalMemoryAvailable(context)){

                if ((!isFolderExist(getInterDirectoryOnly(context, directoryName))) || isFolderEmpty(getInterDirectoryOnly(context, directoryName))){
                    Utils.showToast(context, R.string.err_folder_empty, view);
                    notifyOnMovingCompleteListener(false, false, true, false, false);
                }else{
                    final File internal = getInterDirectoryOnly(context, directoryName);
                    final File extDirectory = getExternalStorageDir(directoryName);
                    long dirSize = getFileSize(internal);

                    if (isMemorySizeAvailableAndroid(dirSize, true)){
                        // Copy to External
                        if(!internal.exists() && !internal.mkdirs()){
                            Utils.showToast(context, R.string.err_storage_directory, view);
                        }else {
                            try {
                                dialog = new SpotsDialog(context, R.style.SpotAlertDialog);
                                dialog.show();
                                TextView title = (TextView) dialog.findViewById(R.id.dmax_spots_title);
                                title.setText(context.getString(R.string.loading_moving));
                                new AsyncTask<Void, Void, Boolean>(){
                                    @Override
                                    protected Boolean doInBackground(Void... voids) {
                                        try {

                                            int dbsize = DataBasePresenter.getInstance().getParcelleTotalItems();

                                            saveOnExternal(dbsize);
                                            // Say Done
                                            return true;

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return false;
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean isOk) {
                                        try {
                                            dialog.dismiss();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        if (isOk)
                                            Utils.showToast(context, R.string.succes_storage, view);
                                        else
                                            Utils.showToast(context, R.string.err_storage, view);
                                        notifyOnMovingCompleteListener(isOk, true, false, false, false);
                                    }
                                }.execute();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }else
                        Utils.showToast(context, R.string.err_storage_space, view);
                }

            }else
                Utils.showToast(context, R.string.err_storage_unavaible, view);
        }

    }


    public void onMoveToInternal(){
        // get Directory Size
        if (context != null && view != null){
            final File extDirectory = getExternalStorageDir(directoryName);
            final File internalDir  = getInterDirectoryOnly(context, directoryName);
            long dirSize = getFileSize(extDirectory);

            if (isMemorySizeAvailableAndroid(dirSize, false)){
                // Copy to External
                if(!internalDir.exists() && !internalDir.mkdirs()){
                    Utils.showToast(context, R.string.err_storage_directory, view);
                }else {
                    try {
                        dialog = new SpotsDialog(context, R.style.SpotAlertDialog);
                        dialog.show();
                        TextView title = (TextView) dialog.findViewById(R.id.dmax_spots_title);
                        title.setText(context.getString(R.string.loading_moving));
                        new AsyncTask<Void, Void, Boolean>(){
                            @Override
                            protected Boolean doInBackground(Void... voids) {
                                try {
                                    int dbsize = DataBasePresenter.getInstance().getParcelleTotalItems();

                                    saveOnInternal(dbsize);
                                    // Say Done
                                    return true;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }

                            @Override
                            protected void onPostExecute(Boolean isOk) {
                                try {
                                    dialog.dismiss();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                if (isOk)
                                    Utils.showToast(context, R.string.succes_storage, view);
                                else
                                    Utils.showToast(context, R.string.err_storage, view);
                                notifyOnMovingCompleteListener(isOk, false, false, false, false);
                            }
                        }.execute();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }else
                Utils.showToast(context, R.string.err_storage_space, view);
        }
    }



    private static long getFileSize(final File file) {
        if (file == null || !file.exists())
            return 0;
        if (!file.isDirectory())
            return file.length();
        final List<File> dirs = new LinkedList<>();
        dirs.add(file);
        long result = 0;
        while (!dirs.isEmpty()) {
            final File dir = dirs.remove(0);
            if (!dir.exists())
                continue;
            final File[] listFiles = dir.listFiles();
            if (listFiles == null || listFiles.length == 0)
                continue;
            for (final File child : listFiles) {
                result += child.length();
                if (child.isDirectory())
                    dirs.add(child);
            }
        }
        return result;
    }

    public boolean externalMemoryAvailable(final Context context) {
        File[] externalStorageFiles = ContextCompat.getExternalFilesDirs(context, null);
        String[] storagepath = new String[externalStorageFiles.length];
        boolean isWrongSDFound = false;
        try {
            if (storagepath.length > 1){
                for (int i = 0; i < storagepath.length; i++){
                    storagepath[i] = externalStorageFiles[i].getAbsolutePath();
                    if (storagepath[i].contains("sdcard"))
                        isWrongSDFound = true;
                    if(!storagepath[i].contains("emulated")){
                        stExternal = storagepath[i];
                    }

                }
                if (isWrongSDFound){
                    notifyOnMovingCompleteListener(false, false, false, false, true);
                    return false;
                }else
                    return true;
            }else
                return false;
        }catch (Exception e){
            e.printStackTrace();
        }


        return false;
    }

    public void setOnMovingCompleteListener(@NonNull OnMovingCompleteListener listener) {
        this.listener = listener;

    }

    public static interface OnMovingCompleteListener {
        void onMovingError();
        void onMovingSucces(boolean fromInternalToExternal);
        void onFolderEmpty();
        void onSDRemove();
        void onSDNotFound();
    }

    private void notifyOnMovingCompleteListener(boolean isDone, boolean toExternal, boolean isFolderEmpty, boolean isSDRemoved, boolean isSdNotFound) {
        if (listener != null ){
            if (isSdNotFound)
                listener.onSDNotFound();
            else if (isSDRemoved){
                listener.onSDRemove();
            }else if (isFolderEmpty){
                listener.onFolderEmpty();
            }
            else{
                if (isDone)
                    listener.onMovingSucces(toExternal);
                else
                    listener.onMovingError();
            }
        }



    }

    public static long getFolderSize(Context context, File file) {
        File directory = readlink(file); // resolve symlinks to internal storage
        String path = directory.getAbsolutePath();
        Cursor cursor = null;
        long size = 0;
        try {
            cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                    new String[]{MediaStore.MediaColumns.SIZE},
                    MediaStore.MediaColumns.DATA + " LIKE ?",
                    new String[]{path + "/%/%"},
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    size += cursor.getLong(0);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return size;
    }


    /**
     * Canonicalize by following all symlinks. Same as "readlink -f file".
     *
     * @param file
     *     a {@link File}
     * @return The absolute canonical file
     */
    public static File readlink(File file) {
        File f;
        try {
            f = file.getCanonicalFile();
        } catch (IOException e) {
            return file;
        }
        if (f.getAbsolutePath().equals(file.getAbsolutePath())) {
            return f;
        }
        return readlink(f);
    }


    public @NonNull String getCacheSize(){
        String size = "";
        if (context != null){
            File directory;
            if (external){
                directory = getExternalStorageDir(directoryName);
            }else
                directory = getInterDirectoryOnly(context, directoryName);

            long directorySize = getFileSize(directory);
            size =  Formatter.formatFileSize(context, directorySize);

        }
        return size;

    }

    public void cleanCache(){
        if (context != null){
            try {
                if (external){
                    deleteRecursive(getExternalStorageDir(directoryName));
                }else {
                    deleteRecursive(getInterDirectoryOnly(context, directoryName));
                }
                Log.v(Constants.APP_NAME, TAG + "cleanCache DONE");

            }catch (Exception e){
                e.printStackTrace();
                Log.v(Constants.APP_NAME, TAG + "cleanCache FAILED");
            }
        }
    }

    public boolean isFolderEmpty(@NonNull final File src){
        try {
            String files[] = src.list();
            int filesLength = files.length;
            return filesLength == 0;
        }catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }

    private void saveOnInternalNow(Context ctx){
        try {
            Save.defaultSaveBoolean(Constants.PREF_PREFERE_STORAGE_IS_INTERNAL,true, ctx);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveOnExternal(int max){
        for (int i = 0; i < max ; i++ ){
            try {
                File fToDelete    = null;
                Contener contener = null;
                // load
                contener = loadImgAndFile(String.valueOf(i));
                if (contener != null){
                    // save
                    if (saveExternal(i, contener.getBitmap(), directoryName)){
                        fToDelete = contener.getImgFile();
                        if (fToDelete!= null){
                            // Clearn old
                            fToDelete.delete();
                        }
                    }

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    private void saveOnInternal(int max){

        for (int i = 0; i < max ; i++ ){
            try {
                // load
                Bitmap item = load(String.valueOf(i));
                // save
                saveInternal(i, item, directoryName);

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private boolean isFolderExist(@NonNull final File directory){
        return directory.exists();
    }


    final class Contener {
        private File imgFile;
        private Bitmap bitmap;

        public @Nullable File getImgFile() throws Exception  {
            return imgFile;
        }

        public void setImgFile(File imgFile) {
            this.imgFile = imgFile;
        }

        public @Nullable Bitmap getBitmap() throws Exception {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }

}
