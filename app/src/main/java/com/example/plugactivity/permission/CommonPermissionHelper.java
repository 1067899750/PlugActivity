package com.example.plugactivity.permission;

/**
 * @author puyantao
 * @describe
 * @create 2020/2/27 11:14
 */

public class CommonPermissionHelper {
    public CommonPermissionHelper() {
    }

    public static PermissionDialogBean getCameraPermission() {
        PermissionDialogBean bean = new PermissionDialogBean();
        bean.setTitle("权限申请");
        bean.setLeftTips("取消");
        bean.setRightTips("确定");
        bean.setSettingTitle("相机无法访问");
        bean.setSettingMsg("如需获取相机权限,请去设置中开启权限，否则将无法正常使用");
        bean.setSettingLeftTips("取消");
        bean.setSettingRightTips("去设置");
        return bean;
    }

    public static PermissionDialogBean getPhonePermission() {
        PermissionDialogBean bean = new PermissionDialogBean();
        bean.setTitle("权限申请");
        bean.setLeftTips("取消");
        bean.setRightTips("确定");
        bean.setSettingTitle("无法获取设备编码");
        bean.setSettingMsg("该设备需要获取您的手机基本信息，请在设置中开启权限，否则将无法正常使用");
        bean.setSettingLeftTips("取消");
        bean.setSettingRightTips("去设置");
        return bean;
    }

    public static PermissionDialogBean getAddressPermission() {
        PermissionDialogBean bean = new PermissionDialogBean();
        bean.setTitle("权限申请");
        bean.setLeftTips("取消");
        bean.setRightTips("确定");
        bean.setSettingTitle("无法获取手机通讯录");
        bean.setSettingMsg("该设备需要获取您的手机通讯录，请在设置中开启权限，否则将无法正常使用");
        bean.setSettingLeftTips("取消");
        bean.setSettingRightTips("去设置");
        return bean;
    }

    public static PermissionDialogBean getCallPermission() {
        PermissionDialogBean bean = new PermissionDialogBean();
        bean.setTitle("权限申请");
        bean.setLeftTips("取消");
        bean.setRightTips("确定");
        bean.setSettingTitle("设置权限");
        bean.setSettingLeftTips("取消");
        bean.setSettingRightTips("去设置");
        return bean;
    }

    public static PermissionDialogBean getSDPermission() {
        PermissionDialogBean bean = new PermissionDialogBean();
        bean.setTitle("权限申请");
        bean.setLeftTips("取消");
        bean.setRightTips("确定");
        bean.setSettingTitle("存储无法访问");
        bean.setSettingMsg("该功能需读取您的存储，请在设置中开启权限，否则将无法正常使用");
        bean.setSettingLeftTips("取消");
        bean.setSettingRightTips("去设置");
        return bean;
    }

    public static PermissionDialogBean getGPSPermission() {
        PermissionDialogBean bean = new PermissionDialogBean();
        bean.setTitle("权限申请");
        bean.setLeftTips("取消");
        bean.setRightTips("确定");
        bean.setSettingTitle("GPS无法访问");
        bean.setSettingMsg("该功能需要获取您的GPS，请在设置中开启权限，否则将无法正常使用");
        bean.setSettingLeftTips("取消");
        bean.setSettingRightTips("去设置");
        return bean;
    }
}
