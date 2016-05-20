package launcher.qingcheng.cn.qingchenglauncher;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    public static  String  pack = "cn.qingchengfit.staffkit";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.findById(this,R.id.app_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent =MainActivity.this.getPackageManager().getLaunchIntentForPackage(pack);
                startActivity(intent);
            }
        });
        PackageInfo p = getApp(this,pack);

        try {
            ((TextView)ButterKnife.findById(this,R.id.name)).setText(getPackageManager().getApplicationLabel(p.applicationInfo));
            ((ImageView)ButterKnife.findById(this,R.id.icon)).setImageDrawable(getPackageManager().getApplicationIcon(pack));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        WallpaperManager wallpaperManager = WallpaperManager
                .getInstance(this);
        ((ImageView)ButterKnife.findById(this,R.id.background)).setImageDrawable(wallpaperManager.getDrawable());
    }


    public static List<PackageInfo> getAllApps(Context context) {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        //获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = (PackageInfo) paklist.get(i);
            //判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                apps.add(pak);
            }
        }
        return apps;
    }

    public static PackageInfo getApp(Context context,String packagename){
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

    @Override
    public void onBackPressed() {

    }
}
