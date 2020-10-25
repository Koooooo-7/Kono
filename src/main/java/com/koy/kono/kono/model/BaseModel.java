package com.koy.kono.kono.model;

import org.jooq.DSLContext;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class BaseModel implements IModel {

    private DBHandle handle;

    public BaseModel() {
    }

    public BaseModel(DBHandle handle) {
        this.handle = handle;
    }

    public DSLContext DB() {
        return handle.getDslContext();
    }

}
