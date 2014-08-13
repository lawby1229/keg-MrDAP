package hdfs;

import java.io.File;
import java.io.IOException;

import org.apache.hadoop.fs.Path;

public class Test
{

	public static void main(String[] args) throws IOException
	{
		
		MFile mf=new MFile("/mobile");
	
		println(mf);
		
	
		
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
