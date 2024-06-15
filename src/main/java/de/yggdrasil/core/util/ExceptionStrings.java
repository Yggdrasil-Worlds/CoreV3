package de.yggdrasil.core.util;

public interface ExceptionStrings {

    String MISSING_SERVER_ID = "Missing Server ID in enviorment";
    String ROOT_PATH_EXTENSIONS_NOT_FOUND = "Root path for extensions not found";

    String LIST_FILES_FAILED = "Failed to list files in the root path for shared extension files";
    String COPY_EXTENSION_FAILED = "Failed to copy file: %s";

    String EXTENSION_DEPENDENCY_CYCLE = "Dependency graph has cycle";
}
