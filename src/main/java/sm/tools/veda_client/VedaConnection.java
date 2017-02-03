package sm.tools.veda_client;

import java.net.URLEncoder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class VedaConnection
{
	boolean is_enable_store = true;

	public VedaConnection(String _url) throws Exception
	{
		jp = new JSONParser();
		destination = _url;
		vedaTicket = getVedaTicket();

		if ((vedaTicket == null) || (vedaTicket.length() < 1))
		{
			System.out.println("Destination.Veda.Login filed");
			_isOk = false;
		}
		_isOk = true;
	}

	public void disableStore()
	{
		is_enable_store = false;
	}

	public void enabelStore()
	{
		is_enable_store = true;
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

	public int putIndividual(Individual indv, boolean isPrepareEvent) throws InterruptedException
	{
		if (is_enable_store == false)
			return 200;

		boolean is_store = false;

		Resources types = indv.getResources("rdf:type");

		if (types != null)
		{
			for (Resource rs : types.resources)
			{
				if (rs.data.equals("v-s:ContractorProfileFile") || rs.data.equals("v-s:File"))
				{
					is_store = true;
					break;
				}
			}
		}

		if (is_store)
		{
			String jsn = indv.toJsonStr();
			String ijsn = "{\"@\":\"" + indv.getUri() + "\"," + jsn + "}";
			int res = putJson(ijsn, isPrepareEvent);
			return res;
		} else
			return 200;
	}

	private int putJson(String jsn, boolean isPrepareEvent) throws InterruptedException
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
			String query = "{\"ticket\":\"" + vedaTicket + "\", \"individual\":" + jsn + ", \"prepare_events\":" + isPrepareEvent
					+ ", \"event_id\":\"\", " + "\"transaction_id\":\"\" }";
			res = util.excutePut(destination + "/put_individual", query);

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

	public String getVedaTicket() throws Exception
	{
		String res = util
				.excuteGet(destination + "/authenticate?login=karpovrt&password=a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3");

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
