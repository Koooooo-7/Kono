package com.koy.kono.kono.core;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author Koy  https://github.com/Koooooo-7
 * @Description
 */

public class ControllerClassLoader extends ClassLoader {

    @SneakyThrows
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        final Enumeration<URL> urls = ControllerClassLoader.class.getClassLoader().getResources(name);
        return super.findClass(name);
    }

    private Set<URL> loadResource(String resourceName, ClassLoader classLoader) {
        final Set<URL> result = new LinkedHashSet<>();
        try {
            final Enumeration<URL> urls = classLoader.getResources(resourceName);
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                int index = url.toExternalForm().lastIndexOf(resourceName);
                if (index != -1) {
                    // Add old url as contextUrl to support exotic url handlers
                    result.add(new URL(url, url.toExternalForm().substring(0, index)));
                } else {
                    result.add(url);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public String getRelativePath(String filePath, File root) {
        String filepath = filePath.replace("\\", "/");
        if (filepath.startsWith(root.getPath())) {
            return filepath.substring(root.getPath().length() + 1);
        }

        return null; //should not get here
    }
}

