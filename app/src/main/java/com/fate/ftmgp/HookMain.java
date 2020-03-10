package com.fate.ftmgp;

import android.content.Context;
import java.io.File;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage {
    private static final String TAG = "Fuck";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        String libraryDir = lpparam.appInfo.nativeLibraryDir;
        if (libraryDir.contains("/data/app/")) { //过滤掉一些系统应用
            if (isTprtGame(libraryDir)) {
                final String path = "/sdcard/" + lpparam.packageName;
                createDir(path);
                XposedHelpers.findAndHookMethod("com.tencent.tpshell.TPShellApplication", lpparam.classLoader, "initialize", String.class, String.class, String.class, String.class, Context.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.args[2] = path;//nativeLibraryDir指向其他路径 即可过掉so文件校验
                        param.args[3] = path; //指向其他地方....可过apk校验
                    }

                });
            }
        }
    }

    //判断是否含有tprt.so
    public boolean isTprtGame(String libraryDir) {
        File nativeLibDir = new File(libraryDir);
        if (nativeLibDir.isDirectory()) {
            File[] files = nativeLibDir.listFiles();
            for (File file : files
            ) {
                if (file.getName().contains("tprt")) {
                    return true;
                }
            }
        }
        return false;
    }

    //创建文件夹
    public void createDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }
}
