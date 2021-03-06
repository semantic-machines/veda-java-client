package sm.tools.veda_client;

import java.util.Date;

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

	public String getLang()
	{
		return lang;
	}

	public Resource(String _data)
	{
		data = _data;
		type = Type._String;
	}

	public Resource(String _data, String stype)
	{
		data = _data;

		type = Type._String;

		if (stype.equals("Bool"))
			type = Type._Bool;
		else if (stype.equals("Uri"))
			type = Type._Uri;
		else if (stype.equals("String"))
			type = Type._String;

	}

	public Resource (Date _data)
	{
		util.date2string(_data);
		type = Type._Datetime;
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

	public Resource(boolean _data, int bool)
	{
		if (_data == true)
			data = "true";
		else
			data = "false";
		type = Type._Bool;
	}

	public String toString()
	{
		return "" + data + "(" + type + ")";
	}
}
