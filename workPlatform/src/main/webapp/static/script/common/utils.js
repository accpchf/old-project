
/**
 * 表单验证公用插件
 */
InputValidate.prototype.handler = function(){
	var $o = this.target,
		r = this.required,
		maxl = this.maxLength,
		minl = this.minLength,
		type = this.type,
		p = this.pattern,
		$c = this.confirmpwd,
	    m = ['该项为必填项',
             '输入值长度应不大于' + maxl,
             '输入值长度应不小于' + minl,
             '请输入正确的邮箱格式',
             '输入错误',
             '密码由数字、字母和下划线组成',
             '两次输入的密码不一致',
             '手机号码格式不对',
             '请输入100以内的整数',
             '请不要输入|'],
	    regs = [ /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})/, 
	            /[^a-zA-Z0-9_]{1}/,
	            /^1[3|5|7|8|][0-9]{9}$/,/^([1]?\d{1,2})$/,/^[^|]*$/];
	if($o && ($o.is('input') || $o.is('textarea')) && $o.length > 0){
		$o.on({
			'validateEvent':function(evt){
				EventUtil.preventDefault(evt);
				var self = $(this);
				var valLength = self.val().trim().length || 0;
				if(r == true && self.val().trim().length <= 0){
					this.currentMsg = m[0];
					self.addClass('error');
					return;
				}
				if(maxl && maxl >0 && valLength > maxl){
					this.currentMsg = m[1];
					self.addClass('error');
					return;
				}
				if(minl && minl > 0 && valLength < minl){
					this.currentMsg = m[2];
					self.addClass('error');
					return;
				}
				if(type){
					switch (type) {
					case 'email':
						if(self.val() && regs[0].test && !regs[0].test(self.val())){
							this.currentMsg = m[3];
							self.addClass('error');
							return;
						}
						break;
					case 'password':
						if(self.val() && regs[1].test(self.val())){
							this.currentMsg = m[5];
							self.addClass('error');
							return;
						}
						break;
					case 'telphone':
						if(self.val() && !regs[2].test(self.val())){
							this.currentMsg = m[7];
							self.addClass('error');
							return;
						}
						break;
					case 'percentage':
						if(self.val() && !regs[3].test(self.val())){
							this.currentMsg = m[8];
							self.addClass('error');
							return;
						}
						break;
					case 'verticalline':
						if(self.val() && !regs[4].test(self.val())){
							this.currentMsg = m[9];
							self.addClass('error');
							return;
						}
						break;
					default:
						break;
					}
				}
				if(p && p.test && !p.test(self.val())){
					this.currentMsg = m[4];
					self.addClass('error');
					return;
				}
				if($c && $c.is('input') && $c.length > 0){
					if($c.val() != self.val()){
						this.currentMsg = m[6];
						self.addClass('error');
						return;
					}
				}
				self.removeClass('error');
				this.currentMsg = "";
			},
			'blur':function(){
				$(this).trigger('validateEvent');
				return false;
			},
			'change':function(){
				$(this).trigger('validateEvent');
				return false;
			},
			'mouseover':function(){
				var self = $(this);
				if(self && self.hasClass('error')){
					layer.tips(this.currentMsg, this , {guide: 2, time: 1});
				}
				return false;
			}
		});
	}
	
};
InputValidate.prototype.checkValidate = function(){
	var $o = this.target;
	$o.trigger('validateEvent');
	if($o.length > 0 && $o.hasClass('error')){
		return false;
	}
	return true;
};
/**
 * 输入框校验函数
 * @param $o 被校验对象(jquery对象)
 * @param r 是否必填 执行值：true
 * @param maxl 最大长度
 * @param minl 最小长度
 * @param t 输入文本类型(email、text、telphone。。。。 默认为text)
 * @param p 自定义校验表达式(ex:/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/)
 * @param $c 二次密码校验时，首密码对象(jquery对象)
 * @returns {InputValidate}
 */
function InputValidate($o, r, maxl, minl, t, p, $c){
	this.target = $o;
	this.required = r;
	this.maxLength = maxl;
	this.minLength = minl;
	this.type = t;
	this.pattern = p;
	this.confirmpwd = $c;
	//this.errormsg = ['该项为必填项',
//	                 '输入值长度应不大于' + maxl,
//	                 '输入值长度应不小于' + minl,
//	                 '请输入正确的邮箱格式',
//	                 '输入错误',
//	                 '密码由数字、字母和下划线组成'];
	//this.reg = [/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/g, /[^a-zA-Z0-9_]{1}/g];
	this.handler();
};

/**
 *日期格式化成字符串 
 * @param {Object} fmt
 */
Date.prototype.format = function(fmt) 
    { 
      var o = { 
        "M+" : this.getMonth()+1,                 //月份 
        "d+" : this.getDate(),                    //日 
        "h+" : this.getHours(),                   //小时 
        "m+" : this.getMinutes(),                 //分 
        "s+" : this.getSeconds(),                 //秒 
        "q+" : Math.floor((this.getMonth()+3)/3), //季度 
        "S"  : this.getMilliseconds()             //毫秒 
      }; 
      if(/(y+)/i.test(fmt)) 
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
      for(var k in o) 
        if(new RegExp("("+ k +")").test(fmt)) 
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
      return fmt; 
    };
    
    
    /**
     *Array的forEach方法兼容IE 
     */
    if (!Array.prototype.forEach)
    {
        Array.prototype.forEach = function(fun)
        {
            var len = this.length;
            if (typeof fun != "function")
                throw new TypeError();
            var thisp = arguments[1];
            for (var i = 0; i < len; i++)
            {
                if (i in this)
                    fun.call(thisp, this[i], i, this);
            }
        };
    }
    
    /**
     *获取指定元素在数组中的位置，不存在则返回-1 
 	 * @param {Object} e
     */
	Array.prototype.getIndexOfItem = function(e){
		for(var i = 0; i < this.length; i++){
			if(this[i] == e){
				return i;
			}
		}
		return -1;
	};
	/**
	 *定义一个事件类，用来处理多浏览器兼容 
	 */
	var EventUtil = {
		getEvent:function(event){
			return event || window.event;
		},
		getTarget:function(event){
			return event.target || event.srcElement;
		},
		stopPropagation:function(event){
			event.stopPropagation ? event.stopPropagation() : event.cancelBulle = true;
		},
		preventDefault:function(event){
			event.preventDefault ? event.preventDefault() : event.returnValue = false;
		}
	};
	
	/**
	 *状态码值 
	 */
	var StatusCode = {
		chatRecordType_text:0,
		chatRecordType_picture:1,
		chatRecordType_file:2,
		//登录
		notExistUser:"账号不存在",
		pwdError:"密码错误",
		//注册
		registerFail:"注册失败"
	};
	
	/**
	 *文件大小格式化 
	 * @param {Object} formString
	 */
	File.prototype.getFormatSize = function(formString){
		var tempV = 0.0009765625;// 1/1024
		var selfSize = this.size;
		if(['B'].getIndexOfItem(formString) > -1){
			return '' + selfSize + formString;
		}else if(['KB', 'B'].getIndexOfItem(formString) > -1){
			return '' + (selfSize * tempV).toFixed(2) + formString;
		}else if(['MB', 'M'].getIndexOfItem(formString) > -1){
			return '' + (selfSize * tempV * tempV) + formString;
		}else{
			var formB = ['B', 'KB', 'MB'];
			var i = 0;
			while(true){
				if(i == 2 || selfSize < 100){
					return '' + selfSize.toFixed(2) + formB[i];
				}
				selfSize *= tempV;
				i++;
			};
		}
		return selfSize;
	};
	/**
	 *文件大小限制 
	 * @param {Object} fileSize
	 * @param {Object} formString
	 */
	File.prototype.limitSize = function(fileSize, formString){
		var tempV = 0.0009765625;// 1/1024
		var selfSize = this.size;
		if(['B'].getIndexOfItem(formString) > -1){
			return selfSize > fileSize;
		}else if(['KB', 'K'].getIndexOfItem(formString) > -1){
			return selfSize * tempV > fileSize;
		}else if(['MB', 'M'].getIndexOfItem(formString) > -1){
			return selfSize * tempV * tempV > fileSize;
		}
		return selfSize > fileSize;
	};
	