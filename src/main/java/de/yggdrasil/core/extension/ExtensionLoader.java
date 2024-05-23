package de.yggdrasil.core.extension;

import de.yggdrasil.core.io.ExtensionReader;

import java.util.*;

public class ExtensionLoader {

    private final List<ServerExtension> extensions = new ArrayList<>();
    private final DependencyGraph dependencyGraph = new DependencyGraph();

    public void loadExtensions() {
        this.createLoadingOrder(collectExtensions());
        this.extensions.forEach(
                serverExtension -> serverExtension.initialize());
    }

    private Map<String, ServerExtension> collectExtensions() {
        HashMap<String, ServerExtension> extensions = new HashMap<>();
        List<ServerExtension> loadedExtensions = ExtensionReader.load();
        loadedExtensions.forEach(
                serverExtension -> extensions.put(serverExtension.name, serverExtension));
        return extensions;
    }

    private void createLoadingOrder(Map<String,ServerExtension> extensionMap) {
        extensions.forEach(e -> dependencyGraph.addPlugin(e));
        dependencyGraph.topologicalSort().forEach(
                name -> this.extensions.add(extensionMap.get(name)));
    }

    public void terminate(){
        this.extensions.forEach(
                serverExtension -> serverExtension.terminate());
    }

}
