package mop.app.client.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class YamlConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(YamlConfigLoader.class);

    public static Map<String, Object> loadConfig(String fileName) throws URISyntaxException {
        String configFilePath = getJarDirectory() + File.separator + fileName;
        Yaml yaml = new Yaml();

        // Load configuration file from current directory
        if (new File(fileName).exists()) {
            try (InputStream inputStream = new FileInputStream(fileName)) {
                return yaml.load(inputStream);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load configuration file: " + fileName);
            }
        }

        // Load configuration file from resources
        if (!new File(configFilePath).exists()) {
            InputStream inputStream = YamlConfigLoader.class.getClassLoader().getResourceAsStream(fileName);
            if (inputStream == null) {
                throw new RuntimeException("Configuration file not found: " + fileName);
            }
            return yaml.load(inputStream);
        }

        // Load configuration file from JAR directory (target/classes)
        try (InputStream inputStream = new FileInputStream(configFilePath)) {
            return yaml.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file: " + fileName);
        }
    }

    private static String getJarDirectory() throws URISyntaxException {
        File jarFile = new File(YamlConfigLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        return jarFile.getParent();
    }
}
