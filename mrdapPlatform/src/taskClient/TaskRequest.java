package taskClient;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import env.Default;

public class TaskRequest {
	private static CloseableHttpClient httpClient = null;

	static {

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		httpClient = HttpClients.custom().setConnectionManager(cm).build();
	}
	
	private TaskRequest()
	{}

	
	public static String getContent(String url,List<String[]> args) {
		HttpPost post = new HttpPost(url);
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		for(String[] pairs:args)
		{
			 nvps.add(new BasicNameValuePair(pairs[0], pairs[1]));

		}
		


		String temp = null;
		String content = "";
		BufferedReader br = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			HttpResponse response = httpClient.execute(post);
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
				content += temp;

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
