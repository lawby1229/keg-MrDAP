package hdfs;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;

public class Test
{

	public static void main(String[] args) throws IOException
	{
		
		MFile mf=new MFile("/mobile/MRO/101/20140807");
	
		FSDataInputStream dis=mf.open();
		
		BufferedReader br=new BufferedReader(new InputStreamReader(dis));
		
		String temp=null;
		while(true)
		{
			temp=br.readLine();
			if(temp==null)
			{
				break;
			}
			
			System.out.println(temp);
			
		}
		
		br.close();
		
	
		
	}
	
	public static void println(MFile mf) throws IOException
	{
		
		if(!mf.isDirectory())
		{
			System.out.println(mf);
			return;
		}
		
		MFile[] mfs=mf.list();
		
		for(MFile mff:mfs)
		{
			println(mff);
		}
		
	}

}
