package com.koy.kono.kono.core;

import com.koy.kono.kono.enums.Protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description The configuration files mapping class.
 */

public class Configuration {

    private Integer port = 9001;
    private Protocol protocol = Protocol.HTTP;
    // TODO: multi location
    private String controllerLocation = "com.koy.kono.app.controller";

    private Map<String, String> routers = new HashMap<>();

    private DataSource dataSource;

    public static class DataSource {
        private String prefix;
        private String url;
        private String userName;
        private String password;

        public DataSource() {
        }

        public DataSource(String prefix, String url, String userName, String password) {
            this.prefix = prefix;
            this.url = url;
            this.userName = userName;
            this.password = password;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getUrl() {
            return url;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getControllerLocation() {
        return controllerLocation;
    }

    public void setControllerLocation(String controllerLocation) {
        this.controllerLocation = controllerLocation;
    }

    public Map<String, String> getRouters() {
        return routers;
    }

    public void setRouters(Map<String, String> routers) {
        this.routers = routers;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
