package com.example.plugactivity.permission;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yanzhenjie.permission.setting.Setting;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

/**
 * @author puyantao
 * @describe
 * @create 2020/2/27 11:05
 */
public class PermissionUtils {
    public PermissionUtils() {
    }

    @SuppressLint("WrongConstant")
    public static void requestPermission(final Context mcontext, final PermissionDialogBean dialogBean, final PermissionUtils.OnPermissionBack listener, final boolean needSetting, String... permissions) {
        AndPermission.with(mcontext).runtime().permission(permissions).rationale(new RuntimeRationale(dialogBean)).onGranted(new Action<List<String>>() {
            @Override
            public void onAction(List<String> permissions) {
                listener.onResult(true);
            }
        }).onDenied(new Action<List<String>>() {
            @Override
            public void onAction(@NonNull List<String> permissions) {
                if (AndPermission.hasAlwaysDeniedPermission(mcontext, permissions)) {
                    if (needSetting) {
                        PermissionUtils.showSettingDialog(mcontext, dialogBean, listener, permissions);
                    } else {
                        listener.onResult(false);
                    }
                } else {
                    listener.onResult(false);
                }

            }
        }).start();
    }

    @SuppressLint("WrongConstant")
    public static void requestPermission(final Context mcontext, final PermissionDialogBean dialogBean, final PermissionUtils.OnPermissionBack listener, String... permissions) {
        AndPermission.with(mcontext).runtime().permission(permissions).rationale(new RuntimeRationale(dialogBean)).onGranted(new Action<List<String>>() {
            @Override
            public void onAction(List<String> permissions) {
                listener.onResult(true);
            }
        }).onDenied(new Action<List<String>>() {
            @Override
            public void onAction(@NonNull List<String> permissions) {
                if (AndPermission.hasAlwaysDeniedPermission(mcontext, permissions)) {
                    PermissionUtils.showSettingDialog(mcontext, dialogBean, listener, permissions);
                } else {
                    listener.onResult(false);
                }

            }
        }).start();
    }

    public static void showSettingDialog(final Context context, PermissionDialogBean dialogBean, final PermissionUtils.OnPermissionBack listener, List<String> permissions) {
        (new AlertDialog.Builder(context)).setCancelable(false).setTitle(dialogBean.getSettingTitle()).setMessage(dialogBean.getSettingMsg()).setPositiveButton(dialogBean.getSettingRightTips(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PermissionUtils.setPermission(context, listener);
            }
        }).setNegativeButton(dialogBean.getSettingLeftTips(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.cancelDialog();
            }
        }).show();
    }

    @SuppressLint("WrongConstant")
    public static void setPermission(Context mcontext, final PermissionUtils.OnPermissionBack listener) {
        AndPermission.with(mcontext).runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        listener.comeBack();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        listener.comeBack();
                    }
                }).start();
    }

    public interface OnPermissionBack {
        void onResult(boolean var1);

        void cancelDialog();

        void comeBack();
    }
}
