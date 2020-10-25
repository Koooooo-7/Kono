package com.koy.kono.kono.model;

import com.koy.kono.kono.core.Configuration;
import io.netty.util.internal.StringUtil;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class MySQLEngine implements Engine {

    @Override
    public DSLContext engine(Configuration configuration, SQLDialect engineType) {
        DSLContext engine = null;
        try {
            Configuration.DataSource dataSource = configuration.getDataSource();
            // if there has the configuration for data source
            if (!isRegisterDataSource(dataSource)) {
                return null;
            }
            Connection conn = DriverManager.getConnection(dataSource.getUrl(), dataSource.getUserName(), dataSource.getPassword());
            engine = DSL.using(conn, engineType);
            return engine;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return engine;
    }


    private boolean isRegisterDataSource(Configuration.DataSource dataSource) {
        return Objects.nonNull(dataSource)
                && StringUtil.isNullOrEmpty(dataSource.getUrl())
                && StringUtil.isNullOrEmpty(dataSource.getUserName());
    }
}
