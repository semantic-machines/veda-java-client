package sm.tools.veda_client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Individual
{
	private JSONObject js_src;

	Individual(JSONObject src)
	{
		js_src = src;
	}

	public String getUri()
	{
		return getValue("@");
	}

	public String getValue(String field_name)
	{
		String res = null;

		Object oo = js_src.get(field_name);
		if (oo instanceof String)
			return (String) js_src.get(field_name);

		JSONArray code_obj = (JSONArray) js_src.get(field_name);
		if (code_obj != null)
		{
			String code = (String) ((JSONObject) (code_obj.get(0))).get("data");
			if (code != null)
			{
				return code;
			}
		}
		return res;
	}
}
