package io.jexxa.common.wrapper.jms;

import io.jexxa.common.properties.Secret;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class JMSConnection {
    public static Connection createConnection(Properties properties)
    {
        var username = new Secret(properties, JMSProperties.JNDI_USER_KEY, JMSProperties.JNDI_USER_FILE);
        var password = new Secret(properties, JMSProperties.JNDI_PASSWORD_KEY, JMSProperties.JNDI_PASSWORD_FILE);

        try
        {
            var initialContext = new InitialContext(properties);
            var connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
            return connectionFactory.createConnection(username.getSecret(), password.getSecret());
        }
        catch (NamingException e)
        {
            throw new IllegalStateException("No ConnectionFactory available via : " + properties.get(JMSProperties.JNDI_PROVIDER_URL_KEY), e);
        }
        catch (JMSException e)
        {
            throw new IllegalStateException("Can not connect to " + properties.get(JMSProperties.JNDI_PROVIDER_URL_KEY), e);
        }
    }

    private JMSConnection()
    {
        //Private constructor
    }
}
