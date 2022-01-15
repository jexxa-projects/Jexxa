package io.jexxa.adapterapi.invocation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class RootInterceptor implements Interceptor, InvocationHandler
{
    private List<Interceptor> beforeList = new ArrayList<>();
    private List<Interceptor> afterList = new ArrayList<>();
    private List<Interceptor> surroundList = new ArrayList<>();

    @Override
    public void before( InvocationContext invocationContext )
    {
        beforeList.forEach( element -> {
            try {
                element.before(invocationContext);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
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
