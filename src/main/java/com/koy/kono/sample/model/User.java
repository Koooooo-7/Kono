package com.koy.kono.sample.model;

import com.koy.kono.kono.model.BaseModel;
import org.jooq.Record;
import org.jooq.Result;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class User extends BaseModel {

    private Integer id;
    private String name;

    private void getUsers() {
        Result<Record> recordResult = this.DB().select().from("user").fetch();
    }
}
