package de.yggdrasil.core.io;

import de.yggdrasil.core.util.ExceptionStrings;
import de.yggdrasil.core.util.ServerConstants;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The ExtensionCopy class provides methods to copy extensions from a specified directory.
 */
public class ExtensionCopy {

    /**
     * Copies the specified extensions from the ROOT_PATH_EXTENSIONS directory.
     *
     * @param extensionNames A list of extension names to be copied.
     * @throws RuntimeException If the ROOT_PATH_EXTENSIONS directory does not exist,
     *                          the files cannot be listed, or the copy operation fails.
     */
    public static void copyExtensions(List<String> extensionNames) {
        String rootPath = ServerConstants.ROOT_PATH_EXTENSIONS;
        File root = new File(rootPath);

        if (!root.exists()) {
            throw new RuntimeException(ExceptionStrings.ROOT_PATH_EXTENSIONS_NOT_FOUND);
        }

        File[] files = root.listFiles();
        if (files == null) {
            throw new RuntimeException(ExceptionStrings.LIST_FILES_FAILED);
        }

        for (File file : files) {
            if (file.isDirectory()) {
                continue;
            }

            for (String extensionName : extensionNames) {
                if (file.getName().contains(extensionName)) {
                    File destination = new File(rootPath, extensionName);
                    try {
                        FileUtils.copyFile(file, destination);
                    } catch (IOException e) {
                        throw new RuntimeException(ExceptionStrings.COPY_EXTENSION_FAILED.formatted(file.getName()), e);
                    }
                }
            }
        }
    }
}
