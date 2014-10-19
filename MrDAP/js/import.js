Import = {};

Import.loadModule = function(){
	var cntr = $("#tslist");
	var table = $("<table></table>");
	table.appendTo(cntr);
	table.css({
		"width": cntr.width()
	});
	var tr = $("<tr></tr>");
	tr.appendTo(table);
	var td = $("<td></td>");
	td.appendTo(tr);
	td.text("输入文件路径");
	td = $("<td></td>");
	td.appendTo(tr);
	var input = $("<input/>");
	input.appendTo(td);
	input.attr("type","text");
	
	tr = $("<tr></tr>");
	tr.appendTo(table);
	td = $("<td></td>");
	td.appendTo(tr);
	td.text("选择文件类别");
	td = $("<td></td>");
	td.appendTo(tr);
	var select = $("<select></select>");
	select.appendTo(td);
	var option = $("<option></option>");
	option.appendTo(select);
	option.attr("value","MRO");
	option.text("MRO");
	option = $("<option></option>");
	option.appendTo(select);
	option.attr("value","MRS");
	option.text("MRS");
	
	var button = $("<input/>");
	button.appendTo(cntr);
	button.attr("type","button");
	button.attr("value","导入数据");
	button.attr("onclick","Import.loadData();");
};

Import.loadData = function(){
	var tr = $("#tslist").children("table").children("tbody").children("tr");
	
	var input = tr.eq(0).children("td").eq(1).children("input").val();
	console.log(input);
	if(input === ""){
		alert("必须输入文件路径!");
		return;
	}
	
	var type = tr.eq(1).children("td").eq(1).children("select").val();
	console.log(type);
	
	$.getJSON(URL.importData() + "?jsoncallback=?&input=" + input + "&type=" + type)
		.done(function(data){
			console.log(data);
			/*****alert info*****/
			if(data.status === "OK"){
				alert("成功新建任务!");
				/*****reload task list*****/
				Import.loadRunningList();
			}
			if(data.status === "FAILED"){
				alert("新建任务失败!");
			}
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Import.loadRunningList = function(){
$("#dtfragment-1").empty();
	$.getJSON(URL.importList() + "?jsoncallback=?")
		.done(function(data){
			console.log(data);
			if(data === null){
				data = [];
			}
			
			var table = $("<table></table>");
			table.attr("id","dtfragment-1-table");
			table.attr("class","display");
			table.appendTo("#dtfragment-1");
			var thead = $("<thead></thead>");
			thead.appendTo(table);
			var tbody = $("<tbody></tbody>");
			tbody.appendTo(table);
			
			var tr = $("<tr></tr>");
			tr.appendTo(thead);
			var title = ["ID","进度","操作"];
			for(var i = 0; i < 3; i++){
				var th = $("<th></th>");
				th.appendTo(tr);
				th.text(title[i]);
			}
			
			
			if(data.length === 0){
				tr = $("<tr></tr>");
				tr.appendTo(tbody);
				for(var i = 0; i < 3; i++){
					var td = $("<td></td>");
					td.appendTo(tr);
					if(i === 1){
						td.attr("id","empty_tslist");}
						td.text("/");
				}
			}
			
			for(var i = 0; i < data.length; i++){
				tr = $("<tr></tr>");
				tr.appendTo(tbody);
				
				var td = $("<td></td>");
				td.appendTo(tr);
				td.text(data[i]);
				td = $("<td></td>");
				td.appendTo(tr);
				var div = $("<div></div>");
				div.appendTo(td);
				div.attr("id",data[i]);
				Import.loadProcess(data[i]);
				var td = $("<td></td>");
				td.appendTo(tr);
				var html = "<input type = 'button' value = '取消' onclick = 'Import.stop(\"" + data[i] + "\")'/>";
				td.html(html);
			}
			$("#dtfragment-1-table").DataTable({
				paging: false
			});
			
			/*****refresh list*****/
//			console.log(Common.refresh_id);
			if(Common.refresh_id != -1){
				window.clearInterval(Common.refresh_id);
			}
			Common.refresh_id = window.setInterval("Import.refreshList()",Common.refreshInterval());
//			console.log(Common.refresh_id);
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Import.loadSuccessList = function(){
$("#dtfragment-2").empty();
	$.getJSON(URL.importSuceessList() + "?jsoncallback=?")
		.done(function(data){
			console.log(data);
			if(data === null){
				data = [];
			}
			
			var table = $("<table></table>");
			table.attr("id","dtfragment-2-table");
			table.attr("class","display");
			table.appendTo("#dtfragment-2");
			var thead = $("<thead></thead>");
			thead.appendTo(table);
			var tbody = $("<tbody></tbody>");
			tbody.appendTo(table);
			
			var tr = $("<tr></tr>");
			tr.appendTo(thead);
			var title = ["时间","ID","操作"];
			for(var i = 0; i < 3; i++){
				var th = $("<th></th>");
				th.appendTo(tr);
				th.text(title[i]);
			}
			
			
			if(data.length === 0){
				tr = $("<tr></tr>");
				tr.appendTo(tbody);
				for(var i = 0; i < 3; i++){
					var td = $("<td></td>");
					td.appendTo(tr);
					if(i === 1){
						td.attr("id","empty_tslist");}
						td.text("/");
				}
			}
			
			for(var i = 0; i < data.length; i++){
				tr = $("<tr></tr>");
				tr.appendTo(tbody);
				
				var td = $("<td></td>");
				td.appendTo(tr);
				td.text(data[i].builtTime);
				td = $("<td></td>");
				td.appendTo(tr);
				td.text(data[i].id);
				td = $("<td></td>");
				td.appendTo(tr);
				var html = "<input type = 'button' value = '删除' onclick = 'Import.remove(\"" + data[i].id + "\")'/>";
				td.html(html);
			}
			$("#dtfragment-2-table").DataTable({
			});
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Import.loadProcess = function(id){
	$.getJSON(URL.importProcess() + "?jsoncallback=?&id=" + id)
		.done(function(data){
			console.log(data.status);
			$("#" + id).percentageLoader({width: 100, height: 100, controllable : false, progress : data.status,value: ""});
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Import.refreshList = function(){
	var tr = $("#dtfragment-1").children("div").children("table").children("tbody").children("tr");
	for(var i = 0; i < tr.length; i++){
		Import.refreshStatus(i);
	}
};

/*****refresh task status*****/

Import.refreshStatus = function(index){
	/*****get task id*****/
	var tr = $("#dtfragment-1").children("div").children("table").children("tbody").children("tr").eq(index);
	var td = tr.children("td").eq(1);
	if(td.attr("id") === "empty_tslist"){
		return;
	}
	var status = td.children("div").children("div").children("div").eq(0).text();
	console.log(status);
	if(status === "100%"){
		Import.loadRunningList();
		Import.loadSuccessList();
		return;
	};
	Import.loadProcess(tr.children("td").eq(0).text());
};

/*****stop task*****/

Import.stop = function(ts_id){
//	console.log(ts_id);
	$.getJSON(URL.stopList() + "?jsoncallback=?" + "&id=" + ts_id)
		.done(function(data){
//			console.log(data);
			/*****alert info*****/
			alert("成功中止导入!");
			/*****reload task list*****/
			Import.loadRunningList();
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

/*****remove task*****/

Import.remove = function(ts_id){
//	console.log(ts_id);
	$.getJSON(URL.deleteList() + "?jsoncallback=?" + "&id=" + ts_id)
		.done(function(data){
//			console.log(data);
			/*****alert info*****/
//			alert("成功删除数据集!");
			/*****reload task list*****/
			Import.loadSuccessList();
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};