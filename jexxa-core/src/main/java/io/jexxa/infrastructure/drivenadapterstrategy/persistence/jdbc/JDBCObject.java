package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

public class JDBCObject {
    private final Object jdbcValue;
    //private final SQLType sqlType;
    private final String bindParameter;

    public JDBCObject(Object jdbcValue, /*SQLType sqlType,*/ String bindParameter) {
        this.jdbcValue = jdbcValue;
        //this.sqlType = sqlType;
        this.bindParameter = bindParameter;
    }

    public Object getJdbcValue() {
        return jdbcValue;
    }

    /*public SQLType getSqlType() {
        return sqlType;
    }*/

    public String getBindParameter()
    {
        return bindParameter;
    }

}
