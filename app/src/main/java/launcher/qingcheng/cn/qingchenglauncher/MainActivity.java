package launcher.qingcheng.cn.qingchenglauncher;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {


    public static String pack = "cn.qingchengfit.staffkit";
    public static String pack_trainer = "com.qingchengfit.fitcoach";
    public static String pack_mms = "com.android.mms";
    @Bind(R.id.background)
    ImageView background;
    @Bind(R.id.icon)
    ImageView icon;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.app_layout)
    LinearLayout appLayout;
    @Bind(R.id.msg_icon)
    ImageView msgIcon;
    @Bind(R.id.msg_name)
    TextView msgName;
    @Bind(R.id.msg_layout)
    LinearLayout msgLayout;
    @Bind(R.id.activity_main)
    RelativeLayout activityMain;
    @Bind(R.id.unread)
    TextView unread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        ButterKnife.findById(this, R.id.app_layout).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    Intent intent = new Intent();
//                    intent = MainActivity.this.getPackageManager().getLaunchIntentForPackage(pack);
//                    startActivity(intent);
//                } catch (Exception e) {
//                    Toast.makeText(MainActivity.this, "没有安装此软件", Toast.LENGTH_LONG).show();
//                }
//            }
//        });


        if (TextUtils.equals("manager", BuildConfig.FLAVOR)) {//管理员
            icon.setImageResource(R.drawable.ic_manager);
            msgLayout.setVisibility(View.GONE);
            name.setText("健身房管理");
            background.setImageResource(R.drawable.manager);
        } else if (TextUtils.equals("trainer", BuildConfig.FLAVOR)) {//教练

            icon.setImageResource(R.drawable.ic_trainer);
            msgLayout.setVisibility(View.VISIBLE);
            name.setText("健身教练助手");
            background.setImageResource(R.drawable.trainer);
        } else if (TextUtils.equals("student", BuildConfig.FLAVOR)) {//学员
            icon.setImageResource(R.drawable.ic_student);
            msgLayout.setVisibility(View.VISIBLE);
            name.setText("会员端页面");
            background.setImageResource(R.drawable.student);
        }

        PackageInfo p = getApp(this, pack);

//        try {
//            ((ImageView) ButterKnife.findById(this, R.id.msg_icon)).setImageDrawable(getPackageManager().getApplicationIcon(pack_mms));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        WallpaperManager wallpaperManager = WallpaperManager
//                .getInstance(this);
//        ((ImageView) ButterKnife.findById(this, R.id.background)).setImageDrawable(wallpaperManager.getDrawable());
        RxPermissions.getInstance(this)
                .request(Manifest.permission.READ_SMS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean){
                            int un = getNewSmsCount();
                            unread.setVisibility(un>0?View.VISIBLE:View.GONE);
                            if ( un>0){
                                unread.setText(un+"");
                            }
                        }
                    }
                })
        ;

        registerObserver();
    }

    @Override
    protected void onDestroy() {
        unregisterObserver();
        super.onDestroy();
    }

    public List<PackageInfo> getAllApps(Context context) {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        //获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = (PackageInfo) paklist.get(i);

            Log.d("hshs", pak.packageName + "   " + getPackageManager().getApplicationLabel(pak.applicationInfo));
            //判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                apps.add(pak);
            }
        }
        return apps;
    }

    public static PackageInfo getApp(Context context, String packagename) {
        PackageManager pManager = context.getPackageManager();
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = (PackageInfo) paklist.get(i);
            //判断是否为非系统预装的应用程序
            if (pak.applicationInfo.processName.endsWith(packagename)) {
                return pak;
            }
        }
        return null;
    }


    private ContentObserver newMmsContentObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange) {
            int mNewSmsCount = getNewSmsCount();
            if (mNewSmsCount >0 ){
                unread.setVisibility(View.VISIBLE);
                unread.setText(getNewSmsCount()+"");
            }else {
                unread.setVisibility(View.GONE);
            }

        }
    };

    private void registerObserver() {
        unregisterObserver();
        getContentResolver().registerContentObserver(Uri.parse("content://sms"), true,
                newMmsContentObserver);
        getContentResolver().registerContentObserver(Telephony.MmsSms.CONTENT_URI, true,
                newMmsContentObserver);
    }

    private synchronized void unregisterObserver() {
        try {
            if (newMmsContentObserver != null) {
                getContentResolver().unregisterContentObserver(newMmsContentObserver);
            }
            if (newMmsContentObserver != null) {
                getContentResolver().unregisterContentObserver(newMmsContentObserver);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onBackPressed() {

    }

    private int getNewSmsCount() {

        int result = 0;
        Cursor csr = getContentResolver().query(Uri.parse("content://sms"), null,
                "type = 1 and read = 0", null, null);
        if (csr != null) {
            result = csr.getCount();
            csr.close();
        }
        return result;
    }

    @OnClick({R.id.app_layout, R.id.msg_layout})
    public void onClick(View view) {
        Intent intent = new Intent();
        try {
            switch (view.getId()) {

                case R.id.app_layout:
                    if (TextUtils.equals("manager", BuildConfig.FLAVOR)) {//管理员
                        intent = MainActivity.this.getPackageManager().getLaunchIntentForPackage(pack);
                        startActivity(intent);

                    } else if (TextUtils.equals("trainer", BuildConfig.FLAVOR)) {//教练

                        intent = MainActivity.this.getPackageManager().getLaunchIntentForPackage(pack_trainer);
                        startActivity(intent);
                    } else if (TextUtils.equals("student", BuildConfig.FLAVOR)) {//学员

                        Uri uri = Uri.parse("http://fit00370.qingchengfit.cn/shop/8/welcome/");
                        Intent toWeb = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(toWeb);
                    }

                    break;
                case R.id.msg_layout:
                    Intent toMsg = MainActivity.this.getPackageManager().getLaunchIntentForPackage(pack_mms);
                    startActivity(toMsg);
                    break;
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "没有安装此软件", Toast.LENGTH_LONG).show();
        }


    }
}
