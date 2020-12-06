package io.jexxa.infrastructure.drivingadapter.messaging.listener;

import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public abstract class MessageContainerListener<T> extends TypedMessageListener<T>
{
    private final Function<T,String > payloadAccessor;
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


}
