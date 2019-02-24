package com.craftingapps.status.saver.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.craftingapps.status.saver.Application;
import com.craftingapps.status.saver.R;
import com.craftingapps.status.saver.helper.AppConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;


public class Utils {

    public static Utils utilClass;

    public static Utils getInstance() {
        if (utilClass == null) {
            utilClass = new Utils();
        }
        return utilClass;
    }


    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) Application.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }


    public static String getDateTime() {
        Date date = new Date();
        return String.valueOf(DateFormat.format("HH:mm:ss yyyy-MM-dd", date.getTime())).trim();
    }


    public void saveMedia(Context context, String fileName, String filePath) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + AppConstants.SAVE_FOLDER_NAME;
        File dir = new File(path);
        boolean isDirectoryCreated = dir.exists();
        if (!isDirectoryCreated) {
            isDirectoryCreated = dir.mkdir();
        }
        if (isDirectoryCreated) {
            Log.d("Folder", "Already Created");
        }

        final File sourceFile = new File(filePath);
        String destPath = Environment.getExternalStorageDirectory().getAbsolutePath() + AppConstants.SAVE_FOLDER_NAME;
        File destFile = new File(destPath + fileName);

        if (!destFile.exists()) {
            try {
//            FileUtils.copyFileToDirectory(file,destFile);
                copyFileUsingChannel(sourceFile, destFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            MediaScannerConnection.scanFile(context, new String[]{destPath + fileName}, new String[]{"*/*"}, new MediaScannerConnection.MediaScannerConnectionClient() {
                public void onMediaScannerConnected() {
                }

                public void onScanCompleted(String path, Uri uri) {
                    Log.d("path: ", path);
                }
            });
            Toast.makeText(context, context.getString(R.string.added_in_favorite), Toast.LENGTH_SHORT).show();
            //  AdsManager.getInstance().showInterstitialAd();
        } else {
            Toast.makeText(context, context.getString(R.string.already_saved), Toast.LENGTH_SHORT).show();
        }

    }


    public void deleteMedia(Context context, String filePath) {
        File file = new File(filePath);
        file.delete();

        if (file.exists()) {
            try {
                file.getCanonicalFile().delete();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (file.exists()) {
                context.getApplicationContext().deleteFile(file.getName());
            }
        }
    }


    public void playVideo(Context context, String filePath) {
        File file = new File(filePath);
        Uri uriForFile = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uriForFile, "video/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(intent);
    }


    public void shareImage(Context context, String filePath, String fileName) {
        File file = new File(filePath);
        Uri uriForFile = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");

        shareIntent.putExtra(Intent.EXTRA_STREAM, uriForFile);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "I am sharing this by Status Saver, Now you can  save your contact's status without taking screenshots, download this now and enjoy." +"" + Uri.parse(AppConstants.GOOGLE_PLAY_URL + AppConstants.CONTEXT.getPackageName()));

        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_with)));
    }


    public void shareVideo(Context context, String filePath, String fileName) {
        File file = new File(filePath);
        Uri uriForFile = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("video/*");

        shareIntent.putExtra(Intent.EXTRA_STREAM, uriForFile);
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.shared_from) + " " + context.getString(R.string.app_name));

        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_with)));
    }


    public void shareApp(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.share_title));
        intent.putExtra(Intent.EXTRA_TEXT, Uri.parse(AppConstants.GOOGLE_PLAY_URL + context.getPackageName()));
        intent.setType("text/plain");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "share"));
    }


    public void openAppOnStore(Context context, String appPackageName) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.GOOGLE_PLAY_URL + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.Market_URL + appPackageName)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void moreApps(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.Google_Play_STORE_URL + AppConstants.Store_Account)));
        } catch (android.content.ActivityNotFoundException anfe) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.Market_URL + AppConstants.Store_Account)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void copyFileUsingChannel(File source, File dest) throws IOException {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } finally {
            try {
                sourceChannel.close();
                destChannel.close();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public static Intent getLikeUsIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.GOOGLE_PLAY_URL + AppConstants.CONTEXT.getPackageName()));
    }

}
