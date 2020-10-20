package com.koy.kono.kono;

import com.koy.kono.kono.server.KonoServer;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class App {
    public static void main(String[] args) {
        KonoServer konoServer = new KonoServer();
        konoServer.run();
    }
}
