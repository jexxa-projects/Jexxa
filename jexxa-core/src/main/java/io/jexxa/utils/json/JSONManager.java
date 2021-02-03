package io.jexxa.utils.json;

import io.jexxa.utils.json.gson.GsonConverter;

@SuppressWarnings("unused")
public class JSONManager
{
    private static JSONConverter jsonConverter = new GsonConverter();

    public static JSONConverter getJSONConverter()
    {
        return jsonConverter;
    }

    public static void setJSONConverter(JSONConverter jsonConverter)
    {
        JSONManager.jsonConverter = jsonConverter;
    }

    private JSONManager()
    {
        //Private constructor
    }
}
