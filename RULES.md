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

## Java and toolchain

- Java 8. Compile and run with Zulu 8 at
  /Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home
- Do NOT use JDK 26 to compile anything in this repository.

### Banned constructs

These compile to invokedynamic, which the measurement tool cannot parse.
Using any of them breaks the entire metrics run, not just one class.

- No lambda expressions
- No method references
- No streams API
- No var, records, text blocks, or switch expressions (not in Java 8 anyway)

Use anonymous inner classes and explicit for loops instead.

String concatenation with + is SAFE under Zulu 8 because Java 8 compiles it
to StringBuilder. It is NOT safe under JDK 26, which compiles it to
invokedynamic. This is why the compiler is pinned.

### Build

- No Maven. No Gradle. No dependency manager.
- No external libraries. All parsing is hand-written.
- Plain javac only.

### Commands

    ZULU8=/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home

    $ZULU8/bin/javac -d build/simple $(find simple-solution/src -name "*.java")
    $ZULU8/bin/javac -d build/patterns $(find pattern-solution/src -name "*.java")

    $ZULU8/bin/java -cp build/simple com.dpat.simple.Main data/products.csv
    $ZULU8/bin/java -cp build/patterns com.dpat.patterns.Main data/products.csv

### Verify before committing

After adding or changing classes, confirm the metrics tool still parses them:

    java -jar tools/ckjm-1.9.jar $(find build/simple -name "*.class")

A ClassFormatException means a banned construct was used somewhere. Find
it and remove it before continuing.

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