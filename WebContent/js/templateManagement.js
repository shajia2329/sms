var ed = null;
var editIndex = undefined;
$(document).ready(function(){
	/**
	 * 模板列表
	 */
	$('#template_list').datagrid({
		url:'/sms/SmsTemplateController/getTemplateList',
		toolbar:'#menu',
		fit:true,
		fitColumns:true,
		pagination:true,
		striped:true,
		nowrap:false,
		singleSelect:true,
		pageList:[10,50,100],
		rownumbers:true,
		columns : [[{
			field : 'template_code',
			width : 100,
			align:'center',
			title : '阿里大鱼模板编号'
		},{
			field : 'template_name',
			width : 100,
			align : 'center',
			title : '模板名称',
		},{
			field : 'create_at',
			width : 100,
			align:'center',
			title : '创建时间'
		},{
			field : 'update_at',
			width : 100,
			align:'center',
			title : '更新时间'
		},{
			field : 'enable_or_disenable',
			width : 50,
			align:'center',
			title : '删除',
			formatter: function(value,row,index){
//				var str = "";
//				str = (value == 0 ? "启用" : "禁用");
				var html = "";
				html = "<a href='javascript:void(0)' onclick='enableAndDisenable(this)' id='" + row.id + "' value='" + value + "'>" + "删除" + "</a>";
				return html;
			}
		}]],
		view: detailview,
		detailFormatter: function(rowIndex, rowData){
			return "<table><tr><td>模板内容：</td><td>"+ rowData.template_content +"</td></tr></table>";
		}
	});
	/**
	 * 添加模板按钮响应
	 */
	$('#add_template_btn').click(function(){
		$('#input_template_code').val('');
		$('#input_template_name').val('');
		$('#input_template_content').val('');
		$('#excel_column_list').datagrid('loadData',{total:0,rows:[]});
		$('#add_template_dialog').dialog('open');
		addColumn();//添加初始行
	});
	/**
	 * 配置模板dialog
	 */
	$('#add_template_dialog').dialog({
		title: '配置模板',
		width: 700,
		height: 600,
		closed: true,
		cache: false,
		modal: true,
		buttons:[{
			text:'验证',
			handler:function(){
				if (endEditing()){
					var template_code = $('#input_template_code').val();
					var template_name = $('#input_template_name').val();
					var template_content = $('#input_template_content').val();
					if (template_code != "" && template_name != "" && template_content != ""){
						$('#validate_template').dialog('open');
						$('#input_phone_num').focus();
					}
				}
			}
		},{
			text:'关闭',
			handler:function(){
				$('#add_template_dialog').dialog('close');
			}
		}]
	});
	/**
	 * 大鱼模板编号输入框
	 */
	$('#input_template_code').validatebox({
		required: true
	});
	/**
	 * 模板名称输入框
	 */
	$('#input_template_name').validatebox({
		required: true
	});
	/**
	 * 模板内容输入框
	 */
	$('#input_template_content').validatebox({
		required: true
	});
	/**
	 * 验证模板电话号码
	 */
	$('#input_phone_num').validatebox({
		required: true,
		validType:["numeric","length[11,11]"]
	});
	
	/**
	 * 配置模板的datagrid
	 */
	$('#excel_column_list').datagrid({
		toolbar:'#add_excel_column_menu',
		fit:true,
		fitColumns:false,
		striped:true,
		nowrap:false,
		singleSelect:true,
		rownumbers:true,
		columns:[[{
			field : 'excel_column_number',
			align:'center',
			width : 300,
			title : 'excel列号',
			editor:{
				type:"validatebox",
				options: {
					validType:'letter',
					required: true
				}
			}
		},{
			field : 'excel_column_name',
			align:'center',
			width : 300,
			title : 'excel列名',
			editor:{
				type:"validatebox",
				options: {
					required: true
				}
			}
		},{
			field:'operate',
			title:'操作',
			width:30,
			align:'center',
			formatter: function(value,row,index){
				var returnHtml = "";
				returnHtml += "<a href='javascript:void(0)' target='_blank' class='easyui-linkbutton' onclick='delete_column(this)' title='删除' index=" + index + "><img src='images/delete.png'></img></a>";
				return returnHtml;
			}
		}]],
		onClickRow:function(index,rowData) {
			if (editIndex != index){
				if (endEditing()){
					$('#excel_column_list').datagrid('selectRow', index).datagrid('beginEdit', index);
					editIndex = index;
				} else {
					$('#excel_column_list').datagrid('selectRow', editIndex);
				}
			}
		}
	});
	/**
	 * 添加配置模板的datagrid的行
	 */
	$('#add_excel_column_btn').click(function(){
		if (endEditing()) {
			addColumn();
		}
	});
	
	/**
	 * 验证模板，填写电话号码的dialog
	 */
	$('#validate_template').dialog({
		title: '填写手机号码',
		width: 300,
		height: 200,
		closed: true,
		cache: false,
		modal: true,
		buttons:[{
			text:'验证并保存',
			handler:function(){
				var template_code = $('#input_template_code').val();
				var template_name = $('#input_template_name').val();
				var template_content = $('#input_template_content').val();
				var datagridDatas = $('#excel_column_list').datagrid('getRows');
				var phone_num = $('#input_phone_num').val();
				if ($('#input_phone_num').validatebox('isValid')) {
					$.ajax({
						type:'POST',
						dataType:'json',
						data:{"template_code":template_code,"template_name":template_name,"template_content":template_content,"datagridDatas":JSON.stringify(datagridDatas),"phone_num":phone_num},
						url:'/sms/SmsTemplateController/addTemplate',
						success:function(result){
							if (result.state == "success") {
								$.messager.show({
									title:'消息',
									msg:result.msg,
									timeout:3000,
									showType:'slide'
								});
								$('#input_phone_num').val('');
								$('#validate_template').dialog('close');
								$('#add_template_dialog').dialog('close');
								$('#template_list').datagrid('reload');
							} else {
								$.messager.alert('错误',result.msg,'error');
							}
						}
					});
				}
			}
		},{
			text:'关闭',
			handler:function(){
				$('#input_phone_num').val('');
				$('#validate_template').dialog('close');
			}
		}]
	});
	
	/**
	 * 下载模板按钮响应
	 */
	$('#download_template_btn').click(function(){
		var selected = $('#template_list').datagrid('getSelected');
		if (selected != null){
			if(selected.enable_or_disenable != "0") {
				$('#downloadFile').form('submit',{
					url:'/sms/SmsTemplateController/downloadTemplate',
					onSubmit: function(param){
						param.id = selected.id;
					}
				});
			} else {
				$.messager.alert('错误','该模板已经禁用，不可下载！','error');
			}
		} else {
			$.messager.alert('错误','请在列表中选择一个要下载的模板！','error');
		}
	});
});

/**
 * validatebox扩展验证
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
	},
	letter:{
		validator:function(value,param){
			if (value){
				return /^[a-zA-Z]+$/.test(value);
			} else {
				return true;
			}
		},
		message:"只能输入字母"
	}
});

/**
 * 设置模板启用和禁用
 */
function enableAndDisenable(param) {
	var id = $(param).attr("id");
	$.ajax({
		type:"POST",
		datatype:"json",
		data:{"id":id},
		url:"/sms/SmsTemplateController/setEnableOrDisenable",
		success:function(result){
			$('#template_list').datagrid('reload');
			$.messager.show({
				title:'提示',
				msg:result.msg,
				timeout:3000,
				showType:'slide'
			});
		}
	});
}
/**
 * 增加一行
 */
function addColumn(){
	var appendIndex = $('#excel_column_list').datagrid('appendRow',{}).datagrid('getRows').length-1;
	$('#excel_column_list').datagrid('beginEdit',appendIndex);
	ed = $('#excel_column_list').datagrid('getEditor', {index:appendIndex,field:'excel_column_number'});
	ed.target.focus();
	lastEditIndex = appendIndex;
	$('#excel_column_list').datagrid('selectRow',appendIndex);
	editIndex = appendIndex;
}
/**
 * 结束编辑
 * @returns {Boolean}
 */
function endEditing(){
	if (editIndex == undefined){
		return true;
	}
	if ($('#excel_column_list').datagrid('validateRow', editIndex)){
		$('#excel_column_list').datagrid('endEdit', editIndex);
		editIndex = undefined;
		return true;
	} else {
		return false;
	}
}
/**
 * 处理删除excelcolumn的按键响应
 */
function delete_column(param){
	var index = $(param).attr("index");
	if (index == "0") {
		return;
	}
	if ($('#excel_column_list').datagrid('validateRow', index)) {
		var datas = $('#excel_column_list').datagrid('getRows');
		$('#excel_column_list').datagrid('deleteRow',index);
		$('#excel_column_list').datagrid('loadData',datas);
	}
}
