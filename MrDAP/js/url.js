URL = {};

URL.head = "";

/*****dataset url*****/

URL.getDatasetList = function(){
	return URL.head + "/rest/dsg/getalldss";
};

URL.getDatasetInfo = function(){
	return URL.head + "/rest/dsg/getds";
};

/*****task url*****/

URL.getTaskList = function(){
	return URL.head + "/rest/tsg/gettss";
};

URL.getTaskInfo = function(){
	return URL.head + "/rest/tsg/getts";
};

URL.getTaskType = function(){
	return URL.head + "/rest/tsg/tstypes";
};

URL.getTaskStatus = function(){
	return URL.head + "/rest/tsg/tsstatus";
};

URL.runTask = function(){
	return URL.head + "/rest/tsg/runtask";
};

URL.stopTask = function(){
	return URL.head + "/rest/tsg/killts";
};

URL.removeTask = function(){
	return URL.head + "/rest/tsg/rmts";
};

/*****download url*****/

URL.download = function(){
	return URL.head + "/rest/tsg/dl";
};