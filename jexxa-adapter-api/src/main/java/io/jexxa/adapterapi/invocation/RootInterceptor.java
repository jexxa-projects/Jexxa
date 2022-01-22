package io.jexxa.adapterapi.invocation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class RootInterceptor implements Interceptor, InvocationHandler
{
    private final List<Interceptor> beforeList = new ArrayList<>();
    private final List<Interceptor> afterList = new ArrayList<>();
    private final List<Interceptor> surroundList = new ArrayList<>();

    @Override
    public void before( InvocationContext invocationContext )
    {
        beforeList.forEach(element -> element.before(invocationContext));
    }

    @Override
    public void after( InvocationContext invocationContext )
    {
        afterList.forEach( element -> element.after(invocationContext));
    }

    @Override
    public void surround( InvocationContext invocationContext ) throws InvocationTargetException, IllegalAccessException {
        invocationContext.invoke();
    }

    public void register(Interceptor interceptor)
    {
        beforeList.add(interceptor);
        afterList.add(interceptor);
        surroundList.add(interceptor);
    }

    @Override
    public void invoke(InvocationContext invocationContext) throws InvocationTargetException, IllegalAccessException {
        before(invocationContext);
        surround(invocationContext);
        after(invocationContext);
    }
}
