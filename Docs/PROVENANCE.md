# Provenance

This repository was prepared on **2026-08-22** as a clean-history public archive. It does not inherit Git objects, branches, remotes, credentials, IDE state, build output, or machine configuration from the original team repository.

## Source snapshots

| Imported component | Source | Revision or state | Treatment |
| --- | --- | --- | --- |
| Android and Windows app | `Lantr-Team/Lantr-Android-Windows` | `cb4d7bb5f179637108769af33484ec4b95046d7e` plus the reviewed 2026-08-22 working tree | Imported without `.git`, local properties, IDE state, agent settings, generated output, or accidental root files. Missing document/image implementations from the working tree were included so the snapshot builds independently. |
| Director service experiment | `Boanca'sBranch` | `f1711e929d7da906a64b66dd4e9a50a82b7e773d` | The standalone Go prototype was extracted rather than merging the outdated application branch. Live Azure identifiers were removed and the service was reorganized around configuration, typed boundaries, limits, and offline tests. |
| Fluent design study | Working-tree document | 2026-03-30 snapshot | Preserved as an experiment note and edited to distinguish investigation from shipped behavior. |

## Clean-history rationale

The original repository contains rough development commits and machine-specific configuration that do not form a useful public product narrative. A clean import creates a reviewable boundary: every file in the first public revision was deliberately selected, scanned, built, and documented.

The original team repository was already publicly readable before this archive existed. The clean-history boundary improves provenance and presentation; it does not erase copies of the prior public history.

## Authorship

Matei Ursache authored the Kotlin/Compose application represented here. Raul Boanca authored the initial Director service prototype and participated in the wider Lantr effort. David Retegan built the separate Apple-platform application and contributed to the shared product direction.

This archive preserves component-level credit even though its public Git history begins with the curated import.

## Deliberate omissions

- local Android SDK paths and IDE metadata;
- personal agent/tool configuration;
- build caches and generated distributions;
- copied JetBrains implementation stubs found outside a source set;
- accidental `nul` and temporary files;
- live cloud project identifiers;
- obsolete branch copies of the application that added no unique experiment.
