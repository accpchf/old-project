/*
 * JS模块加载公有配置
 */
require.config({
    baseUrl : context + "/static",
    paths : {
        "jquery"    : "plugins/jquery/jquery-1.11.1",
        "jqueryUI"  : "plugins/jquery-ui/jquery-ui-1.10.4.min",
        "layer"     : "plugins/layer/layer.min",
        "my97" 		: "plugins/My97DatePicker/WdatePicker",
        "util"		: "script/common/utils",
        "jqueryForm": "plugins/jquery-form/jquery.form",
        "highcharts": "plugins/highcharts/highcharts",
        "jqueryZtree": "plugins/zTree/jquery.ztree.core-3.5.min",
        "socketio"  : nodeJsUrl+"/socket.io/socket.io",
        "bootstrap":"plugins/bootstrap/bootstrap",
        "script"    : "script"
    },
    shim : {
    	"highcharts": {
            "exports": "highcharts",
            "deps": ["jquery"] 
        },
        "bootstrap": {
            "deps": ["jquery"] 
        },
    	'jqueryForm': {
            deps: ['jquery']
        },
        'jqueryUI' : {
            deps : ['jquery'],
            exports : '$'
        },
        'layer' : {
            deps : ['jquery']
        }
    }
});
/*
 * AJAX JSON  数据发送前置处理
 */
require(["jquery"], function($,util) {
    $.ajaxPrefilter(function(options, originalOptions, jqXHR) {
        if(/application\/json/.test(originalOptions.contentType) && originalOptions.data) {
            options.processData = false;
            options.data = JSON.stringify(originalOptions.data);
        };
        //AJAX缓存
        if(options['type'] === 'GET' && originalOptions['cache'] === true) {
            jqXHR.done(function(response) {
                sessionStorage.setItem(options['url'], JSON.stringify(response));
            });
        };
    });
    $.ajaxTransport("+*", function(options, originalOptions, jqXHR, headers, completeCallback) {
        //AJAX 返回 缓存
        if(originalOptions['cache'] === true) {
            var response = sessionStorage.getItem(options['url']);
            if(response) {
                return {
                    send : function(headers, completeCallback) {
                        completeCallback(200, "success", {
                            "result" : response
                        });
                    },
                    abort : function() {
                    }
                };
            };
        };
    });
});
//判断是否可以连接聊天服务器
requirejs.onError = function (err) {
    if(err.requireModules && err.requireModules.length > 0){
    	if($.inArray("socketio",err.requireModules) >= 0){
    		layer.alert("连接失败！");
    	}
    }
    throw err;
};
/*
 * 引入框架JS
 */
require(["script/framework/framework", "script/common/utils","bootstrap"], function(framework) {
    framework.init();
});
