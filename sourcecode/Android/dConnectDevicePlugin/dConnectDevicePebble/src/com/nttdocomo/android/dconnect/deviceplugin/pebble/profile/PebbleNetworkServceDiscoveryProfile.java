package com.nttdocomo.android.dconnect.deviceplugin.pebble.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import com.getpebble.android.kit.PebbleKit;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.PebbleDeviceService;
import com.nttdocomo.android.dconnect.deviceplugin.pebble.util.PebbleManager.OnConnectionStatusListener;
import com.nttdocomo.android.dconnect.event.Event;
import com.nttdocomo.android.dconnect.event.EventError;
import com.nttdocomo.android.dconnect.event.EventManager;
import com.nttdocomo.android.dconnect.message.MessageUtils;
import com.nttdocomo.android.dconnect.profile.NetworkServiceDiscoveryProfile;
import com.nttdocomo.dconnect.message.DConnectMessage;

/**
 * Pebble用 Network Service Discoveryプロファイル.
 */
public class PebbleNetworkServceDiscoveryProfile extends NetworkServiceDiscoveryProfile {
    /**
     * デバイスIDのプレフィックス.
     */
    public static final String DEVICE_ID = "Pebble";
    /**
     * デバイス名を定義.
     */
    public static final String DEVICE_NAME = "Pebble";

    /**
     * コンストラクタ.
     * @param service サービス
     */
    public PebbleNetworkServceDiscoveryProfile(final PebbleDeviceService service) {
        service.getPebbleManager().addConnectStatusListener(new OnConnectionStatusListener() {
            @Override
            public void onConnect() {
                Bundle service = new Bundle();
                setName(service, DEVICE_NAME);
                setType(service, NetworkType.BLUETOOTH);
                setOnline(service, true);

                List<Event> evts = EventManager.INSTANCE.getEventList(
                        PROFILE_NAME, null, ATTRIBUTE_ON_SERVICE_CHANGE);
                for (Event evt : evts) {
                    Intent intent = EventManager.createEventMessage(evt);
                    intent.putExtra(NetworkServiceDiscoveryProfile.PARAM_NETWORK_SERVICE, service);
                    ((PebbleDeviceService) getContext()).sendEvent(intent, evt.getAccessToken());
                }
            }
            @Override
            public void onDisconnect() {
                Bundle service = new Bundle();
                setName(service, DEVICE_NAME);
                setType(service, NetworkType.BLUETOOTH);
                setOnline(service, false);

                List<Event> evts = EventManager.INSTANCE.getEventList(
                        PROFILE_NAME, null, ATTRIBUTE_ON_SERVICE_CHANGE);
                for (Event evt : evts) {
                    Intent intent = EventManager.createEventMessage(evt);
                    intent.putExtra(NetworkServiceDiscoveryProfile.PARAM_NETWORK_SERVICE, service);
                    ((PebbleDeviceService) getContext()).sendEvent(intent, evt.getAccessToken());
                }
            }
        });
    }
    @Override
    public boolean onGetGetNetworkServices(final Intent request, final Intent response) {
        boolean connected = PebbleKit.isWatchConnected(getContext());
        boolean supported = PebbleKit.areAppMessagesSupported(getContext());
        if (connected && supported) {
            List<Bundle> services = new ArrayList<Bundle>();
            BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
            if (bondedDevices.size() > 0) {
                for (BluetoothDevice device : bondedDevices) {
                    String deviceName = device.getName();
                    String deviceAddress = device.getAddress();
                    // URIに使えるように、Macアドレスの":"を取り除いて小文字に変換する 
                    String deviceid = deviceAddress.replace(":", "").toLowerCase();
                    if (deviceName.indexOf("Pebble") != -1) {
                        Bundle service = new Bundle();
                        setId(service, DEVICE_ID + deviceid);
                        setName(service, deviceName);
                        setType(service, NetworkType.BLUETOOTH);
                        setOnline(service, true);
                        services.add(service);
                    }
                }
            }
            setServices(response, services);
            setResult(response, DConnectMessage.RESULT_OK);
        } else {
            MessageUtils.setNotFoundDeviceError(response);
        }
        return true;
    }

    @Override
    protected boolean onPutOnServiceChange(Intent request, Intent response, String deviceId, String sessionKey) {
        EventError error = EventManager.INSTANCE.addEvent(request);
        switch (error) {
        case NONE:
            setResult(response, DConnectMessage.RESULT_OK);
            break;
        case INVALID_PARAMETER:
            MessageUtils.setInvalidRequestParameterError(response);
            break;
        default:
            MessageUtils.setUnknownError(response);
            break;
        }
        return true;
    }

    @Override
    protected boolean onDeleteOnServiceChange(Intent request, Intent response, String deviceId, String sessionKey) {
        EventError error = EventManager.INSTANCE.removeEvent(request);
        switch (error) {
        case NONE:
            setResult(response, DConnectMessage.RESULT_OK);
            break;
        case INVALID_PARAMETER:
            MessageUtils.setInvalidRequestParameterError(response);
            break;
        default:
            MessageUtils.setUnknownError(response);
            break;
        }
        return true;
    }
}
