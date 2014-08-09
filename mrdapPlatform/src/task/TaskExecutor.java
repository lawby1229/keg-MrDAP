package task;

import hdfs.Default;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class TaskExecutor 
{
	private static final String yarn=Default.HADOOP_HOME+"/bin/yarn";
	private static final String spark_submit=Default.SPARK_HOME+"/bin/spark-submit";
	
	
	
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
		
		
		String appId=submit("pagerank.PageRank",6,4,8,8,"./spark-kmeans-10.jar","","");
		
		System.out.println("AppId:"+appId);
		
		Thread.sleep(30000);
		kill(appId);
		
	}

	/*String argss=" --class pagerank.PageRank "+
	"--master yarn-cluster "+
	"--num-executors 6 "+
	"--driver-memory 4g "+
	"--executor-memory 8g "+
	"--executor-cores 8 "+
	"./spark-kmeans-10.jar";*/
	
	public static String submit(String mainClass,int num_executors,int driver_memory,int executor_memory,int executor_cores,String jar_path,String input,String output) throws IOException
	{
		String aa="--class "+mainClass+
				" --master yarn-cluster"+
				" --num-executors "+num_executors+
				" --driver-memory "+driver_memory+"g"+
				" --executor-memory "+executor_memory+"g"+
				" --executor-cores "+executor_cores+
				" "+jar_path+
				" "+input+" "+output;
		return submit(aa);
	}
	
	public static String submit(String args) throws IOException
	{
		String appId=null;
		
		String patten2="application_[0-9_]+";

		String patten1="Submitted application "+patten2+" to ResourceManager";

		
		Pattern p1=Pattern.compile(patten1);
		Pattern p2=Pattern.compile(patten2);


		Process process=Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",spark_submit+" "+args+" 2>&1"});
		BufferedReader br=new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		//BufferedReader br=new BufferedReader(new FileReader("info.txt"));

		
		//Submitted application application_1407400725561_0001 to ResourceManager
		String temp=null;
		while(true)
		{
			
			//進行第一次匹配
			temp=br.readLine();
			if(temp==null)
			{
				break;
			}
			//System.out.println("info:"+temp);

			
			Matcher m1=p1.matcher(temp);
			
			if(m1.find())
			{
				String sub=temp.substring(m1.start(),m1.end());
				Matcher m2=p2.matcher(sub);
				
				if(m2.find())
				{
					appId=sub.substring(m2.start(),m2.end());
					
					new Superviser(appId,br).start();
					
					return appId;
					
				}	
			}
			
		}
		br.close();
		
		return null;
	
	}
	
	public static String kill(String taskId)
	{
		
		return exec(new String[]{"/bin/sh","-c",yarn+" application -kill "+taskId+" 2>&1"});
	}
	
	private static String exec(String[] command)
	{
		StringBuilder sb=new StringBuilder();
		BufferedReader br=null;
		try
		{
			Process process=Runtime.getRuntime().exec(command);
		
			 br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String temp=null;
			int i=0;
			while(i<8)
			{
				temp=br.readLine();
				if(temp==null)
				{
					break;
				}
				i++;
				sb.append(temp+"\n");

			}
			
			
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(br!=null){
			try
			{
				
				br.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
		
		return sb.toString();
	}
	
	

}
