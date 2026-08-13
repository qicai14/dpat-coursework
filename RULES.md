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

Java 8 only. Compile and run with Zulu 8:

    /Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home

Never compile this project with JDK 26.

### Why

Metrics are collected with ckjm 1.9 (build "1.9 April 4 2012"), which
bundles Apache BCEL 5.2. BCEL 5.2 predates the invokedynamic constant pool
entry and throws ClassFormatException on any class file containing it.

Verified on this machine:
- Plain Java 8 code parses correctly.
- String concatenation with + parses correctly under Zulu 8, because
  Java 8 compiles it to StringBuilder.
- Lambda expressions fail with "Invalid byte tag in constant pool: 18"
  under every compiler, because Java 8 implements lambdas with
  invokedynamic.
- JDK 9 and later compile string concatenation to invokedynamic (JEP 280),
  which is why the compiler is pinned rather than the language level.

### Banned constructs

Any of these breaks the entire metrics run, not just one class.

- No lambda expressions
- No method references
- No streams API

Use anonymous inner classes and explicit for loops.

String concatenation with + is allowed and safe under Zulu 8.

### Build

- No Maven, Gradle, or any dependency manager.
- No external libraries. All parsing is hand-written.
- Plain javac only.

### Commands

Run from the repository root. Absolute paths only, never PATH or JAVA_HOME.

    ZULU8=/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home
    CKJM=tools/ckjm-1.9/build/ckjm-1.9.jar

    # Zulu 8 javac does NOT create the -d directory. Always mkdir first.
    mkdir -p build/simple build/patterns

    # compile
    $ZULU8/bin/javac -d build/simple $(find simple-solution/src -name "*.java")
    $ZULU8/bin/javac -d build/patterns $(find pattern-solution/src -name "*.java")

    # run
    $ZULU8/bin/java -cp build/simple com.dpat.simple.Main data/products.csv
    $ZULU8/bin/java -cp build/patterns com.dpat.patterns.Main data/products.csv

    # metrics
    $ZULU8/bin/java -jar $CKJM $(find build/simple -name "*.class")
    $ZULU8/bin/java -jar $CKJM $(find build/patterns -name "*.class")

### Reading ckjm errors

    ClassFormatException: Invalid byte tag in constant pool: 18
        A banned construct was used somewhere. Find it and remove it.

    NullPointerException at ClassParser.parse
        The .class file does not exist. Compilation probably failed,
        or the path is wrong.

### Verify after every batch of classes

    mkdir -p build/simple
    $ZULU8/bin/javac -d build/simple $(find simple-solution/src -name "*.java")
    $ZULU8/bin/java -jar $CKJM $(find build/simple -name "*.class")

ckjm aborts on the first unparseable class rather than skipping it, so a
single banned construct anywhere prevents all measurement. Check early.

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