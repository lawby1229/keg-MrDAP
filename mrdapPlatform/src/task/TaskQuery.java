package task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;



public class TaskQuery {

	private TaskQuery() {
	}

	public static TaskInfo getTaskInfo(String taskId) throws JSONException {

		String str = Request.getApplicationInfo(taskId);
		// System.out.println(json);

		if (str.contains("Task:NotFoundException")) {
			return null;
		}

		JSONObject json = new JSONObject(str);
		JSONObject info = (JSONObject) json.get("app");

		return new TaskInfo(info);
	}

	public static class TaskInfo {
		private JSONObject info = null;

		private TaskInfo(JSONObject jsonStr) throws JSONException {
			info = jsonStr;
		}

		public String getId() throws JSONException {
			return info.getString("id");
		}

		public String getName() throws JSONException {
			return info.getString("name");
		}

		// return RUNNING ..FINISHED
		public String getState() throws JSONException {
			return info.getString("state");
		}

		// KILLED,FAILED,SUCCESSFUL
		public String getFinalStatus() throws JSONException {
			return info.getString("finalStatus");
		}

		// SPARK
		public String getApplicationType() throws JSONException {
			return info.getString("applicationType");
		}

		public long getStartedTime() throws JSONException {
			return info.getLong("startedTime");
		}

		public String getFormatedStartedTime() throws JSONException {
			return toTime(info.getLong("startedTime"));
		}

		// RUNNING TIME
		public String getElapsedTime() throws JSONException {
			return info.getString("elapsedTime");
		}

		public long getFinishTime() throws JSONException {
			return info.getLong("finishedTime");
		}

		public String getFormatedFinishTime() throws JSONException {
			return toTime(info.getLong("finishedTime"));
		}

		public String getAmHostHttpAddress() throws JSONException {
			return info.getString("amHostHttpAddress");
		}

		//
		public String getDiagnostics() throws JSONException {
			return info.getString("diagnostics");
		}

		public String toString() {
			return info.toString();
		}

	}

	public static TaskInfo[] getTasksInfo(FinalStatus finalStatus)
			throws JSONException {
		String str = Request.getApplicationsInfo();

		JSONObject info = new JSONObject(str);

		JSONObject apps = (JSONObject) info.get("apps");

		JSONArray jsa = (JSONArray) apps.get("app");
		ArrayList<TaskInfo> list = new ArrayList<TaskInfo>();

		for (int i = 0; i < jsa.length(); i++) {

			JSONObject temp = jsa.getJSONObject(i);

			if (finalStatus.name().equals(FinalStatus.ALL.name())
					|| finalStatus.name().equals(temp.getString("finalStatus"))) {
				list.add(new TaskInfo(temp));
			}
		}

		return list.toArray(new TaskInfo[0]);
	}

	// GET INFO BY STATE
	public static TaskInfo[] getTasksInfo(State state) throws JSONException {
		String str = Request.getApplicationsInfo();

		JSONObject info = new JSONObject(str);

		JSONObject apps = (JSONObject) info.get("apps");

		JSONArray jsa = (JSONArray) apps.get("app");
		ArrayList<TaskInfo> list = new ArrayList<TaskInfo>();

		for (int i = 0; i < jsa.length(); i++) {

			JSONObject temp = jsa.getJSONObject(i);

			if (state.name().equals(State.ALL.name())
					|| state.name().equals(temp.getString("state"))) {
				list.add(new TaskInfo(temp));
			}
		}

		return list.toArray(new TaskInfo[0]);
	}

	public static void main(String[] args) throws JSONException {
		TaskInfo[] tis = TaskQuery.getTasksInfo(FinalStatus.SUCCEEDED);
		TaskInfo ti3 = TaskQuery.getTaskInfo("application_1407400725561_0013");
		System.out.println(ti3);
//		for (TaskInfo ti : tis) {
//			System.out.println(ti.getId() + ";" + ti.getFormatedStartedTime()
//					+ "--" + ti.getFormatedFinishTime() + ";" + ti.getState()
//					+ ";" + ti.getFinalStatus());
//		}

		// System.out.println(toTime(1407501106430L));

	}

	private static String toTime(long secs) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		;
		Date d = new Date(secs);
		return sdf.format(d);

	}
}
