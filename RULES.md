# RULES

Constraints for all work in this repository. These override any default
behaviour. If a step file conflicts with a rule here, stop and ask.

## Scope

- This is an individual university assignment. Write only what a step file
  asks for.
- Do not add features, classes, tests, logging, or configuration that no
  step file requested.
- Do not refactor existing code unless a step file says to.

## Java

- Source and target: [SET AFTER SPIKE — likely `--release 11` or default 26]
- [IF SPIKE REQUIRES] No lambdas. No method references. No streams.
  Use anonymous inner classes and explicit loops.
- No external libraries. No Maven, Gradle, or any dependency manager.
  Parsing is hand-written.
- Compile with plain `javac`.
- Packages: `com.dpat.simple` for the simple solution,
  `com.dpat.patterns` for the pattern-based solution.

## The two solutions

- Both implement identical functionality. Same inputs, same outputs,
  same validation rules, same error messages.
- Neither may gain a capability the other lacks.
- `LegacyFlatFileReader` is byte-identical in both. It represents an
  existing third-party component. Never modify it, in either solution.

## Writing the simple solution

This is the critical rule.

The simple solution is a realistic design written by a competent developer
under time pressure. It is NOT a deliberately bad design.

- Do write: separate importer classes, sensible naming, working error
  handling, readable methods.
- Do not write: a single god class, dead code, obviously duplicated
  blocks beyond what the design implies, or anything that looks
  engineered to fail.

Its weaknesses are structural and specific: the client selects importers
through a conditional chain naming concrete types, the field-to-Product
mapping is repeated in each importer, and legacy conversion sits inside
the client. Nothing else about it should be weak.

A rigged comparison is visible to a marker and invalidates the empirical
evaluation this project exists to produce.

## Style

- Javadoc on every class, one or two lines.
- No inline comments explaining obvious code.
- Fields private, accessors only where used.
- No `var`.
- 4-space indent.

## Git

- One commit per step file.
- Message format: `step NN: short description`
- Never commit `build/`, `out/`, or `.idea/`.