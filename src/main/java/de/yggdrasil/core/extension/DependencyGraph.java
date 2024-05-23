package de.yggdrasil.core.extension;

import java.util.*;

public class DependencyGraph {

    private Map<String, List<String>> adjList = new HashMap<>();
    private Map<String, Integer> inDegree = new HashMap<>();

    public void addPlugin(ServerExtension plugin) {
        adjList.putIfAbsent(plugin.name, new ArrayList<>());
        inDegree.putIfAbsent(plugin.name, 0);
        for (String dep : plugin.dependencies) {
            adjList.putIfAbsent(dep, new ArrayList<>());
            adjList.get(dep).add(plugin.name);
            inDegree.put(plugin.name, inDegree.getOrDefault(plugin.name, 0) + 1);
        }
    }

    public boolean hasCycle() {
        return !topologicalSort().isEmpty();
    }

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

            List<String> neighbors = adjList.get(current);
            if (neighbors != null) {
                for (String neighbor : neighbors) {
                    inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                    if (inDegree.get(neighbor) == 0) {
                        queue.add(neighbor);
                    }
                }
            }
        }

        if (order.size() != adjList.size()) {
            return new ArrayList<>(); // Zyklus gefunden, keine gültige Sortierung möglich
        }

        return order;
    }

}
