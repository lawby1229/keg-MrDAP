package taskClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONException;



import env.Default;

public class TaskClient 
{
	
	
	public static void main(String[] args) throws IOException, InterruptedException, JSONException
	{
		/*String argss=" --class pagerank.PageRank "+
				"--master yarn-cluster "+
				"--num-executors 6 "+
				"--driver-memory 4g "+
				"--executor-memory 8g "+
				"--executor-cores 8 "+
				"./spark-kmeans-10.jar";
	
		System.out.println(spark_submit+argss+"\n");
		String appId=submit(argss);
		System.out.println("appId:"+appId);*/
		
		String appId=submit("pagerank.PageRank",6,4,8,8,"/home/hadoop/slib/spark-kmeans-10.jar","","","");
		
		System.out.println("AppId:"+appId);
		
		Thread.sleep(30000);
		System.out.println("killed:"+kill(appId));
		
	}

	/*String argss=" --class pagerank.PageRank "+
	"--master yarn-cluster "+
	"--num-executors 6 "+
	"--driver-memory 4g "+
	"--executor-memory 8g "+
	"--executor-cores 8 "+
	"./spark-kmeans-10.jar";*/
	
	//mainClass=pagerank.PageRank&num_executors=6&driver_memory=4&executor_memory=8&executor_cores=8&jar_path=./spark-kmeans-10.jar
	
	
	public static String submit(String mainClass,int num_executors,int driver_memory,int executor_memory,int executor_cores,String jar_path,String input,String output,String args) throws IOException
	{
		List<String[]> list=new ArrayList<String[]>();
		
		list.add(new String[]{"mainClass",mainClass});
		list.add(new String[]{"num_executors",""+num_executors});
		list.add(new String[]{"driver_memory",""+driver_memory});
		list.add(new String[]{"executor_memory",""+executor_memory});
		list.add(new String[]{"executor_cores",""+executor_cores});
		list.add(new String[]{"jar_path",jar_path});
		list.add(new String[]{"input",input});
		list.add(new String[]{"output",output});
		list.add(new String[]{"args",args});
		
		for(String[] aa:list)
		{
			System.out.println(aa[0]+","+aa[1]);
		}
		
		System.out.println("submit");

		
		String app_submit_url=Default.getValue("APPLICATION_SUBMIT_URL");
		
		
		System.out.println(app_submit_url+"submit");
		
		return TaskRequest.getContent(app_submit_url+"submit",list);
		
		
		/*String aa="--class "+mainClass+
				" --master yarn-cluster"+
				" --num-executors "+num_executors+
				" --driver-memory "+driver_memory+"g"+
				" --executor-memory "+executor_memory+"g"+
				" --executor-cores "+executor_cores+
				" "+jar_path+
				" "+input+" "+output+
				" "+args;*/
		
		
	}
	
	
	
	public static String kill(String taskId)
	{
		String app_submit_url=Default.getValue("APPLICATION_SUBMIT_URL");

		List<String[]> list=new ArrayList<String[]>();
		
		list.add(new String[]{"appId",taskId});
		
		return TaskRequest.getContent(app_submit_url+"kill",list);

	}
	
	
	
	

}
