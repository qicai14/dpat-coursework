# CONTEXT

## What this is

Coursework for a Design Patterns module. The deliverable compares two
implementations of the same program: one written without design patterns,
one refined using Factory Method and Adapter. Both are then measured to
test whether the pattern-based version scores higher on reusability.

The quality attribute under examination is REUSABILITY, defined as the
ease of reusing software in a different context.

## The application

A product catalogue import tool. It reads supplier product files, converts
records into Product objects, validates them, and reports how many
succeeded and failed.

Four formats: CSV, JSON, XML, and fixed-width text. The fixed-width format
is read by LegacyFlatFileReader, an existing component that returns
String[][] and cannot be modified.

## The two designs

Simple solution, 9 classes. A client selects the importer by file
extension using a conditional chain, calls it directly, and converts
legacy output inline.

Pattern solution, 16 classes. Factory Method: an abstract ImportJob
declares `protected abstract ProductParser createParser()`, and four
subclasses each decide which parser to instantiate. Adapter:
LegacyFlatFileAdapter implements ProductParser and wraps
LegacyFlatFileReader.

## Why it matters that both work identically

The comparison is only valid if the two programs solve the same problem.
Any functional difference between them invalidates every number in the
evaluation.

## What this repository is not

Not production software. Parsing is deliberately simplified. The point is
the structural difference between the two designs, measured with CK
metrics and the QMOOD reusability index.

## Toolchain constraint

Metrics are collected with ckjm 1.9, which uses Apache BCEL 5.2 to read
compiled bytecode. BCEL 5.2 predates the invokedynamic constant pool entry
introduced in Java 7 and fails on any class file containing it.

The project therefore targets Java 8 and compiles with Zulu 8. Java 8
compiles string concatenation to StringBuilder rather than invokedynamic,
which JDK 9 and later do not. Lambdas and method references produce
invokedynamic under every Java version and are banned outright.

This constraint is documented in the report's measurement guideline. It is
a property of the instrument, not of the design being measured, and it
applies identically to both solutions so the comparison remains valid.