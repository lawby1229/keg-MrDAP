$(document).ready(function(){
	console.log("version 1.1");
	Common.setStyle();
	URL.head = Common.getURL();
//	console.log("async: false");
	Common.loadDataset();
	Common.loadTabs();
	Common.loadTask();
});

Common = {};

/*****refresh*****/

Common.refresh_id = -1;

Common.refreshInterval = function(){
	return 5000;
};

/*****distinguish array & object*****/

Common.isArray = function(what){
	return Object.prototype.toString.call(what) === '[object Array]';
};

Common.isObject = function(what){
	return Object.prototype.toString.call(what) === '[object Object]';
};

/*****open & close window*****/

Common.openWindow = function(){
	$("#window").css("display","block");
};

Common.closeWindow = function(){
	$("#window").css("display","none");
};

/*****document loading*****/

Common.setStyle = function(){
	var height = $(document).height() - 165;
	var width = $(document).width() - 900;
	if(height < 400){
		height = 400;
	}
	if(width < 400){
		width = 400;
	}
	
	$("#main").css({
		"height": height
	});
	$("#dataset").css({
		"height": height
	});
	$("#dstree").css({
		"height": height - 82
	});
	$("#detail").css({
		"height": height,
		"width": width
	});
	$("#dttabs").css({
		"height": height - 22
	});
	$("#dtfragment-1").css({
		"height": height - 88
	});
	$("#dtfragment-2").css({
		"height": height - 88
	});
	$("#task").css({
		"height": height
	});
	$("#tslist").css({
		"height": height - 82
	});
};

Common.getURL = function(){
	var url = "";
	$.ajax({url: "settings.json",async: false,dataType: "json"})
		.done(function(data){
//			console.log(data.url);
			url = data.url;
		}).fail(function(){
			alert("Oops, we got an error...");
		});
	return url;
};

Common.loadDataset = function(){
	Dataset.loadList("dstree","list");
};

Common.loadTabs = function(){
	$("#dttabs").tabs();
};

Common.loadTask = function(){
	Task.loadList();
};