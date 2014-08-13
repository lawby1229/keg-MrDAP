Task = {};

Task.loadList = function(){
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
			
			google.load("visualization","1",{packages:["table"],"callback":drawTable});
			function drawTable(){
				var data = new google.visualization.DataTable();
				data.addColumn('string',"创建日期");
				data.addColumn('string',"类别");
				data.addColumn('string',"状态");
				data.addColumn('string',"操作");
				
				var array = $.parseJSON("[]");
				if(tsdata.length === 0){
					array[0] = $.parseJSON("[]");
					array[0][0] = "/";
					array[0][1] = "/";
					array[0][2] = "<span id = 'empty_tslist'>/</span>";
					array[0][3] = "/";
				}else if(Common.isObject(tsdata)){
					array[0] = $.parseJSON("[]");
					Task.bulidArray(tsdata,array[0]);
				}else{
					for(var i = 0; i < tsdata.length; i++){
						array[i] = $.parseJSON("[]");
						Task.bulidArray(tsdata[i],array[i]);
					}
				}
//				console.log(array);
				
				data.addRows(array);
				var table = new google.visualization.Table(document.getElementById("tslist"));
				table.draw(data, {showRowNumber: true, allowHtml: true});
				$("#tslist .google-visualization-table-th:contains(操作)").css({
					"width": "90px"
				});
			}
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Task.bulidArray = function(data,array){
	array[0] = data.date;
	array[1] = data.tasktype;
	array[2] = "<span id = '" + data.id + "'>" + data.taskstatus + "</span>";
	array[3] = "<input type = 'button' value = '查看' onclick = 'Task.showDetail(\"" + data.id + "\",\"open\")'/>";
	array[3] += "&nbsp;<input type = 'button' value = '删除' onclick = 'Task.remove(\"" + data.id + "\")'/>";
	if(data.taskstatus === "RUNNING"){
		array[3] += "<br/><input type = 'button' value = '中止' onclick = 'Task.stop(\"" + data.id + "\")'/>";
	}
};

Task.showDetail = function(ts_id,status){
//	console.log(ts_id);
	$.getJSON(URL.getTaskInfo() + "?jsoncallback=?" + "&id=" + ts_id)
		.done(function(tsdata){
//			console.log(tsdata);
			var cntr = $("#dtfragment-2");
			cntr.empty();
			if(status === "open"){
				$("#dttabs").tabs("option","active",1);
			}
			
			var title = $("<span></span>");
			title.attr("class","dtfragment-2-title");
			title.appendTo(cntr);
			title.text("详细信息");
			var tstable = $("<div></div>");
			tstable.appendTo(cntr);
			tstable.attr("id","tstable");
			
			google.load("visualization","1",{packages:["table"],"callback":drawTable});
			function drawTable(){
				var data = new google.visualization.DataTable();
				data.addColumn('string',"创建日期");
				data.addColumn('string',"ID");
				data.addColumn('string',"类别");
				data.addColumn('string',"状态");
				data.addColumn('string',"操作");
				
				var array = $.parseJSON("[]");
				array[0] = $.parseJSON("[]");
				array[0][0] = tsdata.date;
				array[0][1] = tsdata.id;
				array[0][2] = tsdata.tasktype;
				array[0][3] = "<span id = '" + tsdata.id + "'>" + tsdata.taskstatus + "</span>";
				array[0][4] = "<input type = 'button' value = '删除' onclick = 'Task.remove(\"" + tsdata.id + "\")'/>";
				if(tsdata.taskstatus === "RUNNING"){
					array[0][4] += "&nbsp;<input type = 'button' value = '中止' onclick = 'Task.stop(\"" + tsdata.id + "\")'/>";
				}
//				console.log(array);
				
				data.addRows(array);
				var table = new google.visualization.Table(document.getElementById("tstable"));
				table.draw(data,{allowHtml: true});
			}
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Task.create = function(){
	var cntr = $("#dtfragment-2");
	cntr.empty();
	$("#dttabs").tabs("option","active",1);
	
	var title = $("<span></span>");
	title.attr("class","dtfragment-2-title");
	title.appendTo(cntr);
	title.text("新建任务");
	var selector = $("<table></table>");
	selector.attr("class","dtfragment-2-selector");
	selector.appendTo(cntr);
	
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
			
			tr = $("<tr></tr>");
			tr.appendTo(selector);
			td = $("<td></td>");
			td.appendTo(tr);
			td.html("选择数据集:");
			td = $("<td></td>");
			td.appendTo(tr);
			td.html("<input type = 'button' value = '打开数据集列表' onclick = 'Common.openWindow()'/>");
			
			var button = $("<input type = 'button' value = '执行任务' onclick = 'Task.run()'/>");
			button.appendTo(cntr);
		}).fail(function(){
			alert("Oops, we got an error...");
		});
	Task.loadDslist();
};

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

Task.run = function(){
	var type = $("input[name='dtfragment-2-task-type']:checked").val();
	if(type === undefined){
		alert("选择一类任务!");
		return;
	}
//	console.log(type);
	
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
	
	$.getJSON(URL.runTask() + "?jsoncallback=?" + "&type=" + type + "&datasets=" + JSON.stringify(datasets))
		.done(function(data){
//			console.log(data);
			alert("成功新建任务!");
			Common.closeWindow();
			Task.loadDslist();
			Task.loadList();
//			console.log(Common.refresh_id);
			window.clearInterval(Common.refresh_id);
			Common.refresh_id = window.setInterval("Task.refreshList()",Common.refreshInterval());
//			console.log(Common.refresh_id);
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Task.getPath = function(node_id){
	var node_text = [];
	node_text[0] = $("#" + node_id).children("a").text();
	var parent = $("#" + node_id).parents("li");
	for(var i = 0; i < parent.length; i++){
		node_text[i + 1] = parent.eq(i).children("a").text();
	}
	return node_text;
};

Task.refreshList = function(){
	var tr = $("#tslist").children("div").children("div").eq(0).children("table").children("tbody").children("tr");
	for(var i = 1; i < tr.length; i++){
		Task.refreshStatus(i);
	}
};

Task.refreshStatus = function(index){
	var tr = $("#tslist").children("div").children("div").eq(0).children("table").children("tbody").children("tr");
	var span = tr.eq(index).children("td").eq(3).children("span");
	var id = span.attr("id");
//	console.log(id);
	if(id === "empty_tslist"){
		return;
	}
	$.getJSON(URL.getTaskStatus() + "?jsoncallback=?" + "&id=" + id)
		.done(function(data){
			var new_text = data.taskstatus;
//			console.log(new_text);
			var old_text = span.text();
			span.text(new_text);
			
			if(old_text === "RUNNING"){
				if(new_text != "RUNNING"){
					var new_html = "<input type = 'button' value = '查看' onclick = 'Task.showDetail(\"" + id + "\",\"open\")'/>";
					new_html += "&nbsp;<input type = 'button' value = '删除' onclick = 'Task.remove(\"" + id + "\")'/>";
					var td = tr.eq(index).children("td").eq(4);
					td.html(new_html);
				}
			}
			if(old_text != new_text){
				if($('#tstable').length){
					var tr_detail = $("#tstable").children("div").children("div").eq(0).children("table").children("tbody").children("tr").eq(1);
					var span_detail = tr_detail.children("td").eq(3).children("span");
					var id_detail = span_detail.attr("id");
//					console.log(id_detail);
					if(id === id_detail){
						Task.showDetail(id,"refresh");
					}
				}
			}
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Task.stop = function(ts_id){
//	console.log(ts_id);
	$.getJSON(URL.stopTask() + "?jsoncallback=?" + "&id=" + ts_id)
		.done(function(data){
//			console.log(data);
			alert("成功中止任务!");
			Task.loadList();
//			console.log(Common.refresh_id);
			window.clearInterval(Common.refresh_id);
			Common.refresh_id = window.setInterval("Task.refreshList()",Common.refreshInterval());
//			console.log(Common.refresh_id);
			if($('#tstable').length){
				Task.showDetail(ts_id,"refresh");
			}
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Task.remove = function(ts_id){
//	console.log(ts_id);
	$.getJSON(URL.removeTask() + "?jsoncallback=?" + "&id=" + ts_id)
		.done(function(data){
//			console.log(data);
			alert("成功删除任务记录!");
			Task.loadList();
//			console.log(Common.refresh_id);
			window.clearInterval(Common.refresh_id);
			Common.refresh_id = window.setInterval("Task.refreshList()",Common.refreshInterval());
//			console.log(Common.refresh_id);
			if($('#tstable').length){
				$("#dtfragment-2").empty();
			}
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};