<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>task board</title>
<style>

body{
	background-color:#fff;
	overflow-y:auto;
	font-family:"Microsoft Yahei",Helvetica Neue,Hiragino Sans GB,WenQuanYi Micro Hei,sans-serif;
	font-size:14px;
	color:#808080;
}
.main-content{ background:#fbfbfb; border:1px solid #e1e1e1; width:90%; margin:0 auto; padding:10px 20px 20px 20px;}
.main-content p{ line-height:38px; color:#333;font-size:11pt;margin-top:0}
.main-content a{color:#333; text-decoration:none}
.main-content a:hover{ text-decoration:underline}
.main-content span{ color:#999; margin-top:20px; display:block;}
.bottom{ border-top:1px solid #e1e1e1; padding:0 15px; height:30px; background:#f5f5f5}
.bottom span{ vertical-align:top;margin-top:5px; color:#999 }
</style>

</head>
<body>

<div class="main-content">
<p>
	<b>${userName}</b>，您好！<br />
	欢迎您使用 Workboard，请点击下面的链接激活您的帐户：<br />
    <a href='${href}' target="_blank">${href}</a><br />
    <span>如果无法打开链接，请复制上面的链接粘贴到浏览器的地址栏。</span>
</p>

<div class="bottom">
   <span style=" float:right">来自：workboard</span>
</div>

</div>

</body>
</html>
