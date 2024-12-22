package mop.app.client.dao.user;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlReader {
    private static final Logger logger = LoggerFactory.getLogger(SqlReader.class);
    private final String content;

    public SqlReader(String resourcePath) {
        String fileContent = readFromClasspath(resourcePath);

        if (fileContent == null) {
            String packagePath = getClass().getPackage().getName().replace('.', '/');
            fileContent = readFromClasspath(packagePath + "/" + resourcePath);
        }

        if (fileContent == null) {
            fileContent = readFromFileSystem(resourcePath);
        }

        if (fileContent == null) {
            throw new RuntimeException("Could not find SQL file: " + resourcePath);
        }

        this.content = fileContent;
    }

    private String readFromClasspath(String path) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
            if (inputStream != null) {
                return new String(inputStream.readAllBytes());
            }
        } catch (IOException e) {
            logger.error("Failed to read file from classpath: " + path, e);
        }
        return null;
    }

    private String readFromFileSystem(String path) {
        try {
            String classLocation = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
            Path basePath = Paths.get(classLocation).getParent();
            File file = basePath.resolve(path).toFile();

            if (!file.exists()) {
                file = new File(path);
            }

            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    return new String(fis.readAllBytes());
                }
            }
        } catch (IOException e) {
            logger.error("Failed to read file from file system: " + path, e);
        }
        return null;
    }

    public String read() {
        return content;
    }
}