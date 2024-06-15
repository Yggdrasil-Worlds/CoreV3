package de.yggdrasil.core.extension;

import de.yggdrasil.core.io.ExtensionReader;
import de.yggdrasil.core.util.ExceptionStrings;

import java.util.*;

/**
 * The ExtensionLoader class loads and manages extensions. It ensures that the extensions
 * are loaded and terminated in a valid order based on their dependencies.
 */
public class ExtensionLoader {

    private final List<ServerExtension> extensions = new ArrayList<>();
    private final DependencyGraph dependencyGraph = new DependencyGraph();

    /**
     * Loads the extensions, creates the load order based on dependencies, and initializes each extension.
     */
    public void loadExtensions() {
        Map<String, ServerExtension> extensionMap = this.collectExtensions();
        if (extensionMap.isEmpty()) return;
        this.createLoadingOrder(extensionMap);
        extensions.forEach(ServerExtension::initialize);
    }

    /**
     * Collects the extensions by loading them from the source and organizing them into a map.
     *
     * @return A map containing the loaded extensions with their names as keys.
     */
    private Map<String, ServerExtension> collectExtensions() {
        Map<String, ServerExtension> extensionMap = new HashMap<>();
        List<ServerExtension> loadedExtensions = ExtensionReader.load();
        loadedExtensions.forEach(serverExtension -> extensionMap.put(serverExtension.name, serverExtension));
        return extensionMap;
    }

    /**
     * Creates the load order of the extensions based on their dependencies.
     *
     * @param extensionMap A map containing the extensions with their names as keys.
     */
    private void createLoadingOrder(Map<String, ServerExtension> extensionMap) {
        extensionMap.values().forEach(dependencyGraph::addExtension);
        if (dependencyGraph.hasCycle()) throw new RuntimeException(ExceptionStrings.EXTENSION_DEPENDENCY_CYCLE);
        List<String> sortedNames = dependencyGraph.topologicalSort();
        sortedNames.forEach(name -> extensions.add(extensionMap.get(name)));
    }

    /**
     * Terminates all loaded extensions.
     */
    public void terminate() {
        extensions.forEach(ServerExtension::terminate);
    }
}
