URL = {};

URL.head = "";

/*****dataset url*****/

URL.getDatasetList = function(){
	return URL.head + "/rest/dsg/getallhdss";
};

URL.getDatasetInfo = function(){
	return URL.head + "/rest/dsg/getds";
};

URL.refreshDslist = function(){
	return URL.head + "/rest/dsg/refresh";
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

URL.refreshTstype = function(){
	return URL.head + "/rest/tsg/refresh";
};

URL.getTaskStatus = function(){
	return URL.head + "/rest/tsg/tsstatus";
};

URL.runTask = function(){
	return URL.head + "/rest/tsg/runhtask";
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