Dataset = {};

Dataset.loadList = function(cntr_name,type){
	$.getJSON(URL.getDatasetList() + "?jsoncallback=?")
		.done(function(data){
//			console.log(data);
			var ul_class = $("#" + cntr_name + " ul").attr("class");
			Dataset.loadLevel(data,ul_class,type);
			if(type === "list"){
				$("#" + cntr_name).jstree();
			}
			if(type === "checkbox"){
				var height = $(document).height() - 165;
				if(height < 400){
					height = 400;
				}
				$("#window").css({
					"left": $("#detail").width() + 345,
					"height": height - 150
				});
				$("#" + cntr_name).jstree({
					"checkbox": {
						"keep_selected_style" : false
					},
					"plugins": ["checkbox"]
				});
			}
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};

Dataset.loadLevel = function(data,ul_id,type){
	var ul = $("." + ul_id);
	var keys = Object.keys(data);
//	console.log(keys.length);
	for(var i = 0; i < keys.length; i++){
		var li = $("<li></li>");
		li.appendTo(ul);
		var span = $("<span></span>");
		span.appendTo(li);
		span.html(keys[i]);
		var ul_sub = $("<ul class = '" + ul_id + "-" + i + "'></ul>");
		ul_sub.appendTo(li);
		
		var value = data[keys[i]];
//		console.log(value);
		if(Common.isArray(value)){
			for(var j = 0; j < value.length; j++){
				li = $("<li></li>");
				var a;
				if(type === "list"){
					a = $("<a href = 'javascript:void(0)' onclick = 'Dataset.showTable(\"" + value[j].id + "\")'>" + value[j].name + "</a>");
				}else{
					a = $("<span id = '" + value[j].id + "'></span>");
					a.html(value[j].name);
				}
				a.appendTo(li);
				li.appendTo(ul_sub);
			}
		}else{
			Dataset.loadLevel(value,ul_id + "-" + i,type);
		}
	}
};

Dataset.showTable = function(ds_id){
//	console.log(ds_id);
	$.getJSON(URL.getDatasetInfo() + "?jsoncallback=?" + "&id=" + ds_id)
		.done(function(dsdata){
//			console.log(dsdata);
			var cntr = $("#dtfragment-1");
			cntr.empty();
			$("#dttabs").tabs("option","active",0);
			
			var title = $("<span></span>");
			title.appendTo(cntr);
			title.text("详细信息");
			var table_cntr = $("<div></div>");
			table_cntr.appendTo(cntr);
			table_cntr.attr("id","dstable-cntr");
			
			var table = $("<table></table>");
			table.attr("id","dstable");
			table.attr("class","display");
			table.appendTo(table_cntr);
			var thead = $("<thead></thead>");
			thead.appendTo(table);
			var tbody = $("<tbody></tbody>");
			tbody.appendTo(table);
			
			var tr = $("<tr></tr>");
			tr.appendTo(thead);
			var title = ["创建日期","ID","名称","序列","类别"];
			for(var i = 0; i < 5; i++){
				var th = $("<th></th>");
				th.appendTo(tr);
				th.text(title[i]);
			}
			
			tr = $("<tr></tr>");
			tr.appendTo(tbody);
			var td = $("<td></td>");
			td.appendTo(tr);
			td.text(dsdata.date);
			
			td = $("<td></td>");
			td.appendTo(tr);
			td.text(dsdata.id);
			
			td = $("<td></td>");
			td.appendTo(tr);
			td.text(dsdata.name);
			
			td = $("<td></td>");
			td.appendTo(tr);
			td.text(dsdata.serial);
			
			td = $("<td></td>");
			td.appendTo(tr);
			td.text(dsdata.type);
			
			$("#dstable").DataTable({
				searching: false,
				ordering: false,
				paging: false,
				info: false
			});
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};