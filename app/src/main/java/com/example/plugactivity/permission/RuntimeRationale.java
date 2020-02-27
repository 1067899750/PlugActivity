package com.example.plugactivity.permission;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.util.List;


/**
 * @author puyantao
 * @describe
 * @create 2020/2/27 11:07
 */
public final class RuntimeRationale implements Rationale<List<String>> {
    private PermissionDialogBean permissionDialogBean;

    public RuntimeRationale(PermissionDialogBean bean) {
        this.permissionDialogBean = bean;
    }

    @Override
    public void showRationale(Context context, List<String> permissions, final RequestExecutor executor) {
        (new AlertDialog.Builder(context)).setCancelable(false).setTitle(this.permissionDialogBean.getTitle()).setMessage(this.permissionDialogBean.getMsg()).setPositiveButton(this.permissionDialogBean.getRightTips(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                executor.execute();
            }
        }).setNegativeButton(this.permissionDialogBean.getLeftTips(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                executor.cancel();
            }
        }).show();
    }
}
