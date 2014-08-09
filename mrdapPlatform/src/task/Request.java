package task;

import hdfs.Default;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class Request {
	private static CloseableHttpClient httpClient = null;

	static {

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		httpClient = HttpClients.custom().setConnectionManager(cm).build();
	}
	
	private Request()
	{}

	public static void main(String[] args) 
	{
		//System.out.println(getApplicationInfo("application_1407400725561_0001"));
		System.out.println(getContent("/ws/v1/cluster/apps"));
	}

	public static String getApplicationsInfo()
	{
		return getContent("/ws/v1/cluster/apps");
	}
	public static String getApplicationInfo(String applicationId)
	{
		return getContent("/ws/v1/cluster/apps/"+applicationId);
	}
	
	private static String getContent(String path) {
		HttpGet get = new HttpGet(Default.APPLICATION_INFO_URL+path);
		
		//get.addHeader("User-agent", "Mozilla/5.0");
		// List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		// nvps.add(new BasicNameValuePair("titles",
		// "Java_(programming_language)"));
		// nvps.add(new BasicNameValuePair("prop", "revisions"));
		// nvps.add(new BasicNameValuePair("rvprop", "content"));
		// nvps.add(new BasicNameValuePair("format", "xml"));

		// action=query&prop=revisions&titles=Java_(programming_language)&rvprop=content&format=xml

		String temp = null;
		String content = "";
		BufferedReader br = null;
		try {
			// post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			HttpResponse response = httpClient.execute(get);
			// 先从响应头得到实体
			HttpEntity entity = response.getEntity();
			// 得到实体输入流

			br = new BufferedReader(new InputStreamReader(entity.getContent()));

			// content = br.readLine();
			while (true) {
				temp = br.readLine();
				if (temp == null) {
					break;
				}
			
				//System.out.println(temp);
				content += temp + "\n";

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();

				} catch (IOException e) {
				}
			}
		}
		return content;
	}

	

}
