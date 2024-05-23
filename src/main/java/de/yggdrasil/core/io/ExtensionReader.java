package de.yggdrasil.core.io;

import de.yggdrasil.core.ServerCore;
import de.yggdrasil.core.extension.ServerExtension;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class ExtensionReader {

    public static List<ServerExtension> load(){
        File extensionsDir = new File("extensions");
        File[] jarFiles = extensionsDir.listFiles((dir, name) -> name.endsWith(".jar"));

        if (jarFiles == null || jarFiles.length == 0) {
            System.out.println("No extension JAR files found.");
            return null;
        }

        List<Class<?>> extensionClasses = new ArrayList<>();

        for (File jarFile : jarFiles) {
            try {
                URL jarURL = jarFile.toURI().toURL();
                URLClassLoader classLoader = new URLClassLoader(new URL[]{jarURL}, ServerCore.class.getClassLoader());
                JarFile jar = new JarFile(jarFile);
                jar.stream().forEach(jarEntry -> {
                    if (jarEntry.getName().endsWith(".class")) {
                        String className = jarEntry.getName().replace("/", ".").replace(".class", "");
                        try {
                            Class<?> clazz = classLoader.loadClass(className);
                            if (ServerExtension.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
                                extensionClasses.add(clazz);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
                jar.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final List<ServerExtension> extensionInstances = new ArrayList<>();

        for (Class<?> extensionClass : extensionClasses) {
            try {
                ServerExtension instance = (ServerExtension) extensionClass.getDeclaredConstructor().newInstance();
                extensionInstances.add(instance);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        return extensionInstances;
    }

}
