package com.mxchip.mico.api;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.mico.wifi.EasyLinkWifiManager;
import com.mxchip.easylink.EasyLinkAPI;
import com.mxchip.easylink.FTCListener;
import com.mxchip.mqttservice2.MqttServiceAPI;
import com.mxchip.mqttservice2.MqttServiceListener;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

/**
 * Include easylink mqtt mDNS
 * 
 * @author Rocke 2015/09/14
 * 
 */
public class MiCOApi extends UZModule {
	private UZModuleContext startMContext;
	private UZModuleContext wifiContext;
	private UZModuleContext startelContext;
	private UZModuleContext stopelContext;

	private EasyLinkWifiManager mWifiManager = null;

	private static MqttServiceAPI mapi;
	private EasyLinkAPI elapi;

	private Context ctx;

	private JSONObject _ERREMPTYJSON;

	public MiCOApi(UZWebView webView) {
		super(webView);
		ctx = getContext();
		try {
			_ERREMPTYJSON = new JSONObject(
					"{\"error\":\"Parameters can not be empty.\"}");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void jsmethod_getSSID(UZModuleContext moduleContext) {
		this.wifiContext = moduleContext;
		mWifiManager = new EasyLinkWifiManager(ctx);
		String ssidName = mWifiManager.getCurrentSSID();
		boolean res = ssidName.contains("unknown");
		try {
			JSONObject json;
			if (ssidName == null || !(ssidName.length() > 0) || res) {
				json = new JSONObject("{\"error\":\"unconnected\"}");
				callback(wifiContext, null, json);
			} else {
				json = new JSONObject("{\"ssid\":\"" + ssidName + "\"}");
				callback(wifiContext, json, null);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void jsmethod_startEasyLink(UZModuleContext moduleContext) {
		this.startelContext = moduleContext;

		elapi = new EasyLinkAPI(ctx);
		String wifi_ssid = moduleContext.optString("wifi_ssid");
		String wifi_password = moduleContext.optString("wifi_password");
		if (wifi_ssid.equals("") || wifi_password.equals("")) {
			callback(startelContext, null, _ERREMPTYJSON);
		} else {
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
								callback(startelContext, jsonStr, null);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							// elapi.stopFTC();
							elapi.stopEasyLink();
						}

						@Override
						public void isSmallMTU(int MTU) {
						}
					});
		}
	}

	public void jsmethod_onlyStopEasyLink(UZModuleContext moduleContext) {
		elapi.stopEasyLink();
	}

	public void jsmethod_stopEasyLink(UZModuleContext moduleContext) {
		this.stopelContext = moduleContext;
		try {
			elapi.stopFTC();
			elapi.stopEasyLink();
			String status = "{\"status\":\"Stop\"}";
			JSONObject jsonStr = new JSONObject(status);
			callback(stopelContext, jsonStr, null);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ------------begin MQTT
	 * 
	 * @param moduleContext
	 */
	public void jsmethod_startMqtt(final UZModuleContext moduleContext) {
		startMContext = moduleContext;
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
			callback(startMContext, null, _ERREMPTYJSON);
		} else {
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
								callback(startMContext, json, null);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
		}
	}

	public void jsmethod_stopMqtt(final UZModuleContext moduleContext) {
		mapi.stopMqttService();
	}

	public void jsmethod_recvMqttMsg(final UZModuleContext moduleContext) {
		mapi.recvMessage();
	}

	public void jsmethod_stopRecvMqttMsg(final UZModuleContext moduleContext) {
		mapi.stopRecvMessage();
	}

	public void jsmethod_publishCommand(final UZModuleContext moduleContext) {
		String topic = moduleContext.optString("topic");
		String command = moduleContext.optString("command");
		String qos = moduleContext.optString("qos");
		String retained = moduleContext.optString("retained");
		if (topic.equals("") || command.equals("") || qos.equals("")
				|| retained.equals("")) {
			callback(startMContext, null, _ERREMPTYJSON);
		} else {
			mapi.publishCommand(topic, command, Integer.parseInt(qos),
					Boolean.getBoolean(retained));
		}
	}

	public void jsmethod_addSubscribe(final UZModuleContext moduleContext) {
		String topic = moduleContext.optString("topic");
		String qos = moduleContext.optString("qos");
		if (topic.equals("") || qos.equals("")) {
			callback(startMContext, null, _ERREMPTYJSON);
		} else {
			mapi.subscribe(topic, Integer.parseInt(qos));
		}
	}

	public void jsmethod_rmSubscribe(final UZModuleContext moduleContext) {
		String topic = moduleContext.optString("topic");
		if (topic.equals("")) {
			callback(startMContext, null, _ERREMPTYJSON);
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
