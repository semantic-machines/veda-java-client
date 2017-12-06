package sm.tools.veda_client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Resources
{
	public List<Resource> resources = new ArrayList<Resource>();

	public Resources()
	{

	}

	public Resources add(Resource rc)
	{
		for (Resource i_rc : resources)
		{
			if (i_rc.data.equals(rc.data) && i_rc.type == rc.type && i_rc.lang == rc.lang)
				return this;
		}

		resources.add(rc);
		return this;
	}

	public Resources add(String _data, String _lang)
	{
		if (_data != null)
		{
			_data = _data.replace("\\", "\\\\");

			Resource rc = new Resource(_data, 2, _lang);

			for (Resource i_rc : resources)
			{
				if (i_rc.data.equals(rc.data) && i_rc.type == rc.type && i_rc.lang == rc.lang)
					return this;
			}

			resources.add(rc);
		}
		return this;
	}

	public Resources add(String _data, int _type)
	{
		if (_data != null)
		{
			if (_type == Type._String)
				_data = _data.replace("\\", "\\\\");

			Resource rc = new Resource(_data, _type);

			for (Resource i_rc : resources)
			{
				if (i_rc.data.equals(rc.data) && i_rc.type == rc.type && i_rc.lang == rc.lang)
					return this;
			}

			resources.add(rc);
		}
		return this;
	}

	public Resources add(Date _data)
	{
		if (_data != null)
		{
			Resource rc = new Resource(util.date2string(_data), Type._Datetime);
			for (Resource i_rc : resources)
			{
				if (i_rc.data.equals(rc.data) && i_rc.type == rc.type && i_rc.lang == rc.lang)
					return this;
			}

			resources.add(rc);
		}
		return this;
	}

	public String toString()
	{
		return "" + resources;
	}
}