package com.gxf.liveplay.update;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.gxf.liveplay.LiveActivityRel;

import java.io.File;



public class DownloadAppUtils {
    private static final String TAG = DownloadAppUtils.class.getSimpleName();
    public static long downloadUpdateApkId = -1;//下载更新Apk 下载任务对应的Id
    public static String downloadUpdateApkFilePath;//下载更新Apk 文件路径

    /**
     * 通过浏览器下载APK包
     * @param context
     * @param url
     */
    public static void downloadForWebView(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 下载更新apk包
     * 权限:1,<uses-permission android:name="android.permission.DOWNLOAD_WITHOUT_NOTIFICATION" />
     * @param context
     * @param url
     */
    public static void downloadForAutoInstall(Context context, String url, String fileName, String title) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        try {
            Uri uri = Uri.parse(url);
            DownloadManager downloadManager = (DownloadManager) context
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            //在通知栏中显示
            request.setVisibleInDownloadsUi(true);
            request.setTitle(title);

            // VISIBILITY_VISIBLE:                   下载过程中可见, 下载完后自动消失 (默认)
           // VISIBILITY_VISIBLE_NOTIFY_COMPLETED:  下载过程中和下载完成后均可见
           // VISIBILITY_HIDDEN:                    始终不显示通知
            if (!UpdateAppUtils.showNotification)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);



            String filePath = null;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//外部存储卡
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            } else {
                Log.i(TAG,"没有SD卡");
                return;
            }

            downloadUpdateApkFilePath = filePath + File.separator + fileName;
            deleteFile(downloadUpdateApkFilePath);// 若存在，则删除
            Uri fileUri = Uri.fromFile(new File(downloadUpdateApkFilePath));
            request.setDestinationUri(fileUri);
            downloadUpdateApkId = downloadManager.enqueue(request);

        } catch (Exception e) {
            e.printStackTrace();
            downloadForWebView(context, url);
        }finally {

        }
    }


    private static boolean deleteFile(String fileStr) {
        File file = new File(fileStr);
        return file.delete();
    }
}
