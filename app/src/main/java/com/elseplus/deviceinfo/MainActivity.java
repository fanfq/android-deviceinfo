package com.elseplus.deviceinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bun.miitmdid.supplier.IdSupplier;
import com.elseplus.deviceinfo.util.AdvertisingIdClient;
import com.elseplus.deviceinfo.util.DeviceIdUtils;
import com.elseplus.deviceinfo.util.DeviceInfo;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView info;
    Button copy;
    // 是否允许了权限
    private boolean isPermissionAllowed;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        info = findViewById(R.id.info);
        info.setText("");

        ////////////////////////////////////////////////////////////////////////////////////////////
        //获取权限
        ////////////////////////////////////////////////////////////////////////////////////////////
        if (XXPermissions.isHasPermission(this, Permission.READ_PHONE_STATE)) {
            isPermissionAllowed = true;
        } else {
            XXPermissions.with(this)
                    .permission(Permission.READ_PHONE_STATE)
                    .request(new OnPermission() {
                        @Override
                        public void hasPermission(List<String> granted, boolean isAll) {
                            isPermissionAllowed = true;
                        }

                        @Override
                        public void noPermission(List<String> denied, boolean quick) {
                            isPermissionAllowed = false;
                            Toast.makeText(MainActivity.this, "您禁止了该权限，无法获取设备相关标识", Toast.LENGTH_SHORT).show();
                        }
                    });
        }


        ////////////////////////////////////////////////////////////////////////////////////////////
        //获取 google advertisingId
        ////////////////////////////////////////////////////////////////////////////////////////////
        new Thread(new Runnable() {
            public void run() {
                try {
                    AdvertisingIdClient.AdInfo adInfo = AdvertisingIdClient
                            .getAdvertisingIdInfo(MainActivity.this);
                    String advertisingId = adInfo.getId();
                    boolean optOutEnabled = adInfo.isLimitAdTrackingEnabled();
                    Log.i("ainfo_advertisingId", "" + advertisingId);
                    Log.i("ainfo_optOutEnabled", "" + optOutEnabled);
                    DeviceInfo deviceInfo  = new DeviceInfo(MainActivity.this);


                    Log.i("deviceInfo",deviceInfo.toPostString());
                    info.append("google advertisingId:"+advertisingId+"\n\n");

                    info.append(deviceInfo.toString()+"\n\n");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //mHandler.sendEmptyMessage(HANDEL_ADID);
            }
        }).start();


        if (isPermissionAllowed) {
            try {
                String imei = DeviceIdUtils.getIMEI(mContext);
                Log.i("IMEI",imei);
                info.append("imei:"+imei+"\n\n");
                //tvDeviceId.setText("IMEI：" + imei);
            } catch (SecurityException e) {
                e.printStackTrace();
                info.append("imei:"+"N/A"+"\n\n");
                Toast.makeText(mContext, "获取IMEI失败", Toast.LENGTH_SHORT).show();
            }
        }

        if (isPermissionAllowed) {
            try {
                String serial = DeviceIdUtils.getSerial();
                Log.i("serial",serial);
                info.append("serial:"+serial+"\n\n");
                //tvDeviceId.setText("设备序列号：" + serial);
            } catch (SecurityException e) {
                e.printStackTrace();
                info.append("serial:"+"N/A"+"\n\n");
                Toast.makeText(mContext, "获取设备序列号失败", Toast.LENGTH_SHORT).show();
            }
        }

        String macAddress = DeviceIdUtils.getMacAddress();
        String androidId = DeviceIdUtils.getAndroidId(mContext);
        //Log.i("macAddress",macAddress);
        Log.i("androidId",androidId);
        info.append("macAddress:"+macAddress+"\n\n");
        info.append("androidId:"+androidId+"\n\n");

        DeviceIdUtils.getSupplierDeviceId(mContext, new DeviceIdUtils.OnSupplierDeviceIdListener() {
            @Override
            public void onSuccess(final IdSupplier idSupplier) {
                // 这里的回调可能不是在主线程中的，如果要更新UI需要切回到主线程
                // 个人测试结果：真机上支持获取补充设备标识，回调执行在工作线程；Android Studio创建的模拟器不支持获取补充设备标识，回调执行在主线程
                // 因此这里保险起见还是切回主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder sb = new StringBuilder();
                        sb.append("OAID：").append(idSupplier.getOAID()).append("\n");
                        sb.append("VAID：").append(idSupplier.getVAID()).append("\n");
                        sb.append("AAID：").append(idSupplier.getAAID()).append("\n");
                        //tvDeviceId.setText(sb.toString());
                        Log.i("OAID",idSupplier.getOAID());
                        Log.i("VAID",idSupplier.getVAID());
                        Log.i("AAID",idSupplier.getAAID());
                        info.append("OAID:"+idSupplier.getOAID()+"\n\n");
                        info.append("VAID:"+idSupplier.getVAID()+"\n\n");
                        info.append("AAID:"+idSupplier.getAAID()+"\n\n");

                    }
                });
            }

            @Override
            public void onFailed(String message) {
                Log.e("x", "获取补充设备标识失败，失败原因为：" + message);
            }
        });

        DeviceIdUtils.getDeviceId(mContext, new DeviceIdUtils.OnDeviceIdListener() {
            @Override
            public void onSuccess(final String deviceId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("通用设备标识",deviceId);
                        info.append("deviceId:"+deviceId+"\n\n");
                       // tvDeviceId.setText("通用设备标识：" + deviceId);
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        Log.i("v",v.getId()+"");
        if(v.getId() == R.id.copy){
            Log.i("c",info.getText().toString());
            ClipboardManager clipboard = (ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label",info.getText().toString());
            clipboard.setPrimaryClip(clip);
        }
    }
}