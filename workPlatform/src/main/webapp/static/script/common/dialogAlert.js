define(['jquery','layer'],function($){
	
	var dialog = {
			alert : function(msg,type){
				var icon = 0;
				if(type !== null && type != 'undefined' && !isNaN(type)){
					icon = type;
				}
				$.layer({
					title: '信息',
					shade: [0.5, '#000'],
					shadeClose: false,
					closeBtn: false,
					fix: true,
					move: '.xubox_title',
					btn: ['确定'],
					dialog: {
					    type: icon,
					    msg: msg
					},
					yes: function(index){
						$("#xubox_shade"+index).remove();
						$("#xubox_layer"+index).remove();
						return false;
					}
				});
			}
	}
	
	return dialog;
	
});