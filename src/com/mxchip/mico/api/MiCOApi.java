package com.mxchip.mico.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.mico.wifi.EasyLinkWifiManager;
import com.mxchip.easylink.EasyLinkAPI;
import com.mxchip.easylink.FTCListener;
import com.mxchip.jmdns.JmdnsAPI;
import com.mxchip.jmdns.JmdnsListener;
import com.mxchip.mqttservice2.MqttServiceAPI;
import com.mxchip.mqttservice2.MqttServiceListener;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

/**
 * Include EasyLink MQTT mDNS
 * 
 * @author Rocke 2015/09/14
 * 
 */
public class MiCOApi extends UZModule {
	private EasyLinkWifiManager mWifiManager = null;
	private static MqttServiceAPI mapi;
	private EasyLinkAPI elapi;
	private JmdnsAPI mdnsApi;
	private Context ctx;

	private boolean _EASYLINKTAG = false;
	private boolean _STARTMQTTTAG = false;
	private boolean _RECVMQTTTAG = false;
	private boolean _MDNSTAG = false;
	
	private boolean _ONLY_EASYLINK = false;

	public MiCOApi(UZWebView webView) {
		super(webView);
		ctx = getContext();
		new MiCOErrorCode();
	}

	/**
	 * ------------begin EasyLink
	 * 
	 * @param moduleContext
	 */
	public void jsmethod_getSSID(UZModuleContext moduleContext) {
		mWifiManager = new EasyLinkWifiManager(ctx);
		String ssidName = mWifiManager.getCurrentSSID();
		boolean res = ssidName.contains("unknown");
		try {
			JSONObject json;
			if (ssidName == null || !(ssidName.length() > 0) || res) {
				callback(moduleContext, null, MiCOErrorCode._SSID_UN_CONNECT);
			} else {
				json = new JSONObject("{\"ssid\":\"" + ssidName + "\"}");
				callback(moduleContext, json, null);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void jsmethod_startEasyLink(final UZModuleContext moduleContext) {
		String wifi_ssid = moduleContext.optString("wifi_ssid");
		String wifi_password = moduleContext.optString("wifi_password");
		if (wifi_ssid.equals("") || wifi_password.equals("")) {
			callback(moduleContext, null, MiCOErrorCode._PARA_EMPTY);
		} else if (_EASYLINKTAG) {
			callback(moduleContext, null, MiCOErrorCode._EASYLINK_ON_TASK);
		} else {
			elapi = new EasyLinkAPI(ctx);
			_EASYLINKTAG = true;
			_ONLY_EASYLINK = true;
			elapi.startEasyLink_FTC(ctx, wifi_ssid, wifi_password,
					new FTCListener() {
						@Override
						public void onFTCfinished(String ip, String jsonString) {
							// Log.d("FTCEnd", ip + " " + jsonString);
							try {
								JSONObject devinfo = new JSONObject(jsonString);
								JSONObject jsonStr = new JSONObject();
								jsonStr.put("devip", ip);
								jsonStr.put("devinfo", devinfo);
								callback(moduleContext, jsonStr, null);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							// elapi.stopFTC();
							stopEasyLink();
						}

						@Override
						public void isSmallMTU(int MTU) {
						}
					});
		}
	}
	
	private void stopEasyLink(){
		if(_ONLY_EASYLINK){
			elapi.stopEasyLink();
			_ONLY_EASYLINK = false;
		}
	}

	public void jsmethod_onlyStopEasyLink(UZModuleContext moduleContext) {
		if (_EASYLINKTAG) {
			stopEasyLink();
		} else {
			callback(moduleContext, null, MiCOErrorCode._EASYLINK_NOT_START);
		}
	}

	public void jsmethod_stopEasyLink(UZModuleContext moduleContext) {
		try {
			if (_EASYLINKTAG) {
				elapi.stopFTC();
				elapi.stopEasyLink();
				_EASYLINKTAG = false;
				JSONObject jsonStr = new JSONObject("{\"status\":\"Stop\"}");
				callback(moduleContext, jsonStr, null);
			} else {
				callback(moduleContext, null, MiCOErrorCode._EASYLINK_NOT_START);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ------------end EasyLink
	 */
	/**
	 * ------------begin mDNS
	 */
	public void jsmethod_startMDNS(final UZModuleContext moduleContext) {
		if (_MDNSTAG) {
			callback(moduleContext, null, MiCOErrorCode._MDNS_ON_TASK);
		}else{
			String servicename = moduleContext.optString("servicename");
			mdnsApi = new JmdnsAPI(ctx);
			_MDNSTAG = true;
			mdnsApi.startMdnsService(servicename, new JmdnsListener() {
				@Override
				public void onJmdnsFind(JSONArray deviceJson) {
					if (!deviceJson.equals("")) {
						try {
							JSONObject json = new JSONObject();
							json.put("deviceinfo", deviceJson);
							callback(moduleContext, json, null);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}

	public void jsmethod_stopMDNS(UZModuleContext moduleContext) {
		if(!_MDNSTAG){
			callback(moduleContext, null, MiCOErrorCode._MDNS_NOT_START);
		}else{
			mdnsApi.stopMdnsService();
			_MDNSTAG = false;
			try {
				JSONObject jsonStr = new JSONObject("{\"status\":\"Stop\"}");
				callback(moduleContext, jsonStr, null);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ------------end mDNS
	 */

	/**
	 * ------------begin MQTT
	 * 
	 * @param moduleContext
	 */
	public void jsmethod_startMqtt(final UZModuleContext moduleContext) {
		mapi = new MqttServiceAPI(ctx);
		String host = moduleContext.optString("host");
		String port = moduleContext.optString("port");
		String username = moduleContext.optString("username");
		String password = moduleContext.optString("password");
		String clientID = moduleContext.optString("clientID");
		String topic = moduleContext.optString("topic");
		if (host.equals("") || port.equals("") || username.equals("")
				|| password.equals("") || clientID.equals("")
				|| topic.equals("")) {
			callback(moduleContext, null, MiCOErrorCode._PARA_EMPTY);
		} else if (_STARTMQTTTAG) {
			callback(moduleContext, null, MiCOErrorCode._MQTT_ON_TASK);
		} else {
			_STARTMQTTTAG = true;
			_RECVMQTTTAG = true;
			mapi.startMqttService(host, port, username, password, clientID,
					topic, new MqttServiceListener() {
						@Override
						public void onMqttReceiver(String msgType,
								String messages) {
							Log.d("---" + msgType + "---", messages);
							try {
								JSONObject json = null;
								if (msgType.equals("payload")) {
									json = new JSONObject(messages);
								} else {
									json = new JSONObject("{\"" + msgType
											+ "\":\"" + messages + "\"}");
								}
								callback(moduleContext, json, null);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
		}
	}

	public void jsmethod_stopMqtt(UZModuleContext moduleContext) {
		if (_STARTMQTTTAG) {
			mapi.stopMqttService();
			_STARTMQTTTAG = false;
			_RECVMQTTTAG = false;
		} else {
			callback(moduleContext, null, MiCOErrorCode._MQTT_NOT_START);
		}
	}

	public void jsmethod_recvMqttMsg(UZModuleContext moduleContext) {
		if (!_STARTMQTTTAG)
			callback(moduleContext, null, MiCOErrorCode._MQTT_NOT_START);
		else if (_RECVMQTTTAG)
			callback(moduleContext, null, MiCOErrorCode._MQTT_RECV_ON_TASK);
		else {
			mapi.recvMessage();
			_RECVMQTTTAG = true;
		}
	}

	public void jsmethod_stopRecvMqttMsg(UZModuleContext moduleContext) {
		if (!_STARTMQTTTAG)
			callback(moduleContext, null, MiCOErrorCode._MQTT_NOT_START);
		else if (!_RECVMQTTTAG)
			callback(moduleContext, null, MiCOErrorCode._MQTT_RECV_NOT_START);
		else {
			mapi.stopRecvMessage();
			_RECVMQTTTAG = false;
		}
	}

	public void jsmethod_publishCommand(UZModuleContext moduleContext) {
		String topic = moduleContext.optString("topic");
		String command = moduleContext.optString("command");
		String qos = moduleContext.optString("qos");
		String retained = moduleContext.optString("retained");
		if (topic.equals("") || command.equals("") || qos.equals("")
				|| retained.equals("")) {
			callback(moduleContext, null, MiCOErrorCode._PARA_EMPTY);
		} else if (!_STARTMQTTTAG) {
			callback(moduleContext, null, MiCOErrorCode._MQTT_NOT_START);
		} else {
			mapi.publishCommand(topic, command, Integer.parseInt(qos),
					Boolean.getBoolean(retained));
		}
	}

	public void jsmethod_addSubscribe(UZModuleContext moduleContext) {
		String topic = moduleContext.optString("topic");
		String qos = moduleContext.optString("qos");
		if (topic.equals("") || qos.equals("")) {
			callback(moduleContext, null, MiCOErrorCode._PARA_EMPTY);
		} else if (!_STARTMQTTTAG) {
			callback(moduleContext, null, MiCOErrorCode._MQTT_NOT_START);
		} else {
			mapi.subscribe(topic, Integer.parseInt(qos));
		}
	}

	public void jsmethod_rmSubscribe(UZModuleContext moduleContext) {
		String topic = moduleContext.optString("topic");
		if (topic.equals("")) {
			callback(moduleContext, null, MiCOErrorCode._PARA_EMPTY);
		} else if (!_STARTMQTTTAG) {
			callback(moduleContext, null, MiCOErrorCode._MQTT_NOT_START);
		} else {
			mapi.unsubscribe(topic);
		}
	}

	/**
	 * ------------end MQTT
	 */

	/**
	 * all call back
	 * 
	 * @param mContext
	 * @param json
	 */
	public void callback(UZModuleContext mContext, JSONObject ret,
			JSONObject err) {
		try {
			if (null != ret)
				mContext.success(ret, false);
			else
				mContext.error(ret, err, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
