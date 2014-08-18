Task = {};

/*****load task list*****/

Task.loadList = function(){
	/*****clear list*****/
	$("#tslist").empty();
	$.getJSON(URL.getTaskList() + "?jsoncallback=?")
		.done(function(data){
			var tsdata;
			if(data === null){
				tsdata = [];
			}else{
				tsdata = data.jTask;
			}
//			console.log(tsdata);
			
			/*****create table*****/
			var table = $("<table></table>");
			table.attr("id","tslist-table");
			table.attr("class","display");
			table.appendTo("#tslist");
			var thead = $("<thead></thead>");
			thead.appendTo(table);
			var tbody = $("<tbody></tbody>");
			tbody.appendTo(table);
			
			/*****create thead*****/
			var tr = $("<tr></tr>");
			tr.appendTo(thead);
			var title = ["创建日期","类别","状态","操作"];
			for(var i = 0; i < 4; i++){
				var th = $("<th></th>");
				th.appendTo(tr);
				th.text(title[i]);
			}
			
			/*****create tbody*****/
			if(tsdata.length === 0){
				tr = $("<tr></tr>");
				tr.appendTo(tbody);
				for(var i = 0; i < 4; i++){
					var td = $("<td></td>");
					td.appendTo(tr);
					if(i === 2){
						var span = $("<span></span>");
						span.attr("id","empty_tslist");
						span.appendTo(td);
						span.text("/");
					}else{
						td.text("/");
					}
				}
			}else if(Common.isObject(tsdata)){
				tr = $("<tr></tr>");
				tr.appendTo(tbody);
				Task.bulidRow(tsdata,tr);
			}else{
				for(var i = 0; i < tsdata.length; i++){
					tr = $("<tr></tr>");
					tr.appendTo(tbody);
					Task.bulidRow(tsdata[i],tr);
				}
			}
			
			$("#tslist-table").DataTable();
			
			/*****refresh list*****/
//			console.log(Common.refresh_id);
			if(Common.refresh_id != -1){
				window.clearInterval(Common.refresh_id);
			}
			Common.refresh_id = window.setInterval("Task.refreshList()",Common.refreshInterval());
//			console.log(Common.refresh_id);
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

/*****create one row*****/

Task.bulidRow = function(data,tr){
	var td = $("<td></td>");
	td.appendTo(tr);
	td.text(data.date);
	
	td = $("<td></td>");
	td.appendTo(tr);
	td.text(data.tasktype);
	
	td = $("<td></td>");
	td.appendTo(tr);
	td.html("<span id = '" + data.id + "'>" + data.taskstatus + "</span>");
	
	td = $("<td></td>");
	td.appendTo(tr);
	var html = "<input type = 'button' style = 'font-family: Times New Roman,楷体;' value = '查看' onclick = 'Task.showDetail(\"" + data.id + "\",\"open\")'/>";
	if(data.taskstatus === "RUNNING"){
		html += "<input type = 'button' style = 'font-family: Times New Roman,楷体;' value = '中止' onclick = 'Task.stop(\"" + data.id + "\")'/>";
	}else{
		html += "<input type = 'button' style = 'font-family: Times New Roman,楷体;' value = '删除' onclick = 'Task.remove(\"" + data.id + "\")'/>";
	}
	td.html(html);
};

/*****show task detail info*****/

Task.showDetail = function(ts_id,status){
//	console.log(ts_id);
	$.getJSON(URL.getTaskInfo() + "?jsoncallback=?" + "&id=" + ts_id)
		.done(function(tsdata){
//			console.log(tsdata);
			/*****clear tab*****/
			var cntr = $("#dtfragment-2");
			cntr.empty();
			/*****switch to this tab*****/
			if(status === "open"){
				$("#dttabs").tabs("option","active",1);
			}
			
			/*****create title & table container*****/
			var title = $("<span></span>");
			title.attr("class","dtfragment-2-title");
			title.appendTo(cntr);
			title.text("详细信息");
			var table_cntr = $("<div></div>");
			table_cntr.appendTo(cntr);
			table_cntr.attr("id","tstable-cntr");
			
			/*****create table 1*****/
			var table = $("<table></table>");
			table.attr("id","tstable-1");
			table.attr("class","display");
			table.appendTo(table_cntr);
			var thead = $("<thead></thead>");
			thead.appendTo(table);
			var tbody = $("<tbody></tbody>");
			tbody.appendTo(table);
			
			/*****create thead 1*****/
			var tr = $("<tr></tr>");
			tr.appendTo(thead);
			var title = ["创建日期","ID","类别","状态","操作"];
			for(var i = 0; i < title.length; i++){
				var th = $("<th></th>");
				th.appendTo(tr);
				th.text(title[i]);
			}
			
			/*****create tbody 1*****/
			tr = $("<tr></tr>");
			tr.appendTo(tbody);
			var td = $("<td></td>");
			td.appendTo(tr);
			td.text(tsdata.date);
			
			td = $("<td></td>");
			td.appendTo(tr);
			td.text(tsdata.id);
			
			td = $("<td></td>");
			td.appendTo(tr);
			td.text(tsdata.tasktype);
			
			td = $("<td></td>");
			td.appendTo(tr);
			td.html("<span id = '" + tsdata.id + "'>" + tsdata.taskstatus + "</span>");
			
			td = $("<td></td>");
			td.appendTo(tr);
			var html = "";
			if(tsdata.taskstatus === "RUNNING"){
				html = "<input type = 'button' style = 'font-family: Times New Roman,楷体;' value = '中止' onclick = 'Task.stop(\"" + tsdata.id + "\")'/>";
			}else{
				html = "<input type = 'button' style = 'font-family: Times New Roman,楷体;' value = '删除' onclick = 'Task.remove(\"" + tsdata.id + "\")'/>";
			}
			td.html(html);
			/*****if task done, download file*****/
			if(tsdata.taskstatus === "SUCCEEDED"){
				var a = $("<a target = '_self' href = '" + URL.download() + "?id=" + tsdata.id + "'></a>");
				a.appendTo(td);
				var input = $("<input type = 'button' style = 'font-family: Times New Roman,楷体;' value = '下载'/>");
				input.appendTo(a);
			}
			
			$("#tstable-1").DataTable({
				searching: false,
				ordering: false,
				paging: false,
				info: false
			});
			
			/*****create table 2*****/
			table = $("<table></table>");
			table.attr("id","tstable-2");
			table.attr("class","display");
			table.appendTo(table_cntr);
			thead = $("<thead></thead>");
			thead.appendTo(table);
			tbody = $("<tbody></tbody>");
			tbody.appendTo(table);
			
			/*****create thead 2*****/
			tr = $("<tr></tr>");
			tr.appendTo(thead);
			title = ["输入路径"];
			for(var i = 0; i < title.length; i++){
				var th = $("<th></th>");
				th.appendTo(tr);
				th.text(title[i]);
			}
			
			/*****create tbody 2*****/
			tr = $("<tr></tr>");
			tr.appendTo(tbody);
			td = $("<td></td>");
			td.appendTo(tr);
			td.text(tsdata.jdatasets);
			
			$("#tstable-2").DataTable({
				searching: false,
				ordering: false,
				paging: false,
				info: false
			});
			
			/*****create table 3*****/
			table = $("<table></table>");
			table.attr("id","tstable-3");
			table.attr("class","display");
			table.appendTo(table_cntr);
			thead = $("<thead></thead>");
			thead.appendTo(table);
			tbody = $("<tbody></tbody>");
			tbody.appendTo(table);
			
			/*****create thead 3*****/
			tr = $("<tr></tr>");
			tr.appendTo(thead);
			title = ["输出路径"];
			for(var i = 0; i < title.length; i++){
				var th = $("<th></th>");
				th.appendTo(tr);
				th.text(title[i]);
			}
			
			/*****create tbody 3*****/
			tr = $("<tr></tr>");
			tr.appendTo(tbody);
			td = $("<td></td>");
			td.appendTo(tr);
			td.text(tsdata.outputPath);
			
			$("#tstable-3").DataTable({
				searching: false,
				ordering: false,
				paging: false,
				info: false
			});
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

/*****create task*****/

Task.create = function(){
	/*****clear tab*****/
	var cntr = $("#dtfragment-2");
	cntr.empty();
	/*****switch to this tab*****/
	$("#dttabs").tabs("option","active",1);
	
	/*****create title & selector*****/
	var title = $("<span></span>");
	title.attr("class","dtfragment-2-title");
	title.appendTo(cntr);
	title.text("新建任务");
	var selector = $("<table></table>");
	selector.attr("class","dtfragment-2-selector");
	selector.appendTo(cntr);
	selector.css({
		"width": cntr.width()
	});
	
	/*****selector table: first row*****/
	var tr = $("<tr></tr>");
	tr.appendTo(selector);
	var td = $("<td></td>");
	td.appendTo(tr);
	td.html("选择任务类别:");
	$.getJSON(URL.getTaskType() + "?jsoncallback=?")
		.done(function(data){
//			console.log(data);
			td = $("<td></td>");
			td.appendTo(tr);
			var table = $("<table></table>");
			table.appendTo(td);
			for(var i = 0; i < data.length; i++){
				if(i % 3 === 0){
					tr = $("<tr></tr>");
					tr.appendTo(table);
				}
				td = $("<td></td>");
				td.appendTo(tr);
				var radio = $("<input/>");
				radio.appendTo(td);
				radio.attr("type","radio");
				radio.attr("name","dtfragment-2-task-type");
				radio.attr("value",data[i]);
				
				span = $("<span></span>");
				span.appendTo(td);
				span.html(data[i]);
			}
			
			/*****selector table: second row*****/
			tr = $("<tr></tr>");
			tr.appendTo(selector);
			td = $("<td></td>");
			td.appendTo(tr);
			td.html("选择数据集:");
			td = $("<td></td>");
			td.appendTo(tr);
			td.html("<input type = 'button' value = '打开数据集列表' onclick = 'Common.openWindow()'/>");
			
			/*****selector table: third row*****/
			tr = $("<tr></tr>");
			tr.appendTo(selector);
			td = $("<td></td>");
			td.appendTo(tr);
			td.html("参数:");
			td = $("<td></td>");
			td.appendTo(tr);
			var width = cntr.width() - selector.find("tr").eq(0).find("td").eq(0).width() - 46;
			var textarea = $("<textarea rows = '5'></textarea>");
			textarea.appendTo(td);
			textarea.css({
				"width": width
			});
			
			/*****create submit button*****/
			var button = $("<input type = 'button' value = '执行任务' onclick = 'Task.run()'/>");
			button.appendTo(cntr);
		}).fail(function(){
			alert("Oops, we got an error...");
		});
	Task.loadDslist();
};

/*****load dataset list as checkbox*****/

Task.loadDslist = function(){
	var window = $("#window");
	window.empty();
	var dstree = $("<div></div>");
	dstree.attr("id","dstree-float");
	dstree.appendTo(window);
	var ul = $("<ul></ul>");
	ul.attr("class","dslist-float");
	ul.appendTo(dstree);
	Dataset.loadList("dstree-float","checkbox");
	var img = $("<img/>");
	img.appendTo(window);
	img.attr("src","css/images/close_256x256.png");
	img.attr("onclick","Common.closeWindow()");
};

/*****run task*****/

Task.run = function(){
	/*****get task type*****/
	var type = $("input[name='dtfragment-2-task-type']:checked").val();
	if(type === undefined){
		alert("选择一类任务!");
		return;
	}
//	console.log(type);
	
	/*****get selected datasets*****/
	var datasets = $.parseJSON("[]");
	$.each($("#dstree-float").jstree("get_top_checked",true),function(index,value){
//		console.log(this.id);
		var path = Task.getPath(this.id);
		datasets[index] = "";
		for(var i = path.length - 1; i >= 0; i--){
			datasets[index] += path[i];
			if(i != 0){
				datasets[index] += "/";
			}
		}
	});
//	console.log(datasets);
	if(datasets.length === 0){
		alert("选择一个数据集!");
		return;
	}
	
	/*****get parameter text*****/
	var tr = $("#dtfragment-2").children("table").children("tbody").children("tr").eq(2);
	var param = tr.children("td").eq(1).children("textarea").val();
//	console.log(param);
	
	/*****run task*****/
	$.getJSON(URL.runTask() + "?jsoncallback=?" + "&type=" + type + "&datasets=" + JSON.stringify(datasets) + "&param=" + param)
		.done(function(data){
//			console.log(data);
			/*****alert info*****/
			if(data.status === "RUNNING"){
				alert("成功新建任务!");
				/*****reload task list*****/
				Task.loadList();
			}
			if(data.status === "FAILED"){
				alert("新建任务失败!");
			}
			
			/*****close & reload checkbox window*****/
			Common.closeWindow();
			Task.loadDslist();
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

/*****get dataset file path*****/

Task.getPath = function(node_id){
	var node_text = [];
	node_text[0] = $("#" + node_id).children("a").text();
	var parent = $("#" + node_id).parents("li");
	for(var i = 0; i < parent.length; i++){
		node_text[i + 1] = parent.eq(i).children("a").text();
	}
	return node_text;
};

/*****refresh task list*****/

Task.refreshList = function(){
	var tr = $("#tslist").children("div").children("table").children("tbody").children("tr");
	for(var i = 0; i < tr.length; i++){
		Task.refreshStatus(i);
	}
};

/*****refresh task status*****/

Task.refreshStatus = function(index){
	/*****get task id*****/
	var tr = $("#tslist").children("div").children("table").children("tbody").children("tr").eq(index);
	var span = tr.children("td").eq(2).children("span");
	if(span.text() != "RUNNING"){
		return;
	};
	var id = span.attr("id");
//	console.log(id);
	
	/*****get task status*****/
	$.getJSON(URL.getTaskStatus() + "?jsoncallback=?" + "&id=" + id)
		.done(function(data){
			/*****set new status*****/
			var new_text = data.taskstatus;
//			console.log(new_text);
			span.text(new_text);
			
			/*****if status changes, change button; if tab shows this task's detail info, refresh it*****/
			if(new_text != "RUNNING"){
				var new_html = "<input type = 'button' style = 'font-family: Times New Roman,楷体;' value = '查看' onclick = 'Task.showDetail(\"" + id + "\",\"open\")'/>";
				new_html += "<input type = 'button' style = 'font-family: Times New Roman,楷体;' value = '删除' onclick = 'Task.remove(\"" + id + "\")'/>";
				var td = tr.children("td").eq(3);
				td.html(new_html);
				
				if($("#tstable-cntr").length){
					var tr_detail = $("#tstable-cntr").children("div").eq(0).children("table").children("tbody").children("tr").eq(0);
					var span_detail = tr_detail.children("td").eq(3).children("span");
					var id_detail = span_detail.attr("id");
//					console.log(id_detail);
					if(id === id_detail){
						Task.showDetail(id,"refresh");
					}
				}
			}
		}).fail(function(){
			/*****refresh browser error*****/
//			alert("Oops, we got an error...");
			console.log("Oops, we got an error...");
		});
};

/*****stop task*****/

Task.stop = function(ts_id){
//	console.log(ts_id);
	$.getJSON(URL.stopTask() + "?jsoncallback=?" + "&id=" + ts_id)
		.done(function(data){
//			console.log(data);
			/*****alert info*****/
			alert("成功中止任务!");
			/*****reload task list*****/
			Task.loadList();
			/*****if tab shows this task's detail info, refresh it*****/
			if($("#tstable-cntr").length){
				Task.showDetail(ts_id,"refresh");
			}
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

/*****remove task*****/

Task.remove = function(ts_id){
//	console.log(ts_id);
	$.getJSON(URL.removeTask() + "?jsoncallback=?" + "&id=" + ts_id)
		.done(function(data){
//			console.log(data);
			/*****alert info*****/
			alert("成功删除任务记录!");
			/*****reload task list*****/
			Task.loadList();
			/*****if tab shows this task's detail info, clear it*****/
			if($("#tstable-cntr").length){
				$("#dtfragment-2").empty();
			}
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};