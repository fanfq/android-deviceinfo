package com.elseplus.deviceinfo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.Locale;
import java.util.UUID;

/**
 * Created by fred on 2016/12/21.
 */

public class DeviceInfo {

    private String res;
    private String channel;
    private String uid;
    private String app_name;
    private String imei;
    private String sim_serial_number;
    private String android_id;
    private String device_uuid;
    private String lang;
    private String lang_code;
    private String country;
    private String operator;
    private String carrier;
    private String network;
    private String device_finger_print_id;
    private String brand;
    private String device;
    private String sdk;
    private String model;
    private String device_type;
    private String mac;

    private Context context;
    private String url;
    private TelephonyManager tm;

    public DeviceInfo(Context context, String url, String res, String uid, String app_name, String channel) {
        this.context = context;
        this.url = url;
        this.res = res;
        this.uid = uid;
        this.app_name = app_name;
        this.channel = channel;
        this.init();
    }

    public DeviceInfo(Context context) {
        this.context = context;
        this.init();
    }

    public void init() {
        tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
//        this.setImei(tm.getDeviceId());
//        this.setSim_serial_number(tm.getSimSerialNumber());
        this.setAndroid_id(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        if (android_id != null && !android_id.equals("") && sim_serial_number != null && !sim_serial_number.equals("") && imei != null && !imei.equals("")) {
            this.setDevice_uuid(new UUID(android_id.hashCode(), ((long) imei.hashCode() << 32) | sim_serial_number.hashCode()).toString());
        }
        this.setLang(Locale.getDefault().getDisplayLanguage());
        this.setLang_code(Locale.getDefault().getLanguage());
        this.setCountry(Locale.getDefault().getCountry());
        this.setOperator(tm.getSimOperatorName());
        this.setCarrier(tm.getNetworkOperatorName());
        this.setNetwork(getNetwork(context));
        this.setDevice_finger_print_id(getUniquePsuedoID());
        this.setBrand(Build.BRAND);
        this.setDevice(Build.DEVICE);
        this.setSdk(Integer.toString(Build.VERSION.SDK_INT));
        this.setModel(Build.MODEL);
        this.setDevice_type(Build.TYPE);
        this.setMac(this.getMacAddress(context));
    }

    public String toString() {
        String str = "?res=%s&" +
                "uid=%s&" +
                "app_name=%s&" +
                "channel=%s&" +
                "imei=%s&" +
                "sim_serial_number=%s&" +
                "android_id=%s&" +
                "device_uuid=%s&" +
                "lang=%s&" +
                "lang_code=%s&" +
                "country=%s&" +
                "operator=%s&" +
                "carrier=%s&" +
                "network=%s&" +
                "device_finger_print_id=%s&" +
                "brand=%s&" +
                "device=%s&" +
                "sdk=%s&" +
                "model=%s&" +
                "device_type=%s&" +
                "mac=%s";
        String tmp = String.format(str, this.res,
                this.uid,
                this.app_name,
                this.channel,
                this.imei,
                this.sim_serial_number,
                this.android_id,
                this.device_uuid,
                this.lang,
                this.lang_code,
                this.country,
                this.operator,
                this.carrier,
                this.network,
                this.device_finger_print_id,
                this.brand,
                this.device,
                this.sdk,
                this.model,
                this.device_type,
                this.mac);

        return url + tmp;
    }

    public String toPostString() {
        String str = "&res=%s&" +
                "uid=%s&" +
                "app_name=%s&" +
                "channel=%s&" +
                "imei=%s&" +
                "sim_serial_number=%s&" +
                "android_id=%s&" +
                "device_uuid=%s&" +
                "lang=%s&" +
                "lang_code=%s&" +
                "country=%s&" +
                "operator=%s&" +
                "carrier=%s&" +
                "network=%s&" +
                "device_finger_print_id=%s&" +
                "brand=%s&" +
                "device=%s&" +
                "sdk=%s&" +
                "model=%s&" +
                "device_type=%s&" +
                "mac=%s";
        String tmp = String.format(str, this.res,
                this.uid,
                this.app_name,
                this.channel,
                this.imei,
                this.sim_serial_number,
                this.android_id,
                this.device_uuid,
                this.lang,
                this.lang_code,
                this.country,
                this.operator,
                this.carrier,
                this.network,
                this.device_finger_print_id,
                this.brand,
                this.device,
                this.sdk,
                this.model,
                this.device_type,
                this.mac);

        return url + tmp;
    }


    private String getNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == 1) {
                return "WIFI";
            }

            if (activeNetwork.getType() == 0) {
                return "MOBILE";
            }
        }

        return "unknown";
    }

    private String getUniquePsuedoID() {
        if (this.tm == null) {
            return "";
        }

        String m_szDevIDShort = "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10;
        String serial = null;

        try {
            serial = Build.class.getField("SERIAL").get((Object) null).toString();
            return (new UUID((long) m_szDevIDShort.hashCode(), (long) serial.hashCode())).toString();
        } catch (Exception var4) {
            serial = "serial";
            return (new UUID((long) m_szDevIDShort.hashCode(), (long) serial.hashCode())).toString();
        }
    }

    private String getMacAddress(Context mContext) {
        String macStr = "";
        WifiManager wifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getMacAddress() != null) {
            macStr = wifiInfo.getMacAddress();// MAC地址
        } else {
            macStr = "null";
        }

        return macStr;
    }


    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getSim_serial_number() {
        return sim_serial_number;
    }

    public void setSim_serial_number(String sim_serial_number) {
        this.sim_serial_number = sim_serial_number;
    }

    public String getAndroid_id() {
        return android_id;
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }

    public String getDevice_uuid() {
        return device_uuid;
    }

    public void setDevice_uuid(String device_uuid) {
        this.device_uuid = device_uuid;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getLang_code() {
        return lang_code;
    }

    public void setLang_code(String lang_code) {
        this.lang_code = lang_code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getDevice_finger_print_id() {
        return device_finger_print_id;
    }

    public void setDevice_finger_print_id(String device_finger_print_id) {
        this.device_finger_print_id = device_finger_print_id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getSdk() {
        return sdk;
    }

    public void setSdk(String sdk) {
        this.sdk = sdk;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

}