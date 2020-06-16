package com.jianbao.jamboble.utils.permissions;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.fragment.app.FragmentActivity;

import com.jianbao.jamboble.utils.LogUtils;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 权限检查工具类
 */
public class PermissionsUtil {
    private static final Map<String, String> permissionDescs = new HashMap<String, String>();
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    static {
        permissionDescs.put(Manifest.permission.CAMERA, "相机权限");
        permissionDescs.put(Manifest.permission.ACCESS_COARSE_LOCATION, "位置信息权限");
        permissionDescs.put(Manifest.permission.CALL_PHONE, "电话权限");
        permissionDescs.put(Manifest.permission.READ_CONTACTS, "通讯录权限");
        permissionDescs.put(Manifest.permission.RECORD_AUDIO, "麦克风权限");
        permissionDescs.put(Manifest.permission.WRITE_SETTINGS, "系统设置权限");
        permissionDescs.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "写入存储空间权限");
        permissionDescs.put(Manifest.permission.READ_EXTERNAL_STORAGE, "读取存储空间权限");
    }

    public static void request(final FragmentActivity fragmentActivity, final OnPermissionGranted permissionGranted, final String... permission) {
        new RxPermissions(fragmentActivity)
                .request(permission)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            permissionGranted.onPermissionGranted(fragmentActivity);
                        } else {
                            LogUtils.e("授权失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public static void requestDonotHandler(final FragmentActivity fragmentActivity, final OnPermissionDonotHandler permissionDonotHandler, final String... permission) {
        new RxPermissions(fragmentActivity)
                .request(permission)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        permissionDonotHandler.onPermissionDonotHandler(fragmentActivity, aBoolean);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //申请的权限为非必须
    public static void requestMustNot(final FragmentActivity fragmentActivity, final OnPermissionGranted permissionGranted, final String... permission) {
        new RxPermissions(fragmentActivity)
                .request(permission)
                .subscribe(new io.reactivex.Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        permissionGranted.onPermissionGranted(fragmentActivity);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public static void startSystemSetting(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName())); // 根据包名打开对应的设置界面
        context.startActivity(intent);
    }

    public interface OnPermissionGranted {
        void onPermissionGranted(Context context);
    }

    public interface OnPermissionDonotHandler {
        void onPermissionDonotHandler(Context context, boolean granted);
    }

}
