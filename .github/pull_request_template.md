## Summary

Briefly describe what this PR changes and why.

## Contract impact

- [ ] Documentation / research only
- [ ] Bug fix with no API change
- [ ] Additive public API
- [ ] Semantic change (requires migration notes)
- [ ] Responsibility widening (requires a new ADR)

## Safety

- [ ] Does **not** command hardware
- [ ] Does **not** add FTC, Android, or Allsparks functional-library dependencies
- [ ] Does **not** add threads, schedulers, or a service registry
- [ ] `UNKNOWN` is never treated as ready or available
- [ ] No secrets or student PII included

## Validation evidence

- [ ] `./gradlew check` (or `gradlew.bat check`) passed locally
- [ ] Unit tests added/updated
- [ ] Hardware validation status: none

Record commands and results here:

```text
```

## Checklist

- [ ] Docs updated if behavior or the public allowlist changed
- [ ] Linked related issues
- [ ] `Closes #<issue>` used only if this PR fully resolves that issue
