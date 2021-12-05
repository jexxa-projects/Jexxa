package io.jexxa.infrastructure.drivingadapter.messaging.listener;

import static io.jexxa.utils.json.JSONManager.getJSONConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.jms.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.jexxa.utils.JexxaLogger;

@SuppressWarnings("unused")
public abstract class JSONMessageListener implements MessageListener
{
    private Message currentMessage;
    private String currentMessageText;

    public abstract void onMessage(String message);

    @Override
    public final void onMessage(Message message)
    {
        try
        {
            this.currentMessage = message;
            if (message instanceof TextMessage)
            {
                TextMessage textMessage = (TextMessage)currentMessage;
                this.currentMessageText = textMessage.getText();
            } else if ( message instanceof BytesMessage) {
                BytesMessage byteMessage = (BytesMessage) currentMessage;
                byte[] payload = new byte[(int) byteMessage.getBodyLength()];
                byteMessage.readBytes(payload);
                this.currentMessageText = Arrays.toString(payload);
            }

            onMessage( currentMessageText );
        }
        catch (RuntimeException | JMSException exception)
        {
            JexxaLogger.getLogger(getClass()).error(exception.getMessage());
        }
        currentMessage = null;
        currentMessageText = null;
    }

    protected final Message getCurrentMessage()
    {
        return currentMessage;
    }

    protected static <U> U fromJson( String message, Class<U> clazz)
    {
        return getJSONConverter().fromJson( message, clazz);
    }

    protected boolean messageContains(String attribute)
    {
        var jsonElement = JsonParser.parseString(currentMessageText);
        return deepSearchKeys(jsonElement, attribute)
                .stream()
                .findFirst()
                .isPresent();
    }

    protected <U> U getFromMessage(String key, Class<U> clazz)
    {
        var jsonElement = JsonParser.parseString(currentMessageText);

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

