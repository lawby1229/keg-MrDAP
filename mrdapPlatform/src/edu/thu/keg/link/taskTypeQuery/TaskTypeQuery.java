package edu.thu.keg.link.taskTypeQuery;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import edu.thu.keg.link.common.Request;
import edu.thu.keg.link.env.Default;



public class TaskTypeQuery
{
	private static final String empty=new JSONObject().toString();
	
	private TaskTypeQuery(){}
	
	
	public static String update()
	{
		return Request.post(Default.getValue("APPLICATION_SUBMIT_URL")+"UpdateTaskTypeInfo",new ArrayList<String[]>());

	}
	
	public static TaskType[] getTaskTypeList() throws JSONException
	{
		
		String str=Request.post(Default.getValue("APPLICATION_SUBMIT_URL")+"TaskTypeList",new ArrayList<String[]>());
	
		//System.out.println(str);
		
		JSONArray jsons=new JSONObject(str).getJSONArray("taskTypes");
		
		if(jsons.length()==0)
		{
			return null;
		}
		
		TaskType[] tts=new TaskType[jsons.length()];
		
		for(int i=0;i<jsons.length();i++)
		{
			tts[i]=new TaskType(jsons.getJSONObject(i).toString());
		}
		
		
		return tts;
	}
	
	
	public static TaskType getTaskTypeById(String id) throws JSONException
	{
		List<String[]> list=new ArrayList<String[]>();
		
		list.add(new String[]{"id",id});
		
		String str=Request.post(Default.getValue("APPLICATION_SUBMIT_URL")+"taskTypeById",list);
		
		JSONObject json=new JSONObject(str);
		
		String temp=json.getString("taskType");
		
		if(temp.equals(empty))
		{
			return null;
			
		}
		
		return new TaskType(temp);
	}
	
	public static void main(String[] args) throws JSONException
	{
		//System.out.println(getTaskTypeById("201408181319"));
		//System.out.println(update());
		TaskType[] tts=getTaskTypeList();
		
		for(TaskType tt:tts)
		{
			System.out.println(tt);
		System.out.println(tt.getValue(TaskTypeField.DESCRIPTION));
		}
		
	}
	
	
	public static class TaskType
	{
		private JSONObject info=null;
		
		private TaskType(String jsonStr) throws JSONException
		{
			info=new JSONObject(jsonStr);
			
		}
		
		public String getValue(TaskTypeField key) throws JSONException
		{
			return this.info.getString(key.toString());
		}
		
		public String toString()
		{
			return info.toString();
		}

		
	}
}
