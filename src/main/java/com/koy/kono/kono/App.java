package com.koy.kono.kono;

import com.koy.kono.kono.server.KonoServer;

public class App {
    public static void main(String[] args) {
        KonoServer konoServer = new KonoServer();
        konoServer.run();
    }
}
