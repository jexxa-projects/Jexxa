package io.jexxa.infrastructure.drivingadapter.messaging.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public abstract class MessageContainerListener<T> extends JSONMessageListener<T>
{
    private Function<T,String > payloadAccessor;
    private T currentMessageContainer;


    protected MessageContainerListener(Class<T> clazz, Function<T,String > payloadAccessor)
    {
        super(clazz);
        this.payloadAccessor = payloadAccessor;
    }

    @Override
    protected final void onMessage(T message)
    {
        this.currentMessageContainer = message;

        onMessageContainer( message );

        this.currentMessageContainer = null;
    }

    protected abstract void onMessageContainer(T messageContainer );

    protected boolean payloadContains(String attribute)
    {
        JsonElement jsonElement = JsonParser.parseString(payloadAccessor.apply(getMessageContainer()));
        return deepSearchKeys(jsonElement, attribute)
                .stream()
                .findFirst()
                .isPresent();
    }

    protected <U> U getFromPayload(String key, Class<U> clazz)
    {
        JsonElement jsonElement = JsonParser.parseString(payloadAccessor.apply(getMessageContainer()));

        var result = deepSearchKeys( jsonElement, key )
                .stream()
                .findFirst()
                .orElseThrow();

        return fromJson(result.toString(), clazz);
    }

    protected T getMessageContainer()
    {
        return currentMessageContainer;
    }

    private List<JsonElement> deepSearchKeys(JsonElement jsonElement, String key)
    {
        List<JsonElement> result = new ArrayList<>();
        deepSearchKeys(jsonElement, key, result);
        return result;
    }


    private void deepSearchKeys(JsonElement jsonElement, String key, List<JsonElement> result)
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
