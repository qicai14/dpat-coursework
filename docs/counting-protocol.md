# Measurement Counting Protocol

Rules for computing QMOOD and CK metrics on the two solutions. Fixed before any measurement was taken, so that counting decisions could not be influenced by the results they produce.

Instruments: QMOOD (Bansiya & Davis, 2002) as the primary reusability measure, with the Chidamber and Kemerer (1994) suite reported alongside as supporting structural detail.

---

## 1. Equation

```
Reusability = −0.25 × DCC + 0.25 × CAM + 0.50 × CIS + 0.50 × DSC
```

| Property | Metric | Definition as printed in the source |
|---|---|---|
| Coupling | DCC | "a count of the different number of classes that a class is directly related to. The metric includes classes that are directly related by attribute declarations and message passing (parameters) in methods" |
| Cohesion | CAM | "computed using the summation of the intersection of parameters of a method with the maximum independent set of all parameter types in the class. A metric value close to 1.0 is preferred. (Range 0 to 1)" |
| Messaging | CIS | "a count of the number of public methods in a class" |
| Design Size | DSC | "a count of the total number of classes in the design" |

DCC, CAM and CIS are computed per class and averaged. DSC is a system-level count.

---

## 2. DCC

### The ambiguity

The definition names two sources of coupling, attribute declarations and "message passing (parameters) in methods". The parenthetical admits two readings.

A narrow reading counts only field declarations and method parameter types. A broad reading treats "message passing" as one class invoking another, with parameters given as an example, which also captures return types, instantiated types and thrown exceptions.

The choice is material. Under the narrow reading, `ImportManager` in the simple solution has DCC 1, because its only user-defined parameter type is `Product` and the four importers it depends on appear as local variables. Its measured CBO is 7. The narrow reading would report the most coupled class in the design as almost uncoupled.

### Rule adopted

DCC for class C is the count of distinct user-defined classes, excluding C itself, that C directly references through any of:

1. Field declarations, including generic type arguments
2. Method parameter types, including generic type arguments
3. Method return types, including generic type arguments
4. Types instantiated with `new`
5. Declared thrown exception types
6. Superclass and implemented interfaces
7. Local variable declarations

Excluded: JDK and standard library types, the class itself, and repeated references to a class already counted.

JDK types are excluded so that DCC and CBO remain comparable, since ckjm excludes `java.*` from CBO by default.

The rule is applied identically to both solutions.

---

## 3. CIS

Count of public methods per class, including public constructors, excluding fields.

Values are taken from ckjm's NPM column rather than counted by hand, which removes one source of transcription error. The equivalence is checkable: `Product` declares four getters and one constructor and ckjm reports NPM 5.

Interface methods are public by default and count. Protected and private methods do not.

---

## 4. CAM

For class C, with `AllTypes(C)` the union of distinct parameter types across all declared methods of C:

```
CAM(C) = ( Σ over methods m of |params(m) ∩ AllTypes(C)| ) ÷ ( |AllTypes(C)| × methods(C) )
```

Conventions:

1. Constructors count as methods.
2. JDK types are included. CAM measures internal consistency of signatures, not design coupling, so the DCC exclusion does not apply.
3. A method with no parameters contributes 0 to the numerator and still counts in `methods(C)`.
4. Generic type arguments are not unwrapped. `List<Map<String,String>>` is one type.
5. Only explicitly declared methods are counted. Implicit default constructors do not appear in source and cannot be counted from it.
6. **Where `AllTypes(C)` is empty the metric is undefined.** Such classes are excluded from the average and the number excluded is reported.

Rule 6 replaces an earlier draft convention that assigned 0. Assigning 0 treats a class whose cohesion cannot be measured as one whose cohesion is worst possible, which is a different claim. Excluding undefined values is standard practice.

Both conventions are reported where they differ, so the effect of the choice is visible.

---

## 5. DSC

Count of classes in the design, including interfaces and abstract classes.

---

## 6. Tooling

| Item | Value |
|---|---|
| Compiler | Zulu 8, 1.8.0_492, aarch64 |
| Language level | Java 8 |
| Metrics tool | ckjm 1.9, build "1.9 April 4 2012" |
| Bytecode library | Apache BCEL 5.2, bundled in the ckjm jar |
| Analysis target | Compiled `.class` files |

### Why Java 8

BCEL 5.2 predates the `invokedynamic` constant pool entry and throws `ClassFormatException` on any class file containing it. JDK 9 and later compile string concatenation to `invokedynamic`, so a modern compiler would make almost every class unreadable to the tool. Java 8 compiles concatenation to `StringBuilder`.

Lambdas, method references and streams produce `invokedynamic` under every Java version and are therefore absent from both solutions.

The constraint comes from the instrument, not the design, and applies identically to both solutions.

### Invocation

```bash
ZULU8=/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home
CKJM=tools/ckjm-1.9/build/ckjm-1.9.jar

mkdir -p build/simple build/patterns
$ZULU8/bin/javac -d build/simple   $(find simple-solution/src  -name "*.java")
$ZULU8/bin/javac -d build/patterns $(find pattern-solution/src -name "*.java")

$ZULU8/bin/java -cp $CKJM:build/simple gr.spinellis.ckjm.MetricsFilter \
  $(find build/simple -name "*.class")   > metrics/ckjm-simple.txt

$ZULU8/bin/java -cp $CKJM:build/patterns gr.spinellis.ckjm.MetricsFilter \
  $(find build/patterns -name "*.class") > metrics/ckjm-pattern.txt
```

The `-cp` form is required. ckjm resolves superclasses through the JVM classpath, so the compiled output directory must be on it. Using `java -jar` reports DIT 0 for any class extending a project class, and does so without raising an error that stops the run.

---

## 7. Controlling the comparison

For the two measurements to be comparable, the programs must differ only in design.

1. Both implement one specification and produce byte-identical output for identical input. Verified across four file formats, a malformed input file, an unsupported extension, and a missing-argument case.
2. Parsing logic is copied verbatim between solutions. Only its placement differs.
3. `LegacyFlatFileReader` is byte-identical in both, apart from its package declaration. It represents an existing component and is the fixed point of the comparison.
4. Both are compiled with the same compiler, at the same language level, and measured with the same tool invocation.
5. Neither uses external libraries, so no dependency can appear in either coupling measurement.

---

## 8. Sensitivity check

DSC enters the reusability equation at +0.50, so a design with more classes scores higher irrespective of any other property. The pattern-based solution has 16 classes against the simple solution's 9.

The pattern-based reusability value is therefore computed twice. Once with its measured DSC of 16, and once with DSC held at 9, all other measured values unchanged. The second figure isolates how much of any observed change is attributable to class count alone.

---

## 9. Known threats to validity

**Definitional.** The DCC values here follow the broad reading and are not directly comparable to studies applying a narrower one. The source definition does not settle which is intended.

**Tool dependence.** Ferenc et al. (2020) found three static analysis tools agreeing on CBO for 7.4 percent of classes. Values here should not be compared against those obtained with a different tool.

**Scale.** Two systems of 9 and 16 classes. No statistical test is applicable and no generalisation is claimed.

**Construct.** QMOOD derives reusability from structural properties. AlOmar et al. (2022) found that established metrics largely failed to register refactoring undertaken explicitly to improve reuse, and Ampatzoglou et al. (2018) argue such models measure the attributes that affect reusability rather than reusability itself.
