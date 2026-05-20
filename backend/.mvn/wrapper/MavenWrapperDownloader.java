import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class MavenWrapperDownloader {

    private static final String DEFAULT_WRAPPER_URL =
            "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar";

    public static void main(String[] args) throws Exception {
        File projectBaseDir = args.length > 0 ? new File(args[0]) : new File(System.getProperty("user.dir"));
        File wrapperPropertiesFile = new File(projectBaseDir, ".mvn/wrapper/maven-wrapper.properties");
        File wrapperJarFile = new File(projectBaseDir, ".mvn/wrapper/maven-wrapper.jar");

        if (wrapperJarFile.exists()) {
            System.out.println("Found existing Maven wrapper jar.");
            return;
        }

        String wrapperUrl = loadWrapperUrl(wrapperPropertiesFile);
        downloadFileFromURL(wrapperUrl, wrapperJarFile);
        System.out.println("Maven wrapper jar downloaded to " + wrapperJarFile.getAbsolutePath());
    }

    private static String loadWrapperUrl(File wrapperPropertiesFile) throws IOException {
        if (!wrapperPropertiesFile.exists()) {
            return DEFAULT_WRAPPER_URL;
        }

        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(wrapperPropertiesFile)) {
            properties.load(inputStream);
        }
        return properties.getProperty("wrapperUrl", DEFAULT_WRAPPER_URL);
    }

    private static void downloadFileFromURL(String urlString, File destination) throws IOException {
        File parent = destination.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Could not create directory " + parent.getAbsolutePath());
        }

        URL website = new URL(urlString);
        try (InputStream inputStream = website.openStream();
             FileOutputStream outputStream = new FileOutputStream(destination)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}
