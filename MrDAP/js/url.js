URL = {};

URL.head = "";

URL.getDatasetList = function(){
	return URL.head + "/rest/dsg/getalldss";
};

URL.getDatasetInfo = function(){
	return URL.head + "/rest/dsg/getds";
};

URL.getTaskList = function(){
	return URL.head + "/rest/tsg/gettss";
};

URL.getTaskInfo = function(){
	return URL.head + "/rest/tsg/getts";
};

URL.getTaskType = function(){
	return URL.head + "/rest/tsg/tstypes";
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

URL.getTaskStatus = function(){
	return URL.head + "/rest/tsg/tsstatus";
};