package org.allsparks.contracts.arch;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class PublicApiSurfaceTest {

    private static final Set<String> ALLOWED = new HashSet<>(Arrays.asList(
            "org.allsparks.contracts.time.MonotonicClock",
            "org.allsparks.contracts.time.SystemMonotonicClock",
            "org.allsparks.contracts.time.FakeMonotonicClock",
            "org.allsparks.contracts.identity.ComponentId",
            "org.allsparks.contracts.identity.CapabilityId",
            "org.allsparks.contracts.observation.Validity",
            "org.allsparks.contracts.observation.Confidence",
            "org.allsparks.contracts.status.Availability",
            "org.allsparks.contracts.status.Readiness",
            "org.allsparks.contracts.status.Readiness.State",
            "org.allsparks.contracts.status.Reason",
            "org.allsparks.contracts.health.HealthSeverity",
            "org.allsparks.contracts.health.HealthFinding",
            "org.allsparks.contracts.input.SignalValueType",
            "org.allsparks.contracts.input.SignalKey",
            "org.allsparks.contracts.input.SamplingPolicy",
            "org.allsparks.contracts.input.InputPriority",
            "org.allsparks.contracts.input.Sample",
            "org.allsparks.contracts.input.InputRequirement",
            "org.allsparks.contracts.input.CoherentGroup",
            "org.allsparks.contracts.input.InputRequirements",
            "org.allsparks.contracts.input.InputValues",
            "org.allsparks.contracts.input.InputRegistrar",
            "org.allsparks.contracts.input.InputDemand",
            "org.allsparks.contracts.input.MotorSignals"));

    private static final Pattern TYPE = Pattern.compile("public\\s+(?:final\\s+)?(?:class|interface|enum)\\s+(\\w+)");

    @Test
    void publicProductionTypesMatchAllowlist() throws IOException {
        List<String> found = new ArrayList<>();
        Path main = SourceScan.mainJava();
        for (Path path : SourceScan.javaFiles(main)) {
            String pkg = packageName(SourceScan.read(path));
            String[] lines = SourceScan.read(path).split("\n");
            String outer = null;
            for (String line : lines) {
                Matcher matcher = TYPE.matcher(line);
                if (!matcher.find()) {
                    continue;
                }
                String name = matcher.group(1);
                if (line.contains("enum") && outer != null && line.contains(outer) == false) {
                    // nested enum inside a public type
                    if (line.trim().startsWith("public ")) {
                        found.add(pkg + "." + outer + "." + name);
                        continue;
                    }
                }
                if (line.contains("static") && outer != null) {
                    found.add(pkg + "." + outer + "." + name);
                    continue;
                }
                outer = name;
                found.add(pkg + "." + name);
            }
        }
        List<String> extra = new ArrayList<>();
        for (String type : found) {
            if (!ALLOWED.contains(type)) {
                extra.add(type);
            }
        }
        List<String> missing = new ArrayList<>();
        for (String type : ALLOWED) {
            if (!found.contains(type)) {
                missing.add(type);
            }
        }
        if (!extra.isEmpty() || !missing.isEmpty()) {
            fail("public API drift. extra=" + extra + " missing=" + missing + " found=" + found);
        }
    }

    private static String packageName(String source) {
        for (String line : source.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("package ")) {
                String pkg = trimmed.substring("package ".length()).trim();
                if (pkg.endsWith(";")) {
                    pkg = pkg.substring(0, pkg.length() - 1);
                }
                return pkg;
            }
        }
        return "";
    }
}
