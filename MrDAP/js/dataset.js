Dataset = {};

Dataset.loadList = function(cntr_name,type){
	$.getJSON(URL.getDatasetList() + "?jsoncallback=?")
		.done(function(data){
//			console.log(data);
			var ul_class = $("#" + cntr_name + " ul").attr("class");
			Dataset.loadLevel(data,ul_class,type);
		}).fail(function(){
			alert("Oops, we got an error...");
		});
	$(document).ajaxComplete(function(event,xhr,settings){
//		console.log(settings.url);
		if(settings.url.indexOf(URL.getDatasetList()) >= 0){
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
		}
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
					a = $("<span></span>");
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
			var dstable = $("<div></div>");
			dstable.appendTo(cntr);
			dstable.attr("id","dstable");
			
			google.load("visualization","1",{packages:["table"],"callback":drawTable});
			function drawTable(){
				var data = new google.visualization.DataTable();
				data.addColumn('string',"创建日期");
				data.addColumn('string',"ID");
				data.addColumn('string',"名称");
				data.addColumn('string',"序列");
				data.addColumn('string',"类别");
				
				var array = $.parseJSON("[]");
				array[0] = $.parseJSON("[]");
				array[0][0] = dsdata.date;
				array[0][1] = dsdata.id;
				array[0][2] = dsdata.name;
				array[0][3] = dsdata.serial;
				array[0][4] = dsdata.type;
//				console.log(array);
				
				data.addRows(array);
				var table = new google.visualization.Table(document.getElementById("dstable"));
				table.draw(data);
				$("#dstable .google-visualization-table-th:contains(序列)").css({
					"width": "50px"
				});
				$("#dstable .google-visualization-table-th:contains(类别)").css({
					"width": "50px"
				});
			}
		}).fail(function(){
			alert("Oops, we got an error...");
		});
};