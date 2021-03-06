/*
 HostProximityProfile.java
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package com.nttdocomo.android.dconnect.deviceplugin.host.profile;

import java.util.List;

import android.content.Intent;
import android.hardware.SensorManager;
import com.nttdocomo.android.dconnect.event.Event;
import com.nttdocomo.android.dconnect.event.EventError;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.ProximityProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;

/**
 * Proximity Profile.
 * 
 * @author NTT DOCOMO, INC.
 */
public class HostProximityProfile extends ProximityProfile implements SensorEventListener {

    /** TAG. */
    private static final String TAG = "HOST";

    /**
     * デバイスID.
     */
    private static String mDeviceId = "";

    /** 
     * Sensor Manager. 
     */
    private SensorManager mSensorManagerProximity;
    
    /**
     * デバイスIDをチェックする.
     * @param deviceId デバイスID
     * @return <code>deviceId</code>がテスト用デバイスIDに等しい場合はtrue、そうでない場合はfalse
     */
    private boolean checkdeviceId(final String deviceId) {
        return HostNetworkServiceDiscoveryProfile.DEVICE_ID.equals(deviceId);
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
     * セッションキーが空の場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createEmptySessionKey(final Intent response) {
        MessageUtils.setInvalidRequestParameterError(response);
    }

    /**
     * デバイスが発見できなかった場合のエラーを作成する.
     * 
     * @param response レスポンスを格納するIntent
     */
    private void createNotFoundDevice(final Intent response) {
        MessageUtils.setNotFoundDeviceError(response);
    }

    @Override
    protected boolean onPutOnUserProximity(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptydeviceId(response);
        } else if (!checkdeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            createEmptySessionKey(response);
        } else {
            setResult(response, DConnectMessage.RESULT_OK);
            
            // イベントの登録
            EventError error = EventManager.INSTANCE.addEvent(request);
            
            if (error == EventError.NONE) { 
                //((HostDeviceService) getContext()).registerPromityEvent(response, deviceId, sessionKey);
                mDeviceId = deviceId;
                mSensorManagerProximity = (SensorManager) this.getContext()
                                    .getSystemService(this.getContext().SENSOR_SERVICE);
                List<Sensor> sensors = mSensorManagerProximity.getSensorList(Sensor.TYPE_PROXIMITY);

                if (sensors.size() > 0) {
                    Sensor sensor = sensors.get(0);
                    mSensorManagerProximity.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                }
                
                setResult(response, DConnectMessage.RESULT_OK);
                
                return true;
                
            } else {
                setResult(response, DConnectMessage.RESULT_ERROR);
                
                return true;
            }
           
        }
        return true;
    }

    @Override
    protected boolean onDeleteOnUserProximity(final Intent request, final Intent response, final String deviceId,
            final String sessionKey) {
        if (deviceId == null) {
            createEmptydeviceId(response);
        } else if (!checkdeviceId(deviceId)) {
            createNotFoundDevice(response);
        } else if (sessionKey == null) {
            createEmptySessionKey(response);
        } else {
            // イベントの解除
            EventError error = EventManager.INSTANCE.removeEvent(request);
            if (error == EventError.NONE) {
                mSensorManagerProximity.unregisterListener(this);
                
                return false;
            } else {
                MessageUtils.setError(response, 100, "Can not unregister event.");
                return true;
            }
        }
      
        return true;
    }

    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            
            List<Event> events = EventManager.INSTANCE.getEventList(mDeviceId, 
                    ProximityProfile.PROFILE_NAME,
                    null, 
                    ProximityProfile.ATTRIBUTE_ON_USER_PROXIMITY);
            
            Bundle mProximityBundle = new Bundle();
           
            if (sensorEvent.values[0] == 0.0) {
                ProximityProfile.setNear(mProximityBundle, true);
            } else {
                ProximityProfile.setNear(mProximityBundle, false);
            }
            
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                Intent mIntent = EventManager.createEventMessage(event);
                ProximityProfile.setProximity(mIntent, mProximityBundle);
                getContext().sendBroadcast(mIntent);
            }
        }
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
        
    }

}
