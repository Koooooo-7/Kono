package com.koy.kono.kono;

import com.koy.kono.kono.server.KonoServer;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class Kono {
    public static void run(String[] args) {
        KonoServer konoServer = new KonoServer();
        konoServer.run();
    }
}
