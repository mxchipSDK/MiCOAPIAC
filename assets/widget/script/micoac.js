/*
 * User Management JavaScript Library
 * Copyright (c) 2015 mxchip.com
 * by Rocke 2015-09-17
 */
(function(window) {
    var m = {};
    var micoobj = null;
    var ifInit = false;

    m.init = function(callback) {
        var sucm;
        var errm;
        console.log(typeof api == "undefined");
        if (typeof api != "undefined") {
            micoobj = api.require("MiCO");
            ifInit = true;
            callback({
                "status": "success"
            }, errm);
        } else {
            callback(sucm, {
                "error": "api is not defined"
            });
        }
    };

    m.getSSId = function(callback) {
        if (ifInit) {
            var sucm;
            var errm;
            micoobj.getSSID(function(ret, err) {
                if (ret.ssid) {
                    callback(ret, errm);
                } else {
                    callback(sucm, err);
                }
            });
        }
    };

    m.startEasyLink = function(ssid, password, callback) {
        if (ifInit) {
            var sucm;
            var errm;
            micoobj.startEasyLink({
                wifi_ssid: ssid,
                wifi_password: password
            }, function(ret, err) {
                if (ret.devip) {
                    callback(ret, errm);
                } else {
                    callback(sucm, err);
                }
            });
        }
    };

    m.onlyStopEasyLink = function() {
        if (ifInit) {
            micoobj.onlyStopEasyLink(function(ret, err) {});
        }
    };

    m.stopEasyLink = function(callback) {
        if (ifInit) {
            var sucm;
            var errm;
            micoobj.stopEasyLink(function(ret, err) {
                if (ret) {
                    callback(ret, errm);
                } else {
                    callback(sucm, err);
                }
            });
        }
    };

    //---------------------easylink end
    m.startMDNS = function(servicename, callback) {
        if (ifInit) {
            var sucm;
            var errm;            
            micoobj.startMDNS({
                servicename: servicename
            }, function(ret, err) {
                if (ret.deviceinfo) {
                    callback(ret, errm);
                } else {
                    callback(sucm, err);
                }
            });
        }
    };

    m.stopMDNS = function(callback) {
        if (ifInit) {
            var sucm;
            var errm;
            micoobj.stopMDNS(function(ret, err) {
                if (ret) {
                    callback(ret, errm);
                } else {
                    callback(sucm, err);
                }
            });
        }
    };

    //---------------------mDNS end
    m.startMqtt = function(host, port, username, password, clientID, topic, callback) {
        if (ifInit) {
            var sucm;
            var errm;
            micoobj.startMqtt({
                host: host,
                port: port,
                username: username,
                password: password,
                clientID: clientID,
                topic: topic
            }, function(rets, errs) {
                if (rets) {
                    callback(rets, errm);
                } else {
                    callback(sucm, errs);
                }
            });
        }
    };

    m.stopMqtt = function(callback) {
        if (ifInit) {
            var sucm;
            var errm;
            micoobj.stopMqtt(function(ret, err) {
                if (typeof ret == "undefined") {
                    callback(ret, errm);
                } else {
                    callback(sucm, err);
                }
            });
        }
    };

    m.recvMqttMsg = function(callback) {
        if (ifInit) {
            var sucm;
            var errm;
            micoobj.recvMqttMsg(function(ret, err) {
                if (ret) {
                    callback(ret, errm);
                } else {
                    callback(sucm, err);
                }
            });
        }
    };
    m.stopRecvMqttMsg = function(callback) {
        if (ifInit) {
            var sucm;
            var errm;
            micoobj.stopRecvMqttMsg(function(ret, err) {
                if (ret) {
                    callback(ret, errm);
                } else {
                    callback(sucm, err);
                }
            });
        }
    };

    m.publishCommand = function(topic, command, qos, retained, callback) {
        if (ifInit) {
            var sucm;
            var errm;
            micoobj.publishCommand({
                topic: topic,
                command: command,
                qos: qos,
                retained: retained
            }, function(ret, err) {
                if (ret) {
                    callback(ret, errm);
                } else {
                    callback(sucm, err);
                }
            });
        }
    };

    m.addSubscribe = function(topic, qos, callback) {
        if (ifInit) {
            var sucm;
            var errm;
            micoobj.addSubscribe({
                topic: topic,
                qos: qos
            }, function(ret, err) {
                if (ret) {
                    callback(ret, errm);
                } else {
                    callback(sucm, err);
                }
            });
        }
    };

    m.rmSubscribe = function(topic, callback) {
        if (ifInit) {
            var sucm;
            var errm;
            micoobj.rmSubscribe({
                topic: topic
            }, function(ret, err) {
                if (ret) {
                    callback(ret, errm);
                } else {
                    callback(sucm, err);
                }
            });
        }
    };

    /*---------------------MQTT------------------------end*/

    window.MiCO = m;

})(window);
