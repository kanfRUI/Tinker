package com.example.administrator.tinker;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

/**
 * init方法自动检查更新，checkUpdate方法手动检查更新
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 如果想更新so，可以将System.loadLibrary替换成Beta.loadLibrary
     */
 /*   static {
        Beta.loadLibrary("mylib");
    }*/

    private TextView tvCurrentVersion;
    private Button btnShowToast;
    private Button btnLoadPatch;
    private Button btnKillSelf;
    private Button btnLoadLibrary;
    private Button btnDownloadPatch;
    private Button btnUserPatch;
    private Button btnCheckUpdate;
    private Button btnLoadUpgradeInfo;
    private TextView tvUpgradeInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCurrentVersion = (TextView) findViewById(R.id.tvCurrentVersion);
        btnShowToast = (Button) findViewById(R.id.btnShowToast);
        btnShowToast.setOnClickListener(this);
        btnKillSelf = (Button) findViewById(R.id.btnKillSelf);
        btnKillSelf.setOnClickListener(this);
        btnLoadPatch = (Button) findViewById(R.id.btnLoadPatch);
        btnLoadPatch.setOnClickListener(this);
        btnLoadLibrary = (Button) findViewById(R.id.btnLoadLibrary);
        btnLoadLibrary.setOnClickListener(this);
        btnDownloadPatch = (Button) findViewById(R.id.btnDownloadPatch);
        btnDownloadPatch.setOnClickListener(this);
        btnUserPatch = (Button) findViewById(R.id.btnPatchDownloaded);
        btnUserPatch.setOnClickListener(this);
        btnCheckUpdate = (Button) findViewById(R.id.btnCheckUpdate);
        btnLoadUpgradeInfo = (Button) findViewById(R.id.btnLoadUpdateInfo);
        tvUpgradeInfo = (TextView) findViewById(R.id.tvUpgradeInfo);

        btnCheckUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Beta.checkUpgrade();
            }
        });

        btnLoadUpgradeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUpgradeInfo();
            }
        });
        tvCurrentVersion.setText("当前版本：" + getCurrentVersion(this));
    }


    /**
     * 根据应用patch包前后来测试是否应用patch包成功.
     *
     * 应用patch包前，提示"This is a bug class"
     * 应用patch包之后，提示"The bug has fixed"
     */
    public void testToast() {
        Toast.makeText(this, LoadBugClass.getBugString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShowToast:  // 测试热更新功能
                testToast();
                break;
            case R.id.btnKillSelf: // 杀死进程
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case R.id.btnLoadPatch: // 本地加载补丁测试
                Beta.applyTinkerPatch(getApplicationContext(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");
                break;
            case R.id.btnLoadLibrary: // 本地加载so库测试
               /* TestJNI testJNI = new TestJNI();
                testJNI.createANativeCrash();*/
                break;
            case R.id.btnDownloadPatch:
                Beta.downloadPatch();
                break;
            case R.id.btnPatchDownloaded:
                Beta.applyDownloadedPatch();
                break;
        }
    }


    /**
     * 获取当前版本.
     *
     * @param context 上下文对象
     * @return 返回当前版本
     */
    public String getCurrentVersion(Context context) {
        try {
            PackageInfo packageInfo =
                    context.getPackageManager().getPackageInfo(this.getPackageName(),
                            PackageManager.GET_CONFIGURATIONS);
            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;

            return versionName + "." + versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }
    @Override
    protected void onResume() {
        super.onResume();
//        loadUpgradeInfo();
        Beta.init(this,true);
    }

    private void loadUpgradeInfo() {
        if (tvUpgradeInfo == null)
            return;

        /***** 获取升级信息 *****/
        UpgradeInfo upgradeInfo = Beta.getUpgradeInfo();

        if (upgradeInfo == null) {
            tvUpgradeInfo.setText("无升级信息");
            return;
        }

        StringBuilder info = new StringBuilder();
        info.append("id: ").append(upgradeInfo.id).append("\n");
        info.append("标题: ").append(upgradeInfo.title).append("\n");
        info.append("升级说明: ").append(upgradeInfo.newFeature).append("\n");
        info.append("versionCode: ").append(upgradeInfo.versionCode).append("\n");
        info.append("versionName: ").append(upgradeInfo.versionName).append("\n");
        info.append("发布时间: ").append(upgradeInfo.publishTime).append("\n");
        info.append("安装包Md5: ").append(upgradeInfo.apkMd5).append("\n");
        info.append("安装包下载地址: ").append(upgradeInfo.apkUrl).append("\n");
        info.append("安装包大小: ").append(upgradeInfo.fileSize).append("\n");
        info.append("弹窗间隔（ms）: ").append(upgradeInfo.popInterval).append("\n");
        info.append("弹窗次数: ").append(upgradeInfo.popTimes).append("\n");
        info.append("发布类型（0:测试 1:正式）: ").append(upgradeInfo.publishType).append("\n");
        info.append("弹窗类型（1:建议 2:强制 3:手工）: ").append(upgradeInfo.upgradeType);

        tvUpgradeInfo.setText(info);
    }

}
