package io.jexxa.infrastructure.drivingadapter.messaging.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.jexxa.utils.JexxaLogger;

@SuppressWarnings("unused")
public abstract class GenericJSONMessageListener  implements MessageListener
{
    private static final Gson gson = new Gson();
    private TextMessage currentMessage;
    private String currentMessageText;

    protected abstract void onMessage(TextMessage message);

    @Override
    public final void onMessage(Message message)
    {
        try
        {
            this.currentMessage = (TextMessage) message;
            this.currentMessageText = currentMessage.getText();
            onMessage( currentMessage );
        }
        catch (RuntimeException | JMSException exception)
        {
            JexxaLogger.getLogger(GenericJSONMessageListener.class).error(exception.getMessage());
        }
        currentMessage = null;
        currentMessageText = null;
    }

    protected final TextMessage getCurrentMessage()
    {
        return currentMessage;
    }

    protected static <U> U fromJson( String message, Class<U> clazz)
    {
        return gson.fromJson( message, clazz);
    }

    protected boolean messageContains(String attribute)
    {
        JsonElement jsonElement = JsonParser.parseString(currentMessageText);
        return deepSearchKeys(jsonElement, attribute)
                .stream()
                .findFirst()
                .isPresent();
    }

    protected <U> U getFromMessage(String key, Class<U> clazz)
    {
        JsonElement jsonElement = JsonParser.parseString(currentMessageText);

        var result = deepSearchKeys( jsonElement, key )
                .stream()
                .findFirst()
                .orElseThrow();

        return fromJson(result.toString(), clazz);
    }

    protected List<JsonElement> deepSearchKeys(JsonElement jsonElement, String key)
    {
        List<JsonElement> result = new ArrayList<>();
        deepSearchKeys(jsonElement, key, result);
        return result;
    }


    protected void deepSearchKeys(JsonElement jsonElement, String key, List<JsonElement> result)
    {
        Objects.requireNonNull(jsonElement);

        if ( jsonElement.isJsonObject() )
        {
            jsonElement.getAsJsonObject().entrySet().forEach(element -> {
                if ( element.getKey().equals(key) ) {
                    result.add(element.getValue());
                }
                deepSearchKeys( element.getValue(), key, result);
            });
        }
    }
}

