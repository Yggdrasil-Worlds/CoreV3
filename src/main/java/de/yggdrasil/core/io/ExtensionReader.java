package de.yggdrasil.core.io;

import de.yggdrasil.core.ServerCore;
import de.yggdrasil.core.extension.ServerExtension;
import de.yggdrasil.core.util.LoggingStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

/**
 * The ExtensionReader class provides methods to load and instantiate ServerExtensions from JAR files.
 */
public class ExtensionReader {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExtensionReader.class);

    /**
     * Loads the ServerExtensions from the "extensions" directory.
     *
     * @return A list of instances of the loaded ServerExtensions.
     */
    public static List<ServerExtension> load() {
        File extensionsDir = new File("extensions");
        File[] jarFiles = extensionsDir.listFiles((dir, name) -> name.endsWith(".jar"));

        if (jarFiles == null || jarFiles.length == 0) {
            LOGGER.warn(LoggingStrings.NO_EXTENSIONS_FOUND);
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
