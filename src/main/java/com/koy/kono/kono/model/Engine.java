package com.koy.kono.kono.model;

import com.koy.kono.kono.core.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;


/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public interface Engine {


    DSLContext engine(Configuration configuration, SQLDialect engineType);

}
