package com.koy.kono.kono.model;

import com.koy.kono.kono.core.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class DBHandle implements Engine, IModel {

    private DSLContext dslContext;

    @Override
    public DSLContext engine(Configuration configuration, SQLDialect engineType) {
        this.dslContext = new EngineFactory().engine(configuration, engineType);
        return dslContext;
    }

    public DSLContext getDslContext() {
        return dslContext;
    }
}
