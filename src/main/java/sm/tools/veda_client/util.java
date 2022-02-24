package sm.tools.veda_client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.LookupTranslator;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class util
{
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");

	public static String escape(String string)
	{

		if (string != null)
		{
			StringBuilder sb = new StringBuilder();

			char c = ' ';

			for (int i = 0; i < string.length(); i++)
			{

				c = string.charAt(i);

				if (c == '\n')
				{
					sb.append("\\n");
				} else if (c == '\r')
				{
					sb.append("\\r");
				} else if (c == '\\')
				{
					sb.append("\\\\");
				} else if (c == '"')
				{
					sb.append("\\\"");
				} else if (c == '\t')
				{
					sb.append("\\t");
				} else
				{
					sb.append(c);
				}
			}
			return sb.toString();
		} else
		{
			return "";
		}
	}

	public static String get_hashed_uri(String big_uri) throws NoSuchAlgorithmException
	{
		MessageDigest md = null;
		md = MessageDigest.getInstance("SHA-1");
		String hh = util.byteArrayToHexString(md.digest(big_uri.getBytes()));

		if (hh.charAt(0) >= '0' && hh.charAt(0) <= '9')
		{
			hh = "d" + hh;
		}

		return "d:" + hh;

	}

	final static String _None = "NONE";
	final static String _Ru = "RU";
	final static String _En = "EN";

	public static void serializeResources(StringBuffer sb, String name, Resources rcs)
	{
		if (rcs == null || rcs.resources == null || rcs.resources.size() == 0)
			return;

		int ll = sb.length() - 1;
		if (ll > 0)
		{
			while (sb.charAt(ll) == ' ')
				ll--;

			char ch = sb.charAt(ll);

			if (ch == '"' || ch == '}' || ch == ']')
				sb.append(", ");
		}

		sb.append('"');
		sb.append(name);
		sb.append("\":[");

		int cc = 0;
		for (Resource rc : rcs.resources)
		{
			if ((rc.data == null || ((String) rc.data).equals(""))
					&& (rc.type == Type._Decimal || rc.type == Type._Integer || rc.type == Type._Datetime))
			{
				continue;
			}

			if (cc > 0)
				sb.append(',');

			sb.append("{\"data\":");

			if (rc.type == Type._String || rc.type == Type._Uri)
			{
				sb.append('"');
				sb.append(util.forJSON((String) rc.data));
				// sb.append((String) rc.data);
				sb.append('"');
			} else if (rc.type == Type._Bool || rc.type == Type._Integer)
			{
				// sb.append('"');
				if (rc.type == Type._Integer)
				{
					String s_tmp = rc.data.toLowerCase();
					if (s_tmp.charAt(0) == '0')
					{
						int ii = 0;

						while (ii < s_tmp.length() - 1 && s_tmp.charAt(ii) == '0')
							ii++;
						s_tmp = s_tmp.substring(ii, s_tmp.length());
					}

					try
					{
						Integer i_tmp = Integer.parseInt(s_tmp);
						//System.out.println("@ i_tmp=[" + i_tmp + "], s_tmp=[" + s_tmp + "]");
						sb.append(i_tmp.toString());
					} catch (Exception ex)
					{
						//ex.printStackTrace();
					}
				} else
					sb.append((String) rc.data.toLowerCase());
				// sb.append('"');
			} else if (rc.type == Type._Decimal)
			{
				sb.append('"');

				try
				{
					Double dd = Double.parseDouble((String) rc.data);

					DecimalFormat format = new DecimalFormat("###################.##########");
					DecimalFormatSymbols dfs = format.getDecimalFormatSymbols();
					dfs.setDecimalSeparator('.');
					format.setDecimalFormatSymbols(dfs);
					sb.append(format.format(dd));
				} catch (Exception ex)
				{
					sb.append((String) rc.data);
				}

				sb.append('"');
			} else if (rc.type == Type._Datetime)
			{
				sb.append('"');
				sb.append((String) rc.data);
				sb.append('"');
			}

			sb.append(", \"type\":");
			sb.append(rc.type);

			if (rc.lang.equals(_None) == false)
			{
				sb.append(", \"lang\":\"");
				sb.append(rc.lang);
				sb.append('"');
			}

			sb.append("}");
			cc++;
		}
		sb.append("]");
	}

	/**
	 * Transforms String to Date.
	 * 
	 * @param date
	 * @param time
	 * @return XMLGregorianCalendar
	 */
	public Date string2date(String date)
	{
		date = date.replace('T', ' ');
		date = date.substring(0, date.indexOf('+'));
		GregorianCalendar gcal = new GregorianCalendar();
		try
		{
			if (date.length() < 22)
				gcal.setTime(sdf2.parse(date));
			else
				gcal.setTime(sdf1.parse(date));
			return gcal.getTime();
		} catch (Exception ex)
		{
			ex.hashCode();
		}
		return null;
	}

	public static String date2string(Date date)
	{
		StringBuilder sb;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if ( (date.getTime() % 100000) == 0) {
			sb = new StringBuilder(dateFormat.format(date));
		} else {
			TimeZone gmtTime = TimeZone.getTimeZone("GMT");
			dateFormat.setTimeZone(gmtTime);
			sb = new StringBuilder(dateFormat.format(date));
		}
		sb.setCharAt(10, 'T');
		sb.append("Z");
		return sb.toString();
	}

	public String date2_short_string(Date date)
	{
		StringBuilder sb = new StringBuilder(sdf3.format(date));
		return sb.toString();
	}

	public static String excutePost(String targetURL, String urlParameters)
	{
		URL url;
		HttpURLConnection connection = null;
		try
		{
			// Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setRequestProperty("charset", "utf-8");

			connection.setUseCaches(false);
			// connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = connection.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null)
			{
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();

		} catch (Exception e)
		{

			//e.printStackTrace();
			return null;

		} finally
		{

			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}

	public static String excuteGet(String targetURL) throws Exception
	{
		StringBuffer responseBuffer = null;
		HttpGet httpGet = new HttpGet(targetURL);
		CloseableHttpResponse response = null;
		try (CloseableHttpClient httpclient = HttpClients.createDefault()){
			try {
				response = httpclient.execute(httpGet);
			    HttpEntity responseEnity = response.getEntity();
			    
			    int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(responseEnity.getContent()));
					String line = null;			
					responseBuffer = new StringBuffer();
					while ((line = reader.readLine()) != null)
					{
						responseBuffer.append(line);
						responseBuffer.append('\r');
					}
					reader.close();
				} else if ( (statusCode != 404) && (statusCode != 422)){
					System.out.println(String.format("Unexpected response code: %s", response.getStatusLine()));
					System.out.println(String.format("targetURL: %s", targetURL));
				}
				EntityUtils.consume(responseEnity);
				//System.out.println(responseBuffer+"\n");
			} finally {
				if (response != null) {
					response.close();
				}
			}
		}
		if (responseBuffer == null) return null;
		return responseBuffer.toString();
	}

	public static String byteArrayToHexString(byte[] b)
	{
		String result = "";
		for (int i = 0; i < b.length; i++)
		{
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	public static String byteArrayToABCString(byte[] b)
	{
		String result = "";
		for (int i = 0; i < b.length; i++)
		{
			result += Integer.toString((b[i] & 0xff) + 0x100, 32).substring(1);
		}
		return result;
	}

	public static int excutePut(String targetURL, String urlParameters) throws InterruptedException
	{
		// System.out.println("Post parameters : " + urlParameters);

		URL url;
		HttpURLConnection connection = null;
		try
		{
			// Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("PUT");
			// connection.setRequestProperty("Content-Type",
			// "application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("charset", "utf-8");

			connection.setUseCaches(false);
			// connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
			writer.write(urlParameters);
			writer.close();
			// wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = connection.getResponseCode();

			if (responseCode != 200)
			{
				System.out.println("\nSending 'PUT' request to URL : " + url);
				System.out.println("Post parameters : " + urlParameters);
				System.out.println("Response Code : " + responseCode);
				System.out.println();
			}

			return responseCode;

		} catch (java.net.NoRouteToHostException e)
		{
			if (connection != null)
			{
				connection.disconnect();
				connection = null;
			}

			// e.printStackTrace();
			Thread.currentThread().sleep(1000);
			System.out.println("retry");
			return excutePut(targetURL, urlParameters);
		} catch (Exception e)
		{
			//e.printStackTrace();
			return -1;

		} finally
		{

			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}

	private static ByteArrayPartSource getResourceAsPartSource(String fileName, InputStream in) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		while (true)
		{
			int rc = in.read(buffer);
			if (rc < 0)
				break;
			out.write(buffer, 0, rc);
		}
		in.close();
		return new ByteArrayPartSource(fileName, out.toByteArray());
	}

	public static int upload(String uri, InputStream in, String fileName, String targetDir) throws Exception
	{
		ByteArrayPartSource source = getResourceAsPartSource(fileName, in);

		PostMethod filePost = new PostMethod(uri);

		filePost.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);
		filePost.addParameter("url", targetDir);
		filePost.addParameter("fileName", fileName);

		FilePart part = new FilePart("file", source);
		part.setContentType("multipart/form-data");
		part.setTransferEncoding("binary");

		StringPart sp = new StringPart("path", targetDir);

		Part[] parts =
		{ part, sp };

		filePost.setRequestEntity(new MultipartRequestEntity(parts, filePost.getParams()));

		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

		int status = client.executeMethod(filePost);
		if (status == HttpStatus.SC_OK)
		{
			System.out.println("uploaded file:" + targetDir + "/" + fileName);
			return 0;
		} else
		{
			System.out.println("responseString:" + filePost.getResponseBodyAsString());
			return -1;
		}

	}

	public static final CharSequenceTranslator ESCAPE_JSON1 = new AggregateTranslator(new LookupTranslator(new String[][]
	{
			{ "\"", "\\\"" },
			{ "\\", "\\\\" }, }), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE()));

	public static String forJSON(String input)
	{
		String res = ESCAPE_JSON1.translate(input);

		return res;
	}

	public static boolean isNumeric(String str)
	{
		return str.matches("-?\\d+(\\.\\d+)?"); // match a number with optional '-' and decimal.
	}
}