package com.bajaj.challenge.followerfinder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.*;
@Component
public class StartupRunner implements CommandLineRunner {
    private final RestTemplate restTemplate;
    public StartupRunner(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Override
    public void run(String... args) throws Exception {
        String registerUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "Sana Lokesh Reddy");
        requestBody.put("regNo", "REG172");
        requestBody.put("email", "sana@gmail.com");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(registerUrl, request, Map.class);
        System.out.println("Full API Response: " + response.getBody());
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            System.out.println("Registration failed with status code: " + response.getStatusCode());
            return;
        }
        Map body = response.getBody();
        if (body == null || !body.containsKey("webhook") || !body.containsKey("accessToken") || !body.containsKey("data")) {
            throw new RuntimeException("Invalid response body: " + body);
        }
        String webhook = (String) body.get("webhook");
        String accessToken = (String) body.get("accessToken");
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        if (data == null || !data.containsKey("users")) {
            throw new RuntimeException("Invalid or missing 'users' in 'data' object: " + data);
        }
        Integer nVal = data.containsKey("n") ? (Integer) data.get("n") : 2;
        Integer findIdVal = data.containsKey("findId") ? (Integer) data.get("findId") : 1;
        System.out.println("Using n = " + nVal + ", findId = " + findIdVal);
        List<Map<String, Object>> users = (List<Map<String, Object>>) data.get("users");
        Set<Integer> result = getNthLevelFollowers(users, findIdVal, nVal);
        Map<String, Object> output = new HashMap<>();
        output.put("followers", new ArrayList<>(result));
        Map<String, Object> outcome = new HashMap<>();
        outcome.put("regNo", "REG172");
        outcome.put("output", output);
        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.setContentType(MediaType.APPLICATION_JSON);
        postHeaders.setBearerAuth(accessToken);
        HttpEntity<Map<String, Object>> postRequest = new HttpEntity<>(outcome, postHeaders);
        System.out.println("Sending webhook to: " + webhook);
        System.out.println("Payload: " + outcome);
        System.out.println("AccessToken (Bearer token attached)");
        int attempts = 0;
        while (attempts < 4) {
            try {
                restTemplate.postForEntity(webhook, postRequest, String.class);
                System.out.println(" Webhook sent successfully");
                break;
            } catch (Exception e) {
                attempts++;
                System.out.println("Attempt " + attempts + " failed. Retrying after backoff...");
                e.printStackTrace();
                Thread.sleep((long) Math.pow(2, attempts) * 1000);
            }
        }
    }
    private Set<Integer> getNthLevelFollowers(List<Map<String, Object>> users, int startId, int level) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (Map<String, Object> user : users) {
            int id = (int) user.get("id");
            List<Integer> follows = (List<Integer>) user.get("follows");
            graph.put(id, follows);
        }
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(startId);
        visited.add(startId);
        int currentLevel = 0;
        while (!queue.isEmpty() && currentLevel < level) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int node = queue.poll();
                List<Integer> neighbours = graph.getOrDefault(node, new ArrayList<>());
                for (int nei : neighbours) {
                    if (!visited.contains(nei)) {
                        queue.add(nei);
                        visited.add(nei);
                    }
                }
            }
            currentLevel++;
        }
        return new HashSet<>(queue);
    }
    @Configuration
    public static class RestTemplateConfig {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
}
