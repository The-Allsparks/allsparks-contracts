# API baseline

`allsparks-contracts.jar` is the last accepted public API. `./gradlew japicmp` (part of `check`) compares the current `jar` output to this file.

Do not edit the JAR by hand. After an intentional, documented API change:

```text
./gradlew updateApiBaseline
```

Commit the new JAR with the migration note in `CHANGELOG.md` and `docs/compatibility.md`. See those docs for why adding an enum constant is a compatibility event.
