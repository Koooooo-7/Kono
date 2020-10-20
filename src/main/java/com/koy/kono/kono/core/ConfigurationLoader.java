package com.koy.kono.kono.core;

import org.yaml.snakeyaml.Yaml;

import java.io.*;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class ConfigurationLoader {
    public static final String CONFIG_FILE_NAME = "/configuration.yml";

    public Configuration configuration() {
        Configuration configuration = new Configuration();
        try {
            configuration = this.loadResource();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return configuration;
    }

    private Configuration loadResource() {
        Configuration configuration = new Configuration();

        try (InputStream in = ConfigurationLoader.class.getResourceAsStream(ConfigurationLoader.CONFIG_FILE_NAME)) {
            Yaml yaml = new Yaml();
            configuration = yaml.loadAs(in, Configuration.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return configuration;
    }

}
