package sm.tools.veda_client;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Individual
{
	private static int _as_json = 1;
	private static int _as_struct = 2;

	private int type_of_data = _as_json;

	private JSONObject js_src;
	private HashMap<String, Resources> data = null;
	private String uri;

	public String toString()
	{
		return "@:" + uri + " " + data.toString();
	}

	public String[] getPredicates()
	{
		if (type_of_data == _as_json)
			getResources("@");

		return data.keySet().toArray(new String[0]);
	}

	public Resources addProperty(String field_name, Date _data)
	{
		Resources res;

		if (type_of_data == _as_json)
			getResources("@");

		res = data.get(field_name);
		if (res == null)
		{
			res = new Resources();
			data.put(field_name, res);
		}

		res.add(_data);
		return res;
	}

	public Resources addProperty(String field_name, String value, int type)
	{
		Resources res = null;

		if (type_of_data == _as_json)
			getResources("@");

		res = data.get(field_name);
		if (res != null)
			for (Resource rc : res.resources)
				if (rc.data.equals(value) && rc.type == type)
					return res;

		if (res == null)
		{
			res = new Resources();
			data.put(field_name, res);
		}

		res.add(new Resource(value, type));
		return res;
	}

	public Resources addProperty(String field_name, String value, String lang)
	{
		Resources res;

		if (type_of_data == _as_json)
			getResources("@");

		res = data.get(field_name);
		if (res != null)
			for (Resource rc : res.resources)
				if (rc.data.equals(value) && rc.type == Type._String)
					return res;

		if (res == null)
		{
			res = new Resources();
			data.put(field_name, res);
		}

		res.add(new Resource(value, Type._String, lang));
		return res;
	}

	public Resources addProperty(String field_name, Resources rsz)
	{
		Resources res = new Resources();

		if (type_of_data == _as_json)
			getResources("@");

		res = data.get(field_name);
		if (res == null)
		{
			res = new Resources();
			data.put(field_name, res);
		}

		if (rsz != null)
		{
			for (Resource rs : rsz.resources)
			{
				res.add(rs);
			}
		}
		return res;
	}

	public Resources addProperty(String field_name, Resource rs)
	{
		Resources res;

		if (type_of_data == _as_json)
			getResources("@");

		res = data.get(field_name);
		if (res == null)
		{
			res = new Resources();
			data.put(field_name, res);
		}

		res.add(rs);

		return res;
	}

	public Resources setProperty(String field_name, Resource rs)
	{
		Resources res = new Resources();

		if (type_of_data == _as_json)
			getResources("@");

		res.add(rs);
		data.put(field_name, res);

		return res;
	}

	public Resources setProperty(String field_name, Resources rsz)
	{
		if (type_of_data == _as_json)
			getResources("@");

		data.put(field_name, rsz);
		return rsz;
	}

	public void removeProperty(String field_name)
	{
		if (type_of_data == _as_json)
			getResources("@");

		data.remove(field_name);
		return;
	}

	public Individual()
	{
		type_of_data = _as_struct;
		data = new HashMap<String, Resources>();
	}

	public Individual(JSONObject src)
	{
		js_src = src;
		type_of_data = _as_json;
	}

	public String getUri()
	{
		if (type_of_data == _as_json)
			return getValue("@");
		if (type_of_data == _as_struct)
			return uri;
		return null;
	}

	public void setUri(String uri)
	{
		this.uri = uri;
	}

	public Resources getResources(String field_name)
	{
		Resources res = null;

		if (type_of_data == _as_json)
		{
			type_of_data = _as_struct;

			res = new Resources();
			data = new HashMap<String, Resources>();
			Set<String> keys = js_src.keySet();
			for (String key : keys)
			{
				if (key.equals("@"))
					uri = (String) js_src.get(key);
				else
				{
					Resource rs = null;

					JSONArray code_objz = (JSONArray) js_src.get(key);
					if (code_objz != null)
					{
						for (int idx = 0; idx < code_objz.size(); idx++)
						{
							// JSONObject vobj = (JSONObject)code_objz.get(idx);

							String value;
							int type = 0;

							JSONObject jsval = (JSONObject) (code_objz.get(idx));

							value = jsval.get("data").toString();
							String stype = jsval.get("type").toString();
							Object olang = jsval.get("lang");
							String lang = "NONE";

							if (olang != null)
								lang = olang.toString();

							if (stype.equals("Boolean"))
								type = Type._Bool;
							else if (stype.equals("String"))
								type = Type._String;
							else if (stype.equals("Uri"))
								type = Type._Uri;
							else if (stype.equals("Datetime"))
								type = Type._Datetime;
							else if (stype.equals("Integer"))
								type = Type._Integer;
							else if (stype.equals("Decimal"))
								type = Type._Decimal;
							else
								type = 0;

							rs = new Resource(value, type, lang);
							addProperty(key, rs);
							res.add(rs);
						}
					}

				}

				// if (oo instanceof String)
			}

		}

		res = data.get(field_name);

		return res;
	}

	public String getValue(String field_name)
	{
		String res = null;

		if (type_of_data == _as_json)
		{
			Object oo = js_src.get(field_name);
			if (oo instanceof String)
				return (String) js_src.get(field_name);

			JSONArray code_obj = (JSONArray) js_src.get(field_name);
			if (code_obj != null)
			{
				Object data = ((JSONObject) (code_obj.get(0))).get("data");

				String code = null;

				if (data instanceof String)
					code = (String) ((JSONObject) (code_obj.get(0))).get("data");
				else if (data instanceof Long)
					code = ((Long) ((JSONObject) (code_obj.get(0))).get("data")) + "";

				if (code != null)
				{
					return code;
				}
			}
		}

		if (type_of_data == _as_struct)
		{
			Resources rss = getResources(field_name);

			if (rss != null && rss.resources.size() > 0)
			{
				return rss.resources.get(0).getData();
			}
		}

		return res;
	}

	public String toJsonStr()
	{
		if (type_of_data == _as_json)
			getResources("@");

		StringBuffer sb = new StringBuffer();
		for (String key : data.keySet())
		{
			Resources rcs = data.get(key);
			util.serializeResources(sb, key, rcs);
		}
		return sb.toString();
	}
}
