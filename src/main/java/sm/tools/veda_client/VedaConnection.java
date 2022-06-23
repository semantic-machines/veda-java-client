package sm.tools.veda_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class VedaConnection
{
    public static final int STORAGE           = 1;
    public static final int ACL               = 2;
    public static final int FULL_TEXT_INDEXER = 4;
    public static final int FANOUT_EMAIL      = 8;
    public static final int SCRIPTS           = 16;
    public static final int FANOUT_SQL        = 32;
    public static final int USER_MODULES_TOOL = 64;
	public VedaConnection(String _url, String user, String pass) throws IOException
	{
		jp = new JSONParser();
		destination = _url;
		vedaTicket = getVedaTicket(user, pass);

		if ((vedaTicket == null) || (vedaTicket.length() < 1))
		{
			System.out.println("Destination.Veda.Login filed");
			_isOk = false;
		} else {
			_isOk = true;
		}
		
	}

	public boolean isOk()
	{
		return _isOk;
	}

	String destination;
	String vedaTicket;
	boolean _isOk = false;
	JSONParser jp;
	public long count_put = 0;
	
	public int setIndividual(Individual indv, boolean isPrepareEvent, int assignedSubsystems) throws InterruptedException
	{
		String jsn = indv.toJsonStr();
		String ijsn = "{\"@\":\"" + indv.getUri() + "\"," + jsn + "}";
		int res = setJson(ijsn, isPrepareEvent, assignedSubsystems);
		return res;
	}

	private int setJson(String jsn, boolean isPrepareEvent, int assignedSubsystems) throws InterruptedException
	{
		if (jsn.indexOf("\\") >= 0)
		{
			int len = jsn.length();
			StringBuffer new_str_buff = new StringBuffer();

			for (int idx = 0; idx < jsn.length(); idx++)
			{
				char ch1 = jsn.charAt(idx);
				new_str_buff.append(ch1);

				if (ch1 == '\\')
				{
					char ch2 = 0;

					if (idx < len - 1)
						ch2 = jsn.charAt(idx + 1);

					if (ch2 != 'n' && ch2 != 'b' && ch2 != '"' && ch2 != '\\')
					{
						new_str_buff.append('\\');
					}

					if (ch2 == '\\')
					{
						new_str_buff.append(ch2);
						idx++;
					}

				}
			}

			jsn = new_str_buff.toString();
		}

		int res = 429;
		int count_wait = 0;
		while (res == 429)
		{
			//String query = "{\"ticket\":\"" + vedaTicket + "\", \"individual\":" + jsn + ", \"prepare_events\":" + isPrepareEvent
				//	+ ", \"event_id\":\"\", " + "\"transaction_id\":\"\" }";
//			String query=String.format("{\"ticket\":\"%s\", \"individual\":%s, \"prepare_events\": %b, \"event_id\":\"\", \"transaction_id\":\"\","
//					+ "\"assigned_subsystems\":%d }", vedaTicket, jsn, isPrepareEvent, assignedSubsystems);
			String query=String.format("{\"individual\":%s, \"prepare_events\": %b, \"event_id\":\"\", \"transaction_id\":\"\","
					+ "\"assigned_subsystems\":%d }", jsn, isPrepareEvent, assignedSubsystems);
//			System.out.println(query);
			res = util.excutePut(destination + "/set_in_individual?ticket=" + vedaTicket, query);

			if (res != 200)
			{
				if (res == 429)
				{
					Thread.sleep(10);
					count_wait++;
				}
			} else
			{
				count_put++;
			}

			if (count_wait == 1)
				System.out.print(".");

		}

		return res;
	}

	public int putIndividual(Individual indv, boolean isPrepareEvent, int assignedSubsystems) throws InterruptedException
	{
		String jsn = indv.toJsonStr();
		String ijsn = "{\"@\":\"" + indv.getUri() + "\"," + jsn + "}";
		int res = putJson(ijsn, isPrepareEvent, assignedSubsystems);
		return res;
	}

	public int putJson(String jsn, boolean isPrepareEvent, int assignedSubsystems) throws InterruptedException
	{
		if (jsn.indexOf("\\") >= 0)
		{
			int len = jsn.length();
			StringBuffer new_str_buff = new StringBuffer();

			for (int idx = 0; idx < jsn.length(); idx++)
			{
				char ch1 = jsn.charAt(idx);
				new_str_buff.append(ch1);

				if (ch1 == '\\')
				{
					char ch2 = 0;

					if (idx < len - 1)
						ch2 = jsn.charAt(idx + 1);

					if (ch2 != 'n' && ch2 != 'b' && ch2 != '"' && ch2 != '\\')
					{
						new_str_buff.append('\\');
					}

					if (ch2 == '\\')
					{
						new_str_buff.append(ch2);
						idx++;
					}

				}
			}

			jsn = new_str_buff.toString();
		}

		int res = 429;
		int count_wait = 0;
		while (res == 429)
		{
			//String query = "{\"ticket\":\"" + vedaTicket + "\", \"individual\":" + jsn + ", \"prepare_events\":" + isPrepareEvent
				//	+ ", \"event_id\":\"\", " + "\"transaction_id\":\"\" }";
//			String query=String.format("{\"ticket\":\"%s\", \"individual\":%s, \"prepare_events\": %b, \"event_id\":\"\", \"transaction_id\":\"\","
//					+ "\"assigned_subsystems\":%d }", vedaTicket, jsn, isPrepareEvent, assignedSubsystems);
			String query=String.format("{\"individual\":%s, \"prepare_events\": %b, \"event_id\":\"\", \"transaction_id\":\"\","
					+ "\"assigned_subsystems\":%d }", jsn, isPrepareEvent, assignedSubsystems);
//			System.out.println(query);
			res = util.excutePut(destination + "/put_individual?ticket=" + vedaTicket, query);

			if (res != 200)
			{
				if (res == 429)
				{
					Thread.sleep(10);
					count_wait++;
				}
			} else
			{
				count_put++;
			}

			if (count_wait == 1)
				System.out.print(".");

		}

		return res;
	}

	public String uploadFile(byte[] data, String path, String fileName) throws ClientProtocolException, IOException {
		String uri = DigestUtils.shaHex(data);
		CloseableHttpClient httpClient = null;
		try {
			httpClient = HttpClients.createDefault();
			BasicCookieStore cookieStore = new BasicCookieStore(); 
			BasicClientCookie cookie = new BasicClientCookie("ticket", vedaTicket);
			cookieStore.addCookie(cookie); 
			HttpClientContext context = HttpClientContext.create();
			context.setCookieStore(cookieStore);
			
			HttpPost uploadFile = new HttpPost(destination+"/files");
			uploadFile.addHeader("accept-encoding", "identity");
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addBinaryBody("file", data, ContentType.DEFAULT_BINARY, fileName);
			builder.addTextBody("uri", uri, ContentType.TEXT_PLAIN);
			builder.addTextBody("path", path, ContentType.TEXT_PLAIN);
			
			HttpEntity multipart = builder.build();
			uploadFile.setEntity(multipart);
			System.out.println("uploadFile: "+uploadFile.getURI());
			CloseableHttpResponse response = httpClient.execute(uploadFile, context);
			System.out.println("response: "+response.toString());
			HttpEntity responseEntity = response.getEntity();
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
				String line = null;
				while((line = reader.readLine()) != null) {
				    System.out.println("UPLOAD FILE: " + line);
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}		
		return uri;
	}
	
	public String getVedaTicket(String user, String pass) throws IOException
	{	
		String query = destination + "/authenticate?login=" + user + "&password=" + pass;
		System.out.println("ticketQuery="+query);
		String res = util.excuteGet(query);
		
		System.out.println(res);
		try
		{
			JSONObject oo = (JSONObject) jp.parse(res);

			return (String) oo.get("id");
		} catch (Exception ex)
		{
			//ex.printStackTrace();
		}
		return null;
	}
	
	public String[] query(String query, String param) throws UnsupportedEncodingException, Exception {
		String[] res_arr;
		String res = util.excuteGet(destination + "/query?ticket=" + vedaTicket + "&"+param+"&query=" + URLEncoder.encode(query,"UTF-8"));
		// System.out.println(res);
		try
		{
			Object oo_res = jp.parse(res);

			if (oo_res instanceof JSONObject)
			{
				Object oo = ((JSONObject) oo_res).get("result");

				if (oo instanceof JSONArray)
				{
					JSONArray arr = (JSONArray) oo;
					res_arr = new String[arr.size()];
					arr.toArray(res_arr);
					return res_arr;
				}
			}
			return null;
		} catch (Exception ex)
		{
			//ex.printStackTrace();
		}
		return null;
	}
	public String[] query(String query) throws IOException
	{
		String[] res_arr;
		String res = util.excuteGet(destination + "/query?ticket=" + vedaTicket + "&query=" + URLEncoder.encode(query,"UTF-8"));
		// System.out.println(res);
		try
		{
			Object oo_res = jp.parse(res);

			if (oo_res instanceof JSONObject)
			{
				Object oo = ((JSONObject) oo_res).get("result");

				if (oo instanceof JSONArray)
				{
					JSONArray arr = (JSONArray) oo;
					res_arr = new String[arr.size()];
					arr.toArray(res_arr);
					return res_arr;
				}
			}
			return null;
		} catch (Exception ex)
		{
			//ex.printStackTrace();
		}
		return null;
	}

	public Individual getIndividual(String uri) throws IOException
	{
		String res = util.excuteGet(destination + "/get_individual?ticket=" + vedaTicket + "&uri=" + uri);

		if (res == null)
			return null;

		// System.out.println(res);
		try
		{
			JSONObject oo = (JSONObject) jp.parse(res);

			return new Individual(oo);
		} catch (Exception ex)
		{
			//ex.printStackTrace();
		}
		return null;
	}

	public static void copy(Individual src, Individual dest, String field_name, int new_type)
	{
		Resources rsz = src.getResources(field_name);

		if (rsz != null)
		{
			for (Resource rs : rsz.resources)
			{
				if (new_type > 0)
					dest.addProperty(field_name, rs.getData(), new_type);
				else
					dest.addProperty(field_name, rs.getData(), rs.getType());
			}
		}

	}
}
