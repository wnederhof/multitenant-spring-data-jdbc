package com.example.multitenant.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.support.ScopeNotActiveException;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class TenantAwareDataSource extends DelegatingDataSource {
    private final TenantRequestContext tenantRequestContext;

    public TenantAwareDataSource(TenantRequestContext tenantRequestContext, DataSource targetDataSource) {
        super(targetDataSource);
        this.tenantRequestContext = tenantRequestContext;
    }

    @NotNull
    @Override
    public Connection getConnection() throws SQLException {
        var connection = Objects.requireNonNull(getTargetDataSource()).getConnection();
        setTenantId(connection);
        return getTenantAwareConnectionProxy(connection);
    }

    @NotNull
    @Override
    public Connection getConnection(@NotNull String username, @NotNull String password) throws SQLException {
        var connection = Objects.requireNonNull(getTargetDataSource()).getConnection();
        setTenantId(connection);
        return getTenantAwareConnectionProxy(connection);
    }

    private void setTenantId(Connection connection) throws SQLException {
        Integer tenantId;
        try {
            tenantId = tenantRequestContext.getTenantId();
            if (tenantId == null) {
                tenantId = -1;
            }
        } catch (ScopeNotActiveException e) {
            tenantId = -1;
        }
        try (var statement = connection.createStatement()) {
            statement.execute("SET app.tenant_id TO '" + tenantId + "'");
        }
    }

    private Connection getTenantAwareConnectionProxy(Connection connection) {
        return (Connection) Proxy.newProxyInstance(
                ConnectionProxy.class.getClassLoader(),
                new Class[]{ConnectionProxy.class},
                new TenantAwareInvocationHandler(connection)
        );
    }

    static class TenantAwareInvocationHandler implements InvocationHandler {
        private final Connection connection;

        TenantAwareInvocationHandler(Connection connection) {
            this.connection = connection;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return switch (method.getName()) {
                case "unwrap" -> {
                    if (((Class<?>) args[0]).isInstance(proxy)) {
                        yield proxy;
                    }
                    yield method.invoke(connection, args);
                }
                case "isWrapperFor" -> {
                    if (((Class<?>) args[0]).isInstance(proxy)) {
                        yield true;
                    }
                    yield method.invoke(connection, args);
                }
                case "close" -> {
                    try (var s = connection.createStatement()) {
                        s.execute("RESET app.tenant_id");
                    }
                    yield method.invoke(connection, args);
                }
                case "getTargetConnection" -> connection;
                default -> method.invoke(connection, args);
            };
        }
    }

}
