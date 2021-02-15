package com.koy.kono.kono.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class BootsListener {

    private ApplicationContext applicationContext;
    private List<BootsListener> listeners = new ArrayList<>();

    BootsListener(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void addListener(BootsListener listener) {
        this.listeners.add(listener);
    }

    void notifyListener() {
        this.listeners.forEach(e->e.runOnBootsApplication(this.applicationContext));
    }

    public void runOnBootsApplication(ApplicationContext context) {

    }
}
