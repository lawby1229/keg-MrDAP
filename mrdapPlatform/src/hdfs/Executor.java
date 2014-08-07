package hdfs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class Executor
{
	public static String exec(String command)
	{
		StringBuilder sb=new StringBuilder();
		BufferedReader br=null;
		try
		{
			Process process=Runtime.getRuntime().exec(command);
		
			 br=new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String temp=null;
			while(true)
			{
				temp=br.readLine();
				if(temp==null)
				{
					break;
				}
				
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
	
	public static void main(String[] args)
	{
		
		String str=exec("hadoop version");
		//String str2=exec("/usr/local/hadoop-2.2.0/bin/hadoop");

		System.out.println(System.getenv().get("HADOOP_HOME"));
		
		
	
		
		
		System.out.println(str);
	}
}
