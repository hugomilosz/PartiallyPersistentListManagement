package com.example.persistentlist;

import java.util.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAutoConfiguration
public class PersistentListApplication {

    private List<List<Integer>> versions;
    private int currentVersion;

    public PersistentListApplication() {
        versions = new ArrayList<>();
        versions.add(new ArrayList<>());
        currentVersion = 1;
    }

    // Request: GET /lists
    @GetMapping("/lists")
    public Map<String, List<Integer>> getVersions() {
        Map<String, List<Integer>> response = new HashMap<>();
        response.put("versions", getAllVersions());
        return response;
    }

    // Request: GET /list/{id}
    @GetMapping("/list/{id}")
    public List<Integer> getList(@PathVariable int id) {
        return versions.get(id - 1);
    }

    // Request: POST /list/{id}
    @PostMapping("/list/{id}")
    public Map<String, Integer> addToList(@PathVariable int id, @RequestBody Map<String, Integer> request) {
        int newElement = request.get("newElement");
        List<Integer> currentList = new ArrayList<>(versions.get(id - 1));
        currentList.add(newElement);
        versions.add(currentList);
        currentVersion++;
        Map<String, Integer> response = new HashMap<>();
        response.put("listVersion", currentVersion);
        return response;
    }

    // Request: DELETE /list/{id}
    @DeleteMapping("/list/{id}")
    public Map<String, Integer> removeFromList(@PathVariable int id, @RequestBody Map<String, Integer> request) {
        int oldElement = request.get("oldElement");
        List<Integer> currentList = new ArrayList<>(versions.get(id - 1));
        currentList.removeIf(element -> element == oldElement);
        versions.add(currentList);
        currentVersion++;
        Map<String, Integer> response = new HashMap<>();
        response.put("listVersion", currentVersion);
        return response;
    }

    // Request: PUT /list/{id}
    @PutMapping("/list/{id}")
    public Map<String, Integer> updateListElement(@PathVariable int id, @RequestBody Map<String, Integer> request) {
        int oldValue = request.get("oldValue");
        int newValue = request.get("newValue");
        List<Integer> currentList = new ArrayList<>(versions.get(id - 1));
        int index = currentList.indexOf(oldValue);
        if (index != -1) {
            currentList.set(index, newValue);
            versions.add(currentList);
            currentVersion++;
        }
        Map<String, Integer> response = new HashMap<>();
        response.put("listVersion", currentVersion);
        return response;
    }

    private List<Integer> getAllVersions() {
        List<Integer> versions = new ArrayList<>();
        for (int i = 1; i <= currentVersion; i++) {
            versions.add(i);
        }
        return versions;
    }

    public static void main(String[] args) {
        SpringApplication.run(PersistentListApplication.class, args);
    }
}
