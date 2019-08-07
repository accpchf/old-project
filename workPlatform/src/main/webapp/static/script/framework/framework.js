define(["jquery","script/framework/topScript","layer"], function($,topScript) {
    var framework = {};
    framework.init = function() {
    	this.commonInfo = topScript.init();

        if($("div[data-id='organizationTemplate']").length > 0){
        	this.loadScript([context+"/static/script/organize/organization.js"]);
        }else if($("div[data-id='projectTemplate']").length > 0){
        	this.loadScript([context+"/static/script/template/projectTemplate.js"]);
        }else if($("div[data-id='relatedMeTempalte']").length > 0){
        	this.loadScript([context+"/static/script/template/relatedMeTempalte.js"]);
        }else{
        	this.loadScript(context+"/static/script/home/home.js");
        }
    };
    framework.loadScript = function(url){
    	$.getScript(url);
    };
    return framework;
});
