package GUI;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reads version information embedded by Maven, or Git for source-tree runs. */
final class BuildVersion {
    private static final String INITIAL_VERSION = "1.6.0";

    private BuildVersion() {
    }

    static String aboutLabel() {
        // IDE builds can overwrite the Maven-filtered resource with a placeholder
        // version, so use Git for classes launched directly from the source tree.
        URL classResource = BuildVersion.class.getResource("BuildVersion.class");
        if (classResource != null && "file".equals(classResource.getProtocol())) {
            String sourceVersion = fromGit();
            if (sourceVersion != null) {
                return sourceVersion;
            }
        }

        Properties properties = new Properties();
        try (InputStream input = BuildVersion.class.getResourceAsStream("/build-version.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException | SecurityException ignored) {
            // A source-tree build can still read its version from Git below.
        }

        String version = properties.getProperty("version");
        String commit = properties.getProperty("commit");
        if (version != null && !version.contains("${") && !version.equals("0.0.0-SNAPSHOT")
                && !version.equals("0.0.0")
                && commit != null && !commit.equals("unknown") && !commit.contains("${")) {
            return format(version, commit);
        }

        String sourceVersion = fromGit();
        return sourceVersion != null ? sourceVersion
                : "Revision: " + INITIAL_VERSION + " (SHA unavailable)";
    }

    private static String fromGit() {
        String commit = git("rev-parse", "--short=7", "HEAD");
        if (commit == null) {
            return null;
        }

        String description = git("describe", "--tags", "--long", "--first-parent", "--match", "v[0-9]*");
        if (description != null) {
            Matcher match = Pattern.compile("(v[0-9]+\\.[0-9]+\\.[0-9]+)-([0-9]+)-g[0-9a-f]+")
                    .matcher(description);
            if (match.matches()) {
                String version = match.group(1).substring(1);
                String distance = match.group(2);
                return format(version + (distance.equals("0") ? "" : "." + distance), commit);
            }
        }

        return format(INITIAL_VERSION + ".0", commit);
    }

    private static String format(String version, String commit) {
        String displayVersion = version.endsWith("-SNAPSHOT")
                ? version.substring(0, version.length() - "-SNAPSHOT".length()) : version;
        return "Revision: " + displayVersion + " (" + commit + ")";
    }

    private static String git(String... arguments) {
        String[] command = new String[arguments.length + 1];
        command[0] = "git";
        System.arraycopy(arguments, 0, command, 1, arguments.length);
        try {
            Process process = new ProcessBuilder(command).start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            return process.waitFor() == 0 && !output.isEmpty() ? output : null;
        } catch (IOException | SecurityException ignored) {
            return null;
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
