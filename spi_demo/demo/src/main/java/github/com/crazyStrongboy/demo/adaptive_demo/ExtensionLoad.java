package github.com.crazyStrongboy.demo.adaptive_demo;

import github.com.crazyStrongboy.inter.Driver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ExtensionLoad<T> {

    private static final String MARS_JUN_DIR = "META-INF/mars_jun/";

    private Map<String, Class<?>> extensionClasses = new HashMap<>();

    /**
     * 获取某一个特定的扩展类
     *
     * @param name
     * @return
     */
    public T getExtension(String name) {
        Class<?> mysql_driver = extensionClasses.get(name);
        try {
            Object instance = mysql_driver.newInstance();
            return (T) instance;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加载所有的扩展类
     * @param tClass
     */
    public void loadDirectory(Class tClass) {
        String fileName = MARS_JUN_DIR + tClass.getName();
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {

            Enumeration<URL> resources = classLoader.getResources(fileName);
            if (resources != null) {
                while (resources.hasMoreElements()) {
                    URL url = resources.nextElement();
                    loadResource(classLoader, url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadResource(ClassLoader classLoader, URL resourceURL) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resourceURL.openStream(), "utf-8"));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    final int ci = line.indexOf('#');
                    if (ci >= 0) {
                        line = line.substring(0, ci);
                    }
                    line = line.trim();
                    if (line.length() > 0) {
                        try {
                            String name = null;
                            int i = line.indexOf('=');
                            if (i > 0) {
                                name = line.substring(0, i).trim();
                                line = line.substring(i + 1).trim();
                            }
                            if (line.length() > 0) {
                                extensionClasses.put(name, Class.forName(line, true, classLoader));
                            }
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }
            } finally {
                reader.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void print() {
        extensionClasses.forEach(
                (key, value) -> {
                    try {
                        Object instance = value.newInstance();
                        if (instance instanceof Driver) {
                            Driver driver = (Driver) instance;
                            driver.connect("aa");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }
}
