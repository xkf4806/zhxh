<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <meta name="renderer" content="webkit">
	<title>Insert title here</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/js/pintuer/pintuer.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/view/order/css/table.css">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.8.2.js"></script>
    <script src="${pageContext.request.contextPath}/js/pintuer/pintuer.js"></script>
    <script src="${pageContext.request.contextPath}/js/pintuer/respond.js"></script>
    <script src="${pageContext.request.contextPath}/js/jquery-jtemplates.js"></script>
    <script src="${pageContext.request.contextPath}/js/js.validate.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/layer2.4/layer.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/laydate/laydate.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/js/util.js" type="text/javascript"></script>
    
    <style type="text/css">
    	.doc-naver {padding-top: 10px;padding-bottom: 10px;}
		.doc-header.fixed-top .doc-naver {padding-top: 10px;padding-bottom: 10px;background-color: #fff;}
		.nav-inline li a {line-height: 22px;}
		.border-back {border-color:#b5cfd9;}
		.admin {width: 100%; padding: 20px;background: #fff;right: 0;bottom: 0;top: 87px;overflow: auto;}
		.list-group, .list-link {border: solid 1px #EED97C;background-color: #fffdf3;}
		.panel {border-top: solid 0}
		.bg-order-1{background-color: #999;}
		.bg-order0{background-color: #f60 ;}
		.bg-order1{background-color: #0ae;}
		.bg-order2{background-color: #0ae;}
		.bg-order3{background-color: #2c7;}
		    
    </style>
    <script type="text/javascript">
    	var pageIndex = 1;
		var pageSize = 10;
		var totalPage = 0;
		
		var sel_orderid = "";
		
    	$(function(){
    		//加载数据-订单列表
    		loadData_orderlist();
    		//查询
    		$(".icon-search").click(function(){
    			loadData_orderlist();
    		});
    		//日期控件
    		var start = {
				elem: '#startDate',
				format: 'YYYY-MM-DD',
				//min: laydate.now(), //设定最小日期为当前日期
				max: '2099-06-16 23:59:59', //最大日期
				istime: true,
				istoday: false,
				choose: function(datas){
				   end.min = datas; //开始日选好后，重置结束日的最小日期
				   end.start = datas //将结束日的初始值设定为开始日
				}
			};
			var end = {
				elem: '#endDate',
				format: 'YYYY-MM-DD',
				//min: laydate.now(),
				max: '2099-06-16 23:59:59',
				istime: true,
				istoday: false,
				choose: function(datas){
				  start.max = datas; //结束日选好后，重置开始日的最大日期
				}
			};
			laydate(start);
			laydate(end);
    	});
    	
    	//加载数据-订单列表
    	function loadData_orderlist(){
    		var _data = {};
    		_data.type = "sub";
    		_data.status = $("#orderstatus").val();
    		_data.startDate = $("#startDate").val();
    		_data.endDate = $("#endDate").val();
    		_data.search = $("#keyword").val();
    		_data.pageIndex = pageIndex;
    		_data.pageSize = pageSize;
    		$.ajax({
    			async:false,
                url: "${pageContext.request.contextPath}/order/orderlist.htmls",
                data: _data,
                type: "post",
                dataType:"json",
                success: function (json) {
                	totalPage = Math.ceil(json.total/pageSize);
                	$("#orderList").setTemplateURL("${pageContext.request.contextPath}/view/order/tpl/order/suborderlist.tpl");
                	$("#orderList").setParam('rowCount', json.total);
	         		$("#orderList").setParam('pageSize', pageSize);
	         		$("#orderList").setParam('pageIndex', pageIndex);
	         		$("#orderList").setParam('totalPage', totalPage);
	         		$("#orderList").setParam('formatAmount', formatAmount);	//格式化金额-util.js
                	$("#orderList").processTemplate(json.data);
                	//
                	pageSel();
	                pageEnter();
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert(jqXHR.responseText);
                }
           });
    	}
    	
    	//翻页
    	function pageClick(sel_page) {
    		pageIndex = sel_page;
    		loadData_orderlist();	//加载数据-订单列表
    	}
    	//输入页码
    	function pageEnter() {
    		$('.ui-pager-inp').bind('keypress',function(event) {
                if(event.keyCode == "13") {
                	if (isInteger($(this).val())) {
                		if ($(this).val() > totalPage || $(this).val() < 1 || $(this).val() == pageIndex) {
                			return false;
                		}
                		else {
                			pageIndex = $(this).val();
                    		loadData_orderlist();	//加载数据-订单列表
                		}
                	}
                	else {
                	}
                    return false;
                }
            });
    	}
    	//切换每页显示条数
    	function pageSel() {
    		$('.pageSel').change(function(){ 
    			var p1=$(this).children('option:selected').val();//这就是selected的值
    			pageIndex = 1;
    			pageSize = p1;
    			loadData_orderlist();	//加载数据-订单列表
    		}) 
    	}
    	//订单详情
    	function orderdetails(id){
    		$("#admin_order").slideToggle(800,function(){
    			sel_orderid = "";
    			//alert("滑动向上缩小完毕");
    			
    		});
    		$("#admin_details").slideToggle(800,function(){
    			sel_orderid = id;
    			//alert("滑动向上缩小完毕");
    			loadData_orderdetails(id);
    		});
    	}
    	
    	//发货
    	function sendOut(id,isDetails){
    		layer.prompt({
   				title: '请输入运单号，并确认',
   				formType: 0 //prompt风格，支持0-2
   			}, function(deliverynum){
   				$.ajax({
   	    			async:false,
   	                url: "${pageContext.request.contextPath}/order/sendout.htmls",
   	                data: {orderid:id,deliverynum:deliverynum},
   	                type: "post",
   	                dataType:"text",
   	                success: function (text) {
   	                	if(text == "1"){
   	                		loadData_orderlist();
   	                		if (isDetails) {
   	                			loadData_orderdetails(id);
   	                		}
   	                		layer.msg("操作成功",{icon:6});
   	                	}else{
   	                		layer.msg("操作失败，请稍后再试！",{icon:6});
   	                	}
   	                },
   	                error: function (jqXHR, textStatus, errorThrown) {
   	                    alert(jqXHR.responseText);
   	                }
   	           	});
   			});
    	}
    	
    	function sendOut_details() {
    		if (sel_orderid == "") {
    			layer.msg("未获取到订单ID，请重新登录再尝试操作。");
    			return;
    		}
    		sendOut(sel_orderid,true);
    	}
    	
    	/*---------订单详情---------*/
    	
    	var total = 0,total_sum = 0;
    	//加载数据-订单详情
    	function loadData_orderdetails(orderid){
    		$.ajax({
    			async:false,
                url: "${pageContext.request.contextPath}/order/orderdetails.htmls",
                data: {orderid:orderid},
                type: "post",
                dataType:"json",
                success: function (json) {
                	var order = json.order;	//订单
                	if (order.orderstatus == 1) {
                		//如果订单状态大于2，隐藏确定发货按钮
                		$("#sdBtn").show();
                	}
                	else {
                		$("#sdBtn").hide();
                	}
                	$("#ordernum_d").html(order.ordernumber);
                	$("#ordertime").html(order.ordertime);
                	$("#ostatus").html(order.orderstatusmemo);
                	disposal(json.products);	//订单详情
                	var address = json.address;	//收货地址
                	var address_str = "收货人："+address.username+"，联系电话："+address.userphone+"，收货地址："+address.userpath;
                	$("#address").html(address_str);
                	//物流信息
                	orderLogistics(json.logisticsList);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert(jqXHR.responseText);
                }
           	});
    	}
    	
    	//订单物流信息
    	function orderLogistics(data){
    		//加载模板数据
    		$("#orderlogistics").setTemplateURL("${pageContext.request.contextPath}/view/order/tpl/order/orderlogistics.tpl");
            $("#orderlogistics").processTemplate(data);
    	}
    	
    	//处理数据-订单商品
    	function disposal(data){
    		//全局对象
    		var objArr = new Array();
    		total = 0;total_sum = 0;
    		for(i=0;i<data.length;i++){
    			objArr.push(initializeObj(data[i]));
    		}
    		//加载模板数据
    		$("#proList").setTemplateURL("${pageContext.request.contextPath}/view/order/tpl/order/orderdetails.tpl");
            $("#proList").processTemplate(objArr);
            //
            $("#total").html(formatFloat(total,2));
            $("#total_sum").html(formatFloat(total,2));
    	}
    	
    	//初始数据对象 - 订单商品
    	function initializeObj(data){
    		var obj = {};
    		obj.id = data.id;
    		obj.brandname = data.brandname;
			obj.productname = data.productname;
			obj.productsize = data.productsize;
			obj.price = data.price;		//售价
			obj.price_txt = formatFloat(data.price,2);		//售价
			obj.buyerdis = data.buyerdis;	//折扣
			obj.quantity = data.productnum; 	//数量
			var _subtotal = 0;
			if(parseFloat(data.buyerdis) > 0){
				_subtotal = data.price * data.buyerdis * data.productnum;	//小计
			}else{
				_subtotal = data.price * data.productnum;	//小计
			}
			obj.subtotal = _subtotal;
			obj.subtotal_txt = formatFloat(_subtotal,2);
			total = parseFloat(total) + parseFloat(obj.subtotal);
			return obj;
    	}
    	
    	//
    	function goback(){
    		$(".admin").slideToggle(800,function(){
    			sel_orderid = "";
    			//alert("滑动向上缩小完毕");
    		});
    	}
    </script>
</head>
<body>
	<%@ include file="/view/order/header.jsp" %>
	<!--内容-->
	<div class="layout" style="margin-bottom: 50px">
		<ul class="bread bg">
			<li><a href="${pageContext.request.contextPath}/order/dispatch.htmls?page=/view/order/index" class="icon-home">首页</a> </li>
			<li><a href="#" >客户订单</a></li>
		</ul>
		<div id="admin_order" class="admin" style="padding: 10px 60px;">
			<div class="padding float-left" style="padding-left: 0px;">
				<form onsubmit="return false;" class="form-inline" method="post">
					<div class="button-group">筛选：</div>
					<div class="button-group">
						<select id="orderstatus" class="input">
							<option value="">全部订单</option>
							<option value="0">待支付订单</option>
							<option value="1">待发货订单</option>
							<option value="2">已发货订单</option>
							<option value="3">已签收订单</option>
							<option value="-1">已取消订单</option>
						</select>
					</div>
					<div class="form-group">
						<div class="field">下单时间：
							<input type="text" placeholder="起始日期" size="12" id="startDate" class="input">至
							<input type="text" placeholder="截止日期" size="12" id="endDate" class="input">
						</div>
					</div>
					<div class="form-group">
						<div class="field">
							<input type="text" placeholder="订单号/机构" size="30" name="ordernum" id="keyword" class="input">
						</div>
					</div>
					<div class="form-button">
						<button type="submit" class="button icon-search"> 搜 索</button>
					</div>
				</form>
			</div>
			<div id="orderList" class="admin-panel">
				
			</div>
		</div>
		<!-- 订单详情 START -->
		<div id="admin_details" class="admin" style="padding: 0px 60px;display: none;">
			<div class="media-body">
				<div class="padding float-left"><h4>订单详情</h4></div>
				<div class="padding float-right">
					<button id="sdBtn" class="button button-small border-blue" onclick="sendOut_details()">确认发货</button>
					<button onclick="goback()" class="button button bg-blue icon-reply"> 返回</button>
				</div>
			</div>
			<div style="padding-bottom: 20px;">
				<ul class="list-group">
					<li>订单号：<strong id="ordernum_d"></strong>	<label style="padding-left: 30px;" id="ordertime"></label><span id="ostatus" class="tag bg-yellow float-right"></span></li>
					<li>【收货信息】<span id="address"></span></li>
				</ul>
			</div>
			<h4>订单商品</h4>
			<hr class="">
			<div style="padding-bottom: 20px;">
				<div id="proList" class="admin-panel">
				</div>
				<div class="text-right panel" style="padding: 30px 50px;">
					<div>合计：￥<span id="total">0.00</span></div>
					<br/>
					<div>应付总额：￥<span style="color: red !important" id="total_sum">0.00</span></div>
				</div>
			</div>
			<h4>物流信息</h4>
			<hr class="">
			<div id="orderlogistics" class="table-responsive">
			</div>
		</div>
		<!-- 订单详情  END -->
	</div>
	<!--底部-->
	<div class="layout fixed-bottom bg-white">
		<div class="border-bottom padding-bottom">
			<div class="text-center height-big text-small">
				<strong>版权所有 亚普软件(北京)有限公司&copy; <a href="#"></a> All Rights Reserved.</strong>
			</div>
		</div>
	</div>
	
</body>
</html>