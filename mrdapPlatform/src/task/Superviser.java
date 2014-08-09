package task;

import java.io.BufferedReader;
import java.io.IOException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import task.TaskQuery.TaskInfo;

public class Superviser extends Thread
{
	private String taskId;
	private BufferedReader br;

	public Superviser(String taskId, BufferedReader br)
	{
		this.taskId = taskId;
		this.br = br;
	}

	public void run()
	{
		String temp = null;
		try
		{
			while (true)
			{

				temp = br.readLine();

				if (temp == null)
				{
					break;
				}

				// if there are some exceptions ,then stop the task.
				if (temp.contains("Caused by:"))
				{
					break;

				}

			}
		} catch (IOException e)
		{
		} finally
		{
			try
			{
				br.close();
			} catch (IOException e)
			{
			}

			try
			{
				//wait for 10seconds
				Thread.sleep(10000);

				//if the task has stopped,then return
				TaskInfo ti = TaskQuery.getTaskInfo(taskId);

				if (task.State.FINISHED.name().equals(ti.getState()))
				{
					return;
				}

				TaskExecutor.kill(taskId);

			} catch (InterruptedException | JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// killed job after 5seconds
		}

	}
}
