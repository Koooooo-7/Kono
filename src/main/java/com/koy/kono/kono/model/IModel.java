package com.koy.kono.kono.model;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public interface IModel {

    // get the type from the defined model
    default <T> Class<? extends T> getType(String property) {
        return null;
    }

}
