package org.allsparks.contracts.arch;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class PackageBoundaryTest {

    private static final List<String> FORBIDDEN_PREFIXES = Arrays.asList(
            "com.qualcomm",
            "org.firstinspires.ftc",
            "android.",
            "androidx.",
            "org.allsparks.amper",
            "org.allsparks.vidar",
            "org.allsparks.mimic",
            "org.allsparks.trace",
            "org.allsparks.beacon",
            "org.allsparks.helm",
            "org.allsparks.echo");

    @Test
    void productionSourcesDoNotImportForbiddenPackages() throws IOException {
        List<String> hits = new ArrayList<>();
        Path main = SourceScan.mainJava();
        for (Path path : SourceScan.javaFiles(main)) {
            String[] lines = SourceScan.read(path).split("\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();
                if (!line.startsWith("import ")) {
                    continue;
                }
                String imported = stripImport(line);
                for (String prefix : FORBIDDEN_PREFIXES) {
                    if (imported.equals(prefix) || imported.startsWith(prefix + ".")) {
                        hits.add(SourceScan.rel(main, path) + ":" + (i + 1) + " " + imported);
                    }
                }
            }
        }
        failIf(hits, "production source imported FTC, Android, or an Allsparks functional project");
    }

    private static String stripImport(String line) {
        String imported = line.substring("import ".length()).trim();
        if (imported.endsWith(";")) {
            imported = imported.substring(0, imported.length() - 1).trim();
        }
        if (imported.startsWith("static ")) {
            imported = imported.substring("static ".length()).trim();
        }
        return imported;
    }

    private static void failIf(List<String> hits, String message) {
        if (!hits.isEmpty()) {
            fail(message + ":\n" + String.join("\n", hits));
        }
    }
}
