package com.example.nicelook;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    /* ����IP */
    private static String PROXY_IP = null;
    /* ����˿� */
    private static int PROXY_PORT = 0;
    /**
     * �жϵ�ǰ�Ƿ�����������
     * 
     * @param context
     * @return
     */
    public static boolean isNetwork(Context context) {
        boolean network = isWifi(context);
        boolean mobilework = isMobile(context);
        if (!network && !mobilework) { // ����������
            //Log.i(NetworkUtil, ����·���ӣ�);
            return false;
        } else if (network == true && mobilework == false) { // wifi����
            //Log.i(NetworkUtil, wifi���ӣ�);
        } else { // ��������
            //Log.i(NetworkUtil, �ֻ���·���ӣ���ȡ������Ϣ��);
            readProxy(context); // ��ȡ������Ϣ
            return true;
        }
        return true;
    }

    /**
     * ��ȡ�������
     * 
     * @param context
     */
    private static void readProxy(Context context) {
//        Uri uri = Uri.parse(content://telephony/carriers/preferapn);
//        ContentResolver resolver = context.getContentResolver();
//        Cursor cursor = resolver.query(uri, null, null, null, null);
//        if (cursor != null && cursor.moveToFirst()) {
//            PROXY_IP = cursor.getString(cursor.getColumnIndex(proxy));
//            PROXY_PORT = cursor.getInt(cursor.getColumnIndex(port));
//        }
//        cursor.close();
    }

    /**
     * �жϵ�ǰ�����Ƿ���wifi������
     * 
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info != null) {
            return info.isConnected(); // ������������״̬
        }
        return false;
    }

    /**
     * �жϵ�ǰ�����Ƿ����ֻ�����
     * 
     * @param context
     * @return
     */
    public static boolean isMobile(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (info != null) {
            return info.isConnected(); // ������������״̬
        }
        return false;
    }
}
