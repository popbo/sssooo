package com.stdc.visual.dynamic.provider.datasource;

import com.stdc.core.log.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author: wang_jie
 * @data: 2022/5/16--19:51
 * @describe: 扩展jdbc连接包加载器 固定路径下未加载到，则在内存中进行加载
 */
public class ExtendedJdbcClassLoader extends URLClassLoader {

    private String driver;
    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }


    public ExtendedJdbcClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);

            if (c != null) {
                if (resolve) {
                    resolveClass(c);
                }
                return c;
            }
            try {
                c = findClass(name);
                if (c != null) {
                    if (resolve) {
                        resolveClass(c);
                    }
                    return c;
                }
            } catch (ClassNotFoundException e) {
                // Ignore
            }


            try {
                if (getParent() != null) {
                    c = super.loadClass(name, resolve);
                    if (c != null) {
                        if (resolve) {
                            resolveClass(c);
                        }
                        return c;
                    }
                }
            } catch (ClassNotFoundException e) {
                // Ignore
            }
            try {
                c = findSystemClass(name);
                if (c != null) {
                    if (resolve) {
                        resolveClass(c);
                    }
                    return c;
                }
            } catch (ClassNotFoundException e) {
                // Ignore
            }
            throw new ClassNotFoundException(name);
        }
    }


    @Override
    protected Package getPackage(String name) {
        return null;
    }


    public void addFile(String s) throws IOException {
        File f = new File(s);
        addFile(f);
    }

    public void addFile(File f) throws IOException {
        addFile(f.toURI().toURL());
    }

    public void addFile(URL u) throws IOException {
        try {
            this.addURL(u);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }
    }

    public static void loadJar(String className,String jarPath) {
        try {
            // 创建URL对象，指向外部jar包
            URL url = new File(jarPath).toURI().toURL();
            URL[] urls = new URL[]{url};
            // 使用URLClassLoader来加载jar包
            ClassLoader cl = new URLClassLoader(urls);
            // 加载外部jar包中的类
            Class<?> cls = cl.loadClass(className);
            cls.newInstance();
            Class.forName(className);
            LogUtil.info("加载jar包成功，calssName:"+className+",jarPath:"+jarPath);
        } catch (Exception e) {
            LogUtil.info("加载jar包失败，calssName:"+className+",jarPath:"+jarPath);
            e.printStackTrace();
        }
    }
}
