package org.allsparks.contracts.arch;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProductionSafetyTest {

    @Test
    void productionSourcesDoNotStartThreads() throws IOException {
        List<String> needles = Arrays.asList(
                "new Thread",
                "ExecutorService",
                "Executors.",
                "new Timer",
                "CompletableFuture",
                "ForkJoinPool",
                "ScheduledExecutor");
        List<String> hits = new ArrayList<>();
        Path main = SourceScan.mainJava();
        for (Path path : SourceScan.javaFiles(main)) {
            String[] lines = SourceScan.read(path).split("\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (line.trim().startsWith("*") || line.trim().startsWith("//")) {
                    continue;
                }
                for (String needle : needles) {
                    if (line.contains(needle)) {
                        hits.add(SourceScan.rel(main, path) + ":" + (i + 1) + " " + needle);
                    }
                }
            }
        }
        if (!hits.isEmpty()) {
            fail("production code must not start threads:\n" + String.join("\n", hits));
        }
    }

    @Test
    void productionSourcesHaveNoLifecycleInterfaces() throws IOException {
        List<String> hits = new ArrayList<>();
        Path main = SourceScan.mainJava();
        for (Path path : SourceScan.javaFiles(main)) {
            String source = SourceScan.read(path);
            if (!source.contains("interface ")) {
                continue;
            }
            String[] lines = source.split("\n");
            boolean inInterface = false;
            for (int i = 0; i < lines.length; i++) {
                String trimmed = lines[i].trim();
                if (trimmed.contains("interface ")) {
                    inInterface = true;
                }
                if (inInterface
                        && (trimmed.startsWith("void start(")
                                || trimmed.startsWith("void stop(")
                                || trimmed.startsWith("void close(")
                                || trimmed.startsWith("void periodic(")
                                || trimmed.startsWith("void attach(")
                                || trimmed.startsWith("void detach("))) {
                    hits.add(SourceScan.rel(main, path) + ":" + (i + 1) + " " + trimmed);
                }
            }
        }
        if (!hits.isEmpty()) {
            fail("lifecycle interfaces are deferred:\n" + String.join("\n", hits));
        }
    }

    @Test
    void publicContractsContainNoSeasonOrDeviceTerms() throws IOException {
        List<String> terms = Arrays.asList(
                "DECODE",
                "BioBuzz",
                "INTO THE DEEP",
                "INTO_THE_DEEP",
                "BumbleBee",
                "AprilTag",
                "DcMotor",
                "OpMode",
                "Control Hub",
                "ControlHub");
        List<String> hits = new ArrayList<>();
        Path main = SourceScan.mainJava();
        for (Path path : SourceScan.javaFiles(main)) {
            String source = SourceScan.read(path);
            for (String term : terms) {
                if (source.contains(term)) {
                    hits.add(SourceScan.rel(main, path) + " " + term);
                }
            }
        }
        if (!hits.isEmpty()) {
            fail("season or device terms in production sources:\n" + String.join("\n", hits));
        }
    }

    @Test
    void buildGradleHasNoProductionDependencies() {
        String gradle = SourceScan.read(SourceScan.buildGradle());
        List<String> hits = new ArrayList<>();
        String[] lines = gradle.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String trimmed = lines[i].trim();
            if (trimmed.startsWith("implementation ") || trimmed.startsWith("api ")) {
                hits.add((i + 1) + ": " + trimmed);
            }
        }
        if (!hits.isEmpty()) {
            fail("production dependencies are not allowed in build.gradle:\n" + String.join("\n", hits));
        }
    }
}
