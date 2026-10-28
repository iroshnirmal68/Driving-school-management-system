package com.drivesafe.pro;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DataService {
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;
    private static final List<String> TABLES = List.of("students", "instructors", "vehicles", "lessons", "payments");

    public DataService(JdbcTemplate jdbc, ObjectMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
        jdbc.execute("CREATE TABLE IF NOT EXISTS app_data (data_key VARCHAR(40) PRIMARY KEY, json_value CLOB NOT NULL)");
        seedIfEmpty();
    }

    private void seedIfEmpty() {
        if (jdbc.queryForObject("SELECT COUNT(*) FROM app_data", Integer.class) != 0) return;
        Map<String,Object> initial = new LinkedHashMap<>();
        initial.put("students", List.of(
            Map.of("id",101,"name","Nimlesh Perera","email","nimlesh@example.com","phone","+94771234567","package","Premium Auto Course","status","Active"),
            Map.of("id",102,"name","Fathima Rizna","email","rizna@example.com","phone","+94719876543","package","Regular Manual Course","status","Active")));
        initial.put("instructors", List.of(
            Map.of("id",201,"name","Captain Ranjith Silva","license","L-DMV-90142","phone","+94752221111","specialization","Defensive Driving Pro","availability","Available"),
            Map.of("id",202,"name","Mahinda Alwis","license","L-DMV-33152","phone","+94724449999","specialization","Light Motor Cars","availability","Available")));
        initial.put("vehicles", List.of(
            Map.of("id",301,"model","Toyota Vitz","plate","WP CAD-2291","transmission","Automatic","status","Active","serviceDate","2026-04-12"),
            Map.of("id",302,"model","Suzuki Alto","plate","WP CAA-8831","transmission","Manual","status","Active","serviceDate","2026-05-01")));
        initial.put("lessons", List.of(Map.of("id",401,"student","Nimlesh Perera","instructor","Captain Ranjith Silva","vehicle","Toyota Vitz (WP CAD-2291)","dateTime","2026-08-12T10:00","status","Scheduled")));
        initial.put("payments", List.of(
            Map.of("id",501,"student","Nimlesh Perera","amount","45000","method","POS Terminals Card","date","2026-06-15"),
            Map.of("id",502,"student","Fathima Rizna","amount","35000","method","Cash Transfer","date","2026-06-18")));
        save(initial);
    }

    public synchronized Map<String,Object> getAll() {
        Map<String,Object> result = new LinkedHashMap<>();
        for (String table : TABLES) {
            String json = jdbc.queryForObject("SELECT json_value FROM app_data WHERE data_key=?", String.class, table);
            try { result.put(table, mapper.readValue(json, new TypeReference<List<Map<String,Object>>>(){})); }
            catch (Exception e) { throw new IllegalStateException("Stored data could not be read for " + table, e); }
        }
        return result;
    }

    public synchronized void save(Map<String,Object> data) {
        for (String table : TABLES) {
            Object value = data.getOrDefault(table, List.of());
            if (!(value instanceof List<?>)) throw new IllegalArgumentException("Expected an array for " + table);
            try {
                String json = mapper.writeValueAsString(value);
                jdbc.update("MERGE INTO app_data (data_key, json_value) KEY(data_key) VALUES (?, ?)", table, json);
            } catch (Exception e) { throw new IllegalArgumentException("Could not save " + table, e); }
        }
    }
}
