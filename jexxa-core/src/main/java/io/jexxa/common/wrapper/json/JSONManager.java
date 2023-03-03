package io.jexxa.common.wrapper.json;

import io.jexxa.common.wrapper.json.gson.GsonConverter;

@SuppressWarnings("unused")
public final class JSONManager
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
