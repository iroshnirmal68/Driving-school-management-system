package com.drivesafe.pro;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final DataService dataService;
    public ApiController(DataService dataService) { this.dataService = dataService; }

    @GetMapping("/health")
    public Map<String,String> health() { return Map.of("status","UP","application","DriveSafe Pro"); }

    @GetMapping("/data")
    public Map<String,Object> getData() { return dataService.getAll(); }

    @PutMapping("/data")
    public Map<String,Object> saveData(@RequestBody Map<String,Object> data) {
        try { dataService.save(data); return Map.of("message","Data saved","status","success"); }
        catch (IllegalArgumentException e) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage()); }
    }

    @PostMapping("/auth/login")
    public Map<String,Object> login(@RequestBody LoginRequest request) {
        // Development/demo credentials only. Replace with hashed passwords and Spring Security before deployment.
        boolean valid = "admin".equals(request.username()) && "password".equals(request.password());
        if (!valid) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid demo credentials. Use admin / password.");
        if (!java.util.Set.of("admin","instructor","student").contains(request.role()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported role.");
        return Map.of("authenticated", true, "username", request.username(), "role", request.role());
    }

    public record LoginRequest(String username, String password, String role) {}
}
