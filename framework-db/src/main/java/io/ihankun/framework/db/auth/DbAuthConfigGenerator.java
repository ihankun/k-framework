package io.ihankun.framework.db.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author hankun
 */
@Slf4j
public class DbAuthConfigGenerator {

    public static Map<String, String> loadFromFile(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            for (String line : lines) {
                line = line.replaceAll("[^\\x0A\\x0D\\x20-\\x7E]", "");
                String[] splits = line.split(",");
                String db = splits[1].trim() + "." + splits[2].trim();
                map.add(splits[0].trim(), db);
            }
            Map<String, String> result = new HashMap<>(map.size());
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                Set<String> dbs = new HashSet<>(entry.getValue());
                result.put(entry.getKey(), String.join(",", dbs));
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
