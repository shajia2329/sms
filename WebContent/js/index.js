var globalInterval;
var uuid;//uuid用于查询发送进度
$(document).ready(function(){
	
	getBatchNumber();//获取批次号
	
	$('#import_text').validatebox({
		required: true,
		tipPosition:'right',
		missingMessage:'请选择文件'
	});
	
	$('#progress').dialog({
		title:'温馨提示',
		width:400,
		height:90,
		modal:true,
		closed:true,
		closable:false
	});
	
	$('#progress2').dialog({
		title:'温馨提示',
		width:400,
		height:90,
		modal:true,
		closed:true,
		closable:false
	});
	
	$('#progress1').dialog({
		title:'温馨提示',
		width:400,
		height:300,
		modal:true,
		closed:true,
		closable:false,
		buttons:[{
			text:'关闭',
			handler:function(){
//				$('#list_datagrid').datagrid('reload');
				$('#progress1').dialog('close');
			}
		}]
	});
	/**
	 * 统计查询的dialog
	 */
	$('#chat_div').dialog({
		title:'发送统计',
		width:600,
		height:400,
		modal:true,
		closed:true,
		closable:false,
		buttons:[{
			text:'关闭',
			handler:function(){
				$('#chat_div').dialog('close');
			}
		}]
	});
	/**
	 * 发送统计
	 */
	$('#chat_datagrid').datagrid({
		fit:true,
		fitColumns:true,
		rownumbers:true,
		columns : [[{
			field : 'department',
			width : 80,
			align:'center',
			title : '发送部门'
		},{
			field : 'date',
			width : 80,
			align:'center',
			title : '日期'
		},{
			field : 'send_sum',
			width : 80,
			align:'center',
			title : '发送总数'
		},{
			field : 'qualified_sum',
			width : 80,
			align:'center',
			title : '早9点30之前数量'
		}]]
	});
	
	/**
	 * 发送统计dialog
	 */
	$('#wait_receipt_count').dialog({
		title:'发送统计',
		width:600,
		height:400,
		modal:true,
		closed:true,
		closable:false,
		buttons:[{
			text:'关闭',
			handler:function(){
				$('#wait_receipt_count').dialog('close');
				$('#select_date_datebox').datebox('clear');
			}
		}]
	});
	
	$('#select_date_datebox').datebox({
		editable:false,
		onSelect: function(date){
			$('#wait_receipt_count_datagrid').datagrid('reload',{
				"click_date":$('#select_date_datebox').datebox('getValue')
			});
		}
	});
	
	/**
	 * 待回执datagrid
	 */
	$('#wait_receipt_count_datagrid').datagrid({
		url:'/sms/MessageInfoController/getTongJiData',
		fit:true,
		fitColumns:true,
		rownumbers:true,
		queryParams:{"click_date":null},
		columns : [[{
			field : 'wait_receipt_count',
			width : 80,
			align:'center',
			title : '待回执数量'
		},{
			field : 'success_count',
			width : 80,
			align:'center',
			title : '成功数量'
		},{
			field : 'fail_count',
			width : 80,
			align:'center',
			title : '失败数量'
		},{
			field : 'sum_count',
			width : 80,
			align:'center',
			title : '发送总数'
		}]]
	});
	
	/**
	 * 统计查询按键响应
	 */
	$('#tongji_btn').click(function() {
		$.ajax({
			url:'/sms/MessageInfoController/getChatData',
			dataType:'json',
			async:false,
			type:'post',
			success:function(data) {
				$('#chat_datagrid').datagrid('loadData',data);
				$('#chat_div').dialog('open');
			}
		});
	});
	
	$('#sum_tongji_btn').click(function() {
		$('#wait_receipt_count').dialog('open');
		$('#wait_receipt_count_datagrid').datagrid('reload',{
			"click_date":null
		});
	});
	
	/*
	 *导入数据按键响应 
	 */
	$('#import_submit').on('click',function(){
		if(form1.file1.value==""){
			$.messager.alert('错误','请上传文件!');
			return false;
		}
		console.info(uuid);
		$('#progress').dialog('open');
//		globalInterval = setInterval("getProgress(this)",1000);//启动定时器
		$('#importForm').form('submit',{
			 url:'/sms/MessageInfoController/importMessageExcel',
			 onSubmit:function(param){
				 param.batchNumber = uuid;
			 },
			 success:function(data){
				 getBatchNumber();
				 $('#progress').dialog('close');
				 data = $.parseJSON(data);
				 if(data.state == "success") {
					 console.info(data);
//					 clearInterval(globalInterval);//清除定时器
					 $.messager.alert('温馨提示',data.msg,'info',function(){
						 $('#list_datagrid').datagrid('reload');
					 });
				 } else {
					 console.info(data);
					 $('#errorMassge').val(data.msg);
					 console.info($('#errorMassge').val());
					 $('#progress1').dialog('open');
				 }
				 $('#checkData').html("已处理0%");
			 }
		});
	});
	
	/*
	 * 选择文件按键响应
	 */
	$('input#import_file').on('change',function(){
		var v = $.trim($(this).val());
		var arr=new Array();
		arr=v.split('\\');
		$('input#import_text').val(arr[arr.length-1]);
		$('input#import_text').validatebox('validate');
		var typeValue = form1.file1.value;
		var type = typeValue.substring(typeValue.lastIndexOf("\\")+1,typeValue.length);
		type = type.substring(type.lastIndexOf("\.")+1,type.length);
		if (type.toUpperCase()=="XLSX" || type.toUpperCase()=="XLS") {
			return true;
		}else {
			$.messager.alert('文件格式错误！','这不是有效的Excel文件，请检查文件是否正确!(传入的文件后缀名应为.xlsx或.xls)');
			document.form1.reset();
			return false;
		}
	});
	
	$('#input_sample_number').validatebox({
		required:true,
		validType:'positive_int'
	});
	
	$('#search_btn').click(function() {
		if($('#searchForm').form('validate')) {
			var inputCompany = $('#input_company').combobox('getValue');
			var inputSystem = $('#input_system').combobox('getValue');
			var inputStatus = $('#input_status').combobox('getValues');
			var dateStart = $('#input_date_start').datebox('getValue');
			var dateEnd = $('#input_date_end').datebox('getValue');
			var sampleNum = $('#input_sample_number').val();
			var statusArr = [];
			for(var i=0; i<inputStatus.length; i++) {
				if (inputStatus[i] != "") {
					statusArr.push(inputStatus[i]);
				}
			}
			console.info(statusArr);
			$('#progress2').dialog('open');
			$.ajax({
				url:'/sampletool/ImportDataController/randomSearch',
				dataType:'json',
				type: "POST",
				data:{inputStatus:statusArr,inputCompany:inputCompany,inputSystem:inputSystem,dateStart:dateStart,dateEnd:dateEnd,sampleNum:sampleNum},
				traditional:true,
				success:function(r){
					if(r.result == true) {
						$('#list_datagrid').datagrid('loadData',r.data);
						$('#progress2').dialog('close');
					} else {
						$('#progress2').dialog('close');
						$.messager.alert('错误',r.msg,'error');
					}
				}
			});
			
		} else {
			console.info('param invalid');
			return;
		}
	});
	
	/*
	 * 短信列表
	 */
	$('#list_datagrid').datagrid({
		url:'/sms/MessageInfoController/getMessageList',
		fit:true,
		toolbar:'#resend',
		fitColumns:true,
		pagination:true,
		striped:true,
		nowrap:false,
		singleSelect:false,
		pageSize:20,
		pageList:[20,50,100],
		queryParams:{fromlist:true},	//获取数据时，发送的额外参数（用来判断是否来自查询 true:不是来自查询）
		rownumbers:true,
		columns : [[{
			field : 'send_to',
			width : 80,
			align:'center',
			title : '客户姓名'
		},{
			field : 'phone_number',
			width : 100,
			align : 'center',
			title : '手机号码',
		},{
			field : 'message_content',
			width : 300,
			align:'center',
			title : '短信内容'
		},{
			field : 'department',
			width : 80,
			align:'center',
			title : '发送部门'
		},{
			field : 'send_person',
			width : 50,
			align:'center',
			title : '信息管理员'
		},{
			field : 'send_time',
			width : 100,
			align:'center',
			title : '发送时间'
		},{
			field : 'state',
			width : 50,
			align:'center',
			title : '发送状态'
		}]],
		onSelect:function(rowIndex, rowData) {
			if (rowData.state=="发送成功" || rowData.state=="等待回执") {
				$('#list_datagrid').datagrid('uncheckRow',rowIndex);
			}
		}
	});
	/**
	 * 失败全选按钮响应
	 */
	$('#select_all_fail_btn').click(function() {
		$('#list_datagrid').datagrid('unselectAll');
		var rows = $('#list_datagrid').datagrid('getRows');
		for (var i=0; i<rows.length; i++) {
			if (rows[i].state == "发送失败") {
				$('#list_datagrid').datagrid('selectRow',i);
			}
		}
	});
	/**
	 * 重发按钮响应
	 */
	$('#resend_btn').click(function() {
		var rows = $('#list_datagrid').datagrid('getSelections');
		console.info(rows);
		if (rows.length > 0) {
			var ids = [];
			for (var i=0; i<rows.length; i++) {
				ids.push(rows[i].id);
			}
			$('#progress2').dialog('open');
			console.info(ids);
			$.ajax({
				url:'/sms/MessageInfoController/sendFailMessage',
				dataType:'json',
				type: "POST",
				traditional:true,
				data:{"ids":ids},
				success:function(data){
					$('#progress2').dialog('close');
					if (data.result == true) {
						$.messager.alert('温馨提示',data.msg,'info',function(){
							 $('#list_datagrid').datagrid('reload');
						 });
					} else {
						$.messager.alert('错误','系统错误','error');
					}
				}
			});
		} else {
			$.messager.alert('错误',"请选择" + "\"发送失败\"" + "的短信！",'error');
		}
	});
});


/*
 * 扩展validatebox
 */
$.extend($.fn.validatebox.defaults.rules, {
	positive_int:{
		validator:function(value,param){
			if (value){
				return /^[0-9]*[1-9][0-9]*$/.test(value);
			} else {
				return true;
			}
		},
		message:"只能输入正整数"
	},
	numeric:{
		validator:function(value,param){
			if (value){
				return /^[0-9]*(\.[0-9]+)?$/.test(value);
			} else {
				return true;
			}
		},
		message:"只能输入数字"
	}
});

function getProgress(interval) {
	$.ajax({
		url:'/sms//',
//		type:'json',
		type: "POST",
		success:function(data){
			$('#checkData').html("已处理" + data + "%");
			console.info(data);
		},
		error:function(){
			clearInterval(globalInterval);
		}
	});
}

function formReset(){
	$('#searchForm').form('reset');
}
/**
 * 获取批次号
 */
function getBatchNumber() {
	$.ajax({
		url:'/sms/MessageInfoController/getUUID',
		type: "POST",
		success:function(data){
			uuid = data;
		}
	});
}