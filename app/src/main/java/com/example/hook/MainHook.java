package com.example.hook;

import android.app.Activity;
import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainHook implements IXposedHookLoadPackage {
    private static final String TAG = "LSPatchHook";

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("android") || lpparam.packageName.equals("com.android.systemui")) {
            return;
        }

        // 1. Hook moveTaskToBack -> return false
        XposedHelpers.findAndHookMethod(
            Activity.class,
            "moveTaskToBack",
            boolean.class,
            new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d(TAG, "[!] Blocked moveTaskToBack call!");
                    return false;
                }
            }
        );

        // 2. Hook finishAffinity -> do nothing
        XposedHelpers.findAndHookMethod(
            Activity.class,
            "finishAffinity",
            new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d(TAG, "[!] Blocked finishAffinity call!");
                    return null;
                }
            }
        );

        // 3. Spoof Window Focus -> force true
        XposedHelpers.findAndHookMethod(
            Activity.class,
            "onWindowFocusChanged",
            boolean.class,
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Log.d(TAG, "[*] Original onWindowFocusChanged: " + param.args[0]);
                    param.args[0] = true;
                }
            }
        );
    }
}
