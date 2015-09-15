package com.mxchip.mico.api;

import org.json.JSONException;
import org.json.JSONObject;

public class MiCOErrorCode {
	public static JSONObject _SSID_UN_CONNECT;// 90000 phone did not connect

	public static JSONObject _PARA_EMPTY;// 90001 Parameters can not be empty.

	public static JSONObject _EASYLINK_ON_TASK;// 90002 EasyLink on task
	public static JSONObject _EASYLINK_NOT_START;// 90003 EasyLink is not
													// working

	public static JSONObject _MQTT_ON_TASK;// 90004 MQTT on task
	public static JSONObject _MQTT_NOT_START;// 90005 MQTT is not working

	public static JSONObject _MQTT_RECV_ON_TASK;// 90006 We are receiving MQTT
												// message
	public static JSONObject _MQTT_RECV_NOT_START;// 90007 MQTT message is not
													// working
	
	public static JSONObject _MDNS_ON_TASK;// 90008 MDNS on task
	public static JSONObject _MDNS_NOT_START;// 90009 MDNS is not working

	public MiCOErrorCode() {
		try {
			_SSID_UN_CONNECT = new JSONObject("{\"error\":90000}");
			_PARA_EMPTY = new JSONObject("{\"error\":90001}");
			_EASYLINK_ON_TASK = new JSONObject("{\"error\":90002}");
			_EASYLINK_NOT_START = new JSONObject("{\"error\":90003}");
			_MQTT_ON_TASK = new JSONObject("{\"error\":90004}");
			_MQTT_NOT_START = new JSONObject("{\"error\":90005}");
			_MQTT_RECV_ON_TASK = new JSONObject("{\"error\":90006}");
			_MQTT_RECV_NOT_START = new JSONObject("{\"error\":90007}");
			_MDNS_ON_TASK = new JSONObject("{\"error\":90008}");
			_MDNS_NOT_START = new JSONObject("{\"error\":90009}");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
