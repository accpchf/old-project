<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String context = request.getContextPath();
	request.setAttribute("context", context);
%>
<div id="zhcs_OA_chatWindow" class="task_chat">
	<div class="chat_mini" >
        <h3><i class="icon-chat"></i><span class="cmt-title">正在聊(<span class="num"></span>)</span></h3>
    </div>
	<div class="chat_box" style="display:block;">
		<div id="zhcs_OA_chatWindowTop" class="chat_header">
			<div class="icon">
				<a href="#" class="icon-minus"></a>
				<a href="#" class="icon-cancel"></a>
			</div>
			<div class="titles">
				<img src="" class="img-circle"><span></span>
			</div>
		</div>
		<div id="zhcs_OA_chatWindowLeft" class="friends_list_wrap">
			<ul class="friends_list">
			</ul>
		</div>
		<div  id="zhcs_OA_chatWindowCenter" class="chat_content">
			<div id="zhcs_OA_chatWindowContent" class="list">
			</div>

			<div id="zhcs_OA_chatWindowSendMessage" class="send_tantan">
				<div class="tip-wrapper">
					<p class="tip chat_notice">这里是提示</p>
				</div>
				<div class="chat-message">
					<div id="zhcs_OA_chatWindowExpression" class="face-wrap g-hide">
                            <div class="face-con">
                                <table class="face-table">
	                                <tbody>
	                                </tbody>
                                </table>
                             </div>
                        </div>
                        <div class="face-area">
	                        <a title="插入表情" class=" icon-emo-grin"></a>
							<a id="select_picture" title="插入图片" class=" icon-picture"></a>
							<form id="send_picture"  style="display: none">
	                          <input type="file" name="upload_pic"/>
	                        </form>
							<a title="发送文件" id="select_file" class="icon-folder-open-empty-1"></a>
							<form id="send_file" enctype="multipart/form-data" style="display: none">
	                          <input type="file"/>
	                        </form>
                        </div>
<!-- 					<a title="聊天记录"><i class="icon-clipboard-1"></i>聊天记录<i class="icon-down-dir"></i></a> -->
<!-- 					<a title="聊天记录">筛选图片<i class="icon-down-dir"></i></a> -->
				</div>

				<div class="text_area">
					<div class="texttopic" style="display: none;"></div>
					<textarea class="content form-control" placeholder="说点什么吧！"></textarea>
					<div class="func_area">
						
						<a id="sendMessageBtn" class="submit btn">发送</a>
						<a id="closeBtn" class="submit btn space">关闭</a>
					</div>
				</div>
			</div>
		</div>
		<div id="zhcs_OA_chatWindowRight" class="friends_list_wrap">
		</div>
	</div>
	<script type="text/javaScript" src="${context }/static/script/chat/chat.js"></script>
</div>