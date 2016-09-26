package sm.tools.veda_client;

public class Resource
{
	String data;
	String lang = "NONE";
	int type;

	public String getData()
	{
		return data;
	}

	public int getType()
	{
		return type;
	}

	public Resource(String _data)
	{
		data = _data;
		type = Type._String;
	}

	public Resource(String _data, int _type)
	{
		data = _data;
		type = _type;
	}

	public Resource(String _data, int _type, String _lang)
	{
		data = _data;
		type = _type;
		lang = _lang;
	}

	public String toString()
	{
		return "" + data + "(" + type + ")";
	}
}
