/*
 WearSystemProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.deviceplugin.wear.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.nttdocomo.android.dconnect.deviceplugin.wear.setting.WearSettingActivity;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.DConnectProfileProvider;
import com.nttdocomo.android.dconnect.profile.SystemProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * JUnit用テストデバイスプラグイン、Systemプロファイル.
 * @author NTT DOCOMO, INC.
 */
public class WearSystemProfile extends SystemProfile {
	
	/**
	 * Tag.
	 */
	private static final String TAG = "WEAR";
	
    /**
     * バージョン.
     */
    public static final String VERSION = "1.0";
    
    /**
     * デバイスIDをチェックする.
     * @param deviceId デバイスID
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    private boolean checkdeviceId(final String deviceId) {
        return WearNetworkServiceDiscoveryProfile.DEVICE_ID.equals(deviceId);
    }

    /**
     * デバイスIDが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptydeviceId(final Intent response) {
        MessageUtils.setEmptyDeviceIdError(response);
    }

    /**
     * デバイスが発見できなかった場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createNotFoundDevice(final Intent response) {
        MessageUtils.setNotFoundDeviceError(response);
    }

    /**
     * コンストラクタ.
     * @param plugin プラグイン
     */
    public WearSystemProfile(final DConnectProfileProvider plugin) {
        super(plugin);
    }
    
    /*
    @Override
    protected boolean onGetDevice(Intent request, Intent response, String deviceId) {
        
        if (deviceId == null) {
            createEmptydeviceId(response);
        } else if (!checkdeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else {
            setVersion(response, VERSION);
            setSupports(response, new String[]{});
            Bundle connect = new Bundle();
            setWifiState(connect, false);
            setBluetoothState(connect, false);
            setNFCState(connect, false);
            setBLEState(connect, false);
            setConnect(response, connect);
            setResult(response, DConnectMessage.RESULT_OK);
        }
        
        return true;
    }
    */

    @Override
    protected Class<? extends Activity> getSettingPageActivity(final Intent request, final Bundle param) {
       
    	Log.i(TAG, "getSettingPageActivity");
    	return WearSettingActivity.class; 
    }
    
}
