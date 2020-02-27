package com.example.plugactivity.permission;

import java.io.Serializable;
/**
 *
 * @description
 * @author puyantao
 * @date 2020/2/27 11:07
 */
public class PermissionDialogBean implements Serializable {
    private String title = "申请权限";
    private String msg;
    private String leftTips = "取消";
    private String rightTips = "确定";
    private String settingTitle = "设置权限";
    private String settingMsg;
    private String settingLeftTips = "取消";
    private String settingRightTips = "设置";

    public PermissionDialogBean() {
    }

    public String getSettingTitle() {
        return this.settingTitle;
    }

    public void setSettingTitle(String settingTitle) {
        this.settingTitle = settingTitle;
    }

    public String getSettingMsg() {
        return this.settingMsg;
    }

    public void setSettingMsg(String settingMsg) {
        this.settingMsg = settingMsg;
    }

    public String getSettingLeftTips() {
        return this.settingLeftTips;
    }

    public void setSettingLeftTips(String settingLeftTips) {
        this.settingLeftTips = settingLeftTips;
    }

    public String getSettingRightTips() {
        return this.settingRightTips;
    }

    public void setSettingRightTips(String settingRightTips) {
        this.settingRightTips = settingRightTips;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getLeftTips() {
        return this.leftTips;
    }

    public void setLeftTips(String leftTips) {
        this.leftTips = leftTips;
    }

    public String getRightTips() {
        return this.rightTips;
    }

    public void setRightTips(String rightTips) {
        this.rightTips = rightTips;
    }
}
