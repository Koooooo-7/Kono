package com.koy.kono.kono.model;

import com.koy.kono.kono.core.Configuration;
import org.jooq.*;


/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class EngineFactory implements Engine {


    @Override
    public DSLContext engine(Configuration configuration, SQLDialect engineType) {

        switch (engineType) {
            case MYSQL:
                return new MySQLEngine().engine(configuration, SQLDialect.MYSQL);
            case H2:
                return new H2Engine().engine(configuration, SQLDialect.H2);
            default:
                return new MySQLEngine().engine(configuration, engineType);
        }
    }
}
