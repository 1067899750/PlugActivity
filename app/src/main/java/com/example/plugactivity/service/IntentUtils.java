package com.example.plugactivity.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import java.io.File;
import java.util.List;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

/**
 * 用于android 7.0 file FileProvider intent 跳转需Uri
 */
public class IntentUtils {


    public static Uri getFileUri(Intent intent, Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri taskUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            return taskUri;
        } else {
            return Uri.fromFile(file);
        }
    }

    public static Uri getFileUri(Intent intent, Context context, String filePath) {
        return getFileUri(intent, context, new File(filePath));

    }

    /**
     * 申请权限
     *
     * @param intent
     * @param context
     * @param file
     * @return
     */
    public static Uri getFileUriWithPermission(Intent intent, Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri taskUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", file);
            List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                context.grantUriPermission(packageName, taskUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            return taskUri;
        } else {
            return Uri.fromFile(file);
        }
    }

    /**
     * 申请权限
     *
     * @param intent
     * @param context
     * @param filePath
     * @return
     */
    public static Uri getFileUriWithPermission(Intent intent, Context context, String filePath) {
        return getFileUriWithPermission(intent, context, new File(filePath));
    }

    /**
     * 拨打电话，去拨号界面
     *
     * @param context     上下文
     * @param phoneNumber 电话号码
     */
    public static void dialPhone(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

    /**
     * 单文件扫描
     *
     * @param context 上下文
     * @param path    文件路径
     */
    public static void scanFile(Context context, String path) {
        scanFile(context, new String[]{path});
    }

    /**
     * 多文件扫描
     *
     * @param context 上下文
     * @param paths   文件路径数组
     */
    public static void scanFile(Context context, String[] paths) {
        MediaScannerConnection.scanFile(context, paths, null, null);
    }

    /**
     * 安装应用
     *
     * @param context 上下文
     * @param file    安装包文件
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(getFileUri(context, file),
                "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION |
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        context.startActivity(intent);
    }

    /**
     * Android N 以后Intent跨应用传递file://URI会抛出FileUriExposedException
     *
     * @param context 上下文
     * @param file    文件
     * @return intent的data
     */
    private static Uri getFileUri(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    /**
     * 打开应用市场中对应应用页面
     *
     * @param context     上下文
     * @param packageName 应用包名
     */
    public static void startAppStore(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + packageName));
        context.startActivity(intent);
    }

    /**
     * 根据uri打开相应页面
     *
     * @param context 上下文
     * @param url     跳转的路径
     */
    public static void startBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    /**
     * 打开系统图片裁剪页面
     *
     * @param fragment    从Fragment中打开
     * @param src         原始图片文件
     * @param dest        裁剪以后图片文件
     * @param outputX     裁剪宽度
     * @param outputY     裁剪高度
     * @param requestCode If >= 0, this code will be returned in
     *                    onActivityResult() when the activity exits.
     */
    public static void startImageCrop(Fragment fragment, File src, File dest, int outputX, int outputY, int requestCode) {
        Context context = fragment.getContext();
        if (context == null) {
            return;
        }

        Intent intent = getImageCropIntent(context, src, dest, outputX, outputY);
        fragment.startActivityForResult(intent, requestCode);
    }


    /**
     * 打开系统图片裁剪页面
     *
     * @param activity    从Activity中打开
     * @param src         原始图片文件
     * @param dest        裁剪以后图片文件
     * @param outputX     裁剪宽度
     * @param outputY     裁剪高度
     * @param requestCode If >= 0, this code will be returned in
     *                    onActivityResult() when the activity exits.
     */
    public static void startImageCrop(Activity activity, File src, File dest, int outputX, int outputY, int requestCode) {
        Intent intent = getImageCropIntent(activity, src, dest, outputX, outputY);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取裁剪intent
     */
    private static Intent getImageCropIntent(Context context, File src, File dest, int outputX, int outputY) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(getFileUri(context, src), "image/*");
        intent.putExtra("crop", true);
        int aspectX = outputX;
        int aspectY = outputY;
        int mode = 0;
        while ((mode = aspectX % aspectY) != 0) {
            aspectX = aspectY;
            aspectY = mode;
        }
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);

        Uri outputUri = getFileUri(context, dest);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        grantUriPermission(context, intent, outputUri);
        return intent;
    }

    /**
     * 为intent的extra中的uri设置读写Uri权限
     */
    private static void grantUriPermission(Context context, Intent intent, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            List<ResolveInfo> resInfoList = context.getPackageManager()
                    .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                context.grantUriPermission(packageName, uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }
    }

    /**
     * 使用第三方应用打开文件。根据文件后缀区分MimeType，若无后缀则Type为null
     *
     * @param context 上下文
     * @param file    需打开的文件
     */
    public static void openFile(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(getFileUri(context, file), MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath())));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION |
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        context.startActivity(intent);
    }

    /**
     * 从Fragment中去拍照
     *
     * @param photo 照片文件
     */
    public static void takePhoto(Fragment fragment, File photo, int requestCode) {
        Context context = fragment.getContext();
        if (context == null) {
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = getFileUri(context, photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        grantUriPermission(context, intent, imageUri);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 从Activity中去拍照
     *
     * @param photo 照片文件
     */
    public static void takePhoto(Activity activity, File photo, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = getFileUri(activity, photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        grantUriPermission(activity, intent, imageUri);
        activity.startActivityForResult(intent, requestCode);
    }
}
