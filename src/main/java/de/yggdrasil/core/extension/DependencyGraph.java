package de.yggdrasil.core.extension;

import java.util.*;

/**
 * The DependencyGraph class models the dependencies between extensions and allows for a topological sorting
 * to determine a valid load order of the extensions.
 */
public class DependencyGraph {

    /**
     * Adjacency list storing the dependencies between the extensions.
     * The key is the name of an extension, and the value is a list of extensions that depend on this extension.
     */
    private final Map<String, List<String>> adjList = new HashMap<>();

    /**
     * Map storing the number of incoming edges (dependencies) for each extension.
     * The key is the name of an extension, and the value is the number of extensions that depend on this extension.
     */
    private final Map<String, Integer> inDegree = new HashMap<>();

    /**
     * Adds an extension and its dependencies to the graph.
     *
     * @param extension The extension to be added.
     */
    public void addExtension(ServerExtension extension) {
        adjList.putIfAbsent(extension.name, new ArrayList<>());
        inDegree.putIfAbsent(extension.name, 0);

        for (String dep : extension.dependencies) {
            adjList.putIfAbsent(dep, new ArrayList<>());
            adjList.get(dep).add(extension.name);
            inDegree.put(extension.name, inDegree.getOrDefault(extension.name, 0) + 1);
        }
    }

    /**
     * Checks if the graph contains a cycle.
     *
     * @return true if a cycle is present, otherwise false.
     */
    public boolean hasCycle() {
        return topologicalSort().isEmpty();
    }

    /**
     * Performs a topological sort of the extensions.
     *
     * @return A list of extensions in topologically sorted order.
     *         Returns an empty list if a cycle is present.
     */
    public List<String> topologicalSort() {
        List<String> order = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();

        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        while (!queue.isEmpty()) {
            String current = queue.poll();
            order.add(current);

            List<String> neighbors = adjList.getOrDefault(current, Collections.emptyList());
            for (String neighbor : neighbors) {
                int newInDegree = inDegree.get(neighbor) - 1;
                inDegree.put(neighbor, newInDegree);
                if (newInDegree == 0) {
                    queue.add(neighbor);
                }
            }
        }

        return order.size() == adjList.size() ? order : Collections.emptyList(); // Cycle check
    }

}
