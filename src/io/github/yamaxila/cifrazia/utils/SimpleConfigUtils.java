package io.github.yamaxila.cifrazia.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleConfigUtils {


    private static final Map<String, String> CONFIG = new HashMap<>();
    private static final File configFile = new File("./config.cifr");

    static {
        if(!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void loadConfig() throws IOException {
        List<String> configLines = Files.readAllLines(Paths.get(configFile.toURI()));

        configLines.stream()
                .filter(p -> p.contains("<:>"))
                .forEach((line -> CONFIG.put(line.split("<:>")[0], line.split("<:>")[1])));
    }

    public static String getValue(String key) {
        return CONFIG.get(key);
    }
    public static String getValue(String key, String defValue) {
        return CONFIG.getOrDefault(key, defValue);
    }

    public static String setValue(String key, String value) {
        if (CONFIG.containsKey(key)) {
            CONFIG.replace(key, value);
        } else {
            CONFIG.put(key, value);
        }
        return value;
    }

    public static void save() throws IOException {
        StringBuilder configOut = new StringBuilder();
        CONFIG.forEach((key, value) -> {
            configOut.append(key);
            configOut.append("<:>");
            configOut.append(value);
            configOut.append("\n");
        });
        Files.write(Paths.get(configFile.toURI()), configOut.toString().getBytes(StandardCharsets.UTF_8));
    }

}
