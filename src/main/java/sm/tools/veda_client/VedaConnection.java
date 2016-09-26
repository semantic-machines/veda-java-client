package sm.tools.veda_client;

import java.net.URLEncoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class VedaConnection
{
	public VedaConnection(String _url) throws Exception
	{
		jp = new JSONParser();
		destination = _url;
		vedaTicket = getVedaTicket();

		if ((vedaTicket == null) || (vedaTicket.length() < 1))
		{
			System.out.println("Destination:Veda:Login filed");
			_isOk = false;
		}
		_isOk = true;
	}

	public boolean isOk()
	{
		return _isOk;
	}

	String destination;
	String vedaTicket;
	boolean _isOk = false;
	JSONParser jp;

	public int putIndividual(Individual indv, boolean isPrepareEvent) throws InterruptedException
	{
		String jsn = indv.toJsonStr();
		String ijsn = "{\"@\":\"" + indv.getUri() + "\"," + jsn + "}";
		int res = putJson(ijsn, isPrepareEvent);
		return res;
	}

	public int putJson(String jsn, boolean isPrepareEvent) throws InterruptedException
	{
		int res = 429;
		int count_wait = 0;
		while (res == 429)
		{
			res = util.excutePut(destination + "/put_individual", "{\"ticket\":\"" + vedaTicket + "\", \"individual\":" + jsn
					+ ", \"prepare_events\":" + isPrepareEvent + ", \"event_id\":\"\", " + "\"transaction_id\":\"\" }");

			if (res == 429)
			{
				Thread.sleep(10);
				count_wait++;
			}

			if (count_wait == 1)
				System.out.print(".");

		}

		return res;
	}

	public String getVedaTicket() throws Exception
	{
		String res = util.excuteGet(
				destination + "/authenticate?login=karpovrt&password=a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3");

		System.out.println(res);
		try
		{
			JSONObject oo = (JSONObject) jp.parse(res);

			return (String) oo.get("id");
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	public String[] query(String query) throws Exception
	{
		String[] res_arr;
		String res = util.excuteGet(destination + "/query?ticket=" + vedaTicket + "&query=" + URLEncoder.encode(query));

		// System.out.println(res);
		try
		{
			Object oo = jp.parse(res);

			if (oo instanceof JSONArray)
			{
				JSONArray arr = (JSONArray) oo;
				res_arr = new String[arr.size()];
				arr.toArray(res_arr);
				return res_arr;
			}
			return null;
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	public Individual getIndividual(String uri) throws Exception
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
			ex.printStackTrace();
		}
		return null;
	}

	public static void copy(Individual src, Individual dest, String field_name)
	{
		Resources rsz = src.getResources(field_name);

		if (rsz != null)
		{
			for (Resource rs : rsz.resources)
				dest.addProperty(field_name, rs.getData(), rs.getType());
		}

	}

}
