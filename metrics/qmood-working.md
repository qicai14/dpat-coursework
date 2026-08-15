# QMOOD Working — Per-Class Derivations

Hand-computed values supporting the reusability comparison. Every figure is traceable to a named class and a stated rule.

Counting rules were fixed before measurement and are recorded in `docs/counting-protocol.md`.

---

## Equation

```
Reusability = −0.25 × DCC + 0.25 × CAM + 0.50 × CIS + 0.50 × DSC
```

Source: Bansiya and Davis (2002).

| Property | Metric |
|---|---|
| Coupling | DCC, Direct Class Coupling |
| Cohesion | CAM, Cohesion Among Methods of Class |
| Messaging | CIS, Class Interface Size |
| Design Size | DSC, Design Size in Classes |

---

## Counting rules applied

**DCC.** Distinct user-defined classes referenced through field declarations, method parameter types, return types, `new` instantiations, declared thrown types, superclass or implemented interfaces, and local variable declarations. JDK types excluded, consistent with ckjm's default exclusion of `java.*` from CBO. Each distinct class counted once.

**CIS.** Public methods per class including public constructors. Taken from ckjm's NPM column.

**CAM.** For class C, with AllTypes(C) the union of distinct parameter types across all declared methods:

```
CAM(C) = ( Σ over methods m of |params(m) ∩ AllTypes(C)| ) ÷ ( |AllTypes(C)| × methods(C) )
```

Constructors count as methods. JDK types are included, since CAM measures signature consistency rather than design coupling. Generic arguments are not unwrapped. Only explicitly declared methods are counted, since implicit constructors do not appear in source. **Where AllTypes(C) is empty the metric is undefined and the class is excluded from the average.**

**DSC.** Total classes in the design.

---

# SIMPLE SOLUTION

## DCC derivation

| Class | Referenced user-defined classes | DCC |
|---|---|---|
| Product | none | 0 |
| ImportResult | Product | 1 |
| ValidationException | none (`Exception` is JDK) | 0 |
| LegacyFlatFileReader | none | 0 |
| Main | ImportManager, ImportResult, Product | 3 |
| CsvImporter | ImportResult, Product, ValidationException | 3 |
| JsonImporter | ImportResult, Product, ValidationException | 3 |
| XmlImporter | ImportResult, Product, ValidationException | 3 |
| ImportManager | CsvImporter, JsonImporter, XmlImporter, LegacyFlatFileReader, ImportResult, Product, ValidationException | 7 |
| | **Total** | **20** |

## CAM derivation

| Class | Methods | AllTypes | \|AllTypes\| | Σ intersections | CAM |
|---|---|---|---|---|---|
| Product | 5 | String, double, int | 3 | 3 | 0.200 |
| ImportResult | 7 | Product, String | 2 | 2 | 0.143 |
| ValidationException | 1 | String | 1 | 1 | 1.000 |
| LegacyFlatFileReader | 1 | File | 1 | 1 | 1.000 |
| Main | 1 | String[] | 1 | 1 | 1.000 |
| CsvImporter | 4 | Path, Map, Product | 3 | 4 | 0.333 |
| JsonImporter | 4 | Path, Map, Product | 3 | 4 | 0.333 |
| XmlImporter | 4 | Path, Map, Product | 3 | 4 | 0.333 |
| ImportManager | 5 | Path, String[], Product | 3 | 5 | 0.333 |
| | | | | **Total** | **4.676** |

No class has an empty parameter type set, so all nine are defined.

## Summary

| Class | DCC | CIS | CAM |
|---|---|---|---|
| Product | 0 | 5 | 0.200 |
| ImportResult | 1 | 7 | 0.143 |
| ValidationException | 0 | 1 | 1.000 |
| LegacyFlatFileReader | 0 | 2 | 1.000 |
| Main | 3 | 2 | 1.000 |
| CsvImporter | 3 | 2 | 0.333 |
| JsonImporter | 3 | 2 | 0.333 |
| XmlImporter | 3 | 2 | 0.333 |
| ImportManager | 7 | 2 | 0.333 |
| **Total** | **20** | **25** | **4.676** |
| **Average** | **2.222** | **2.778** | **0.520** |

DSC = **9**

---

# PATTERN-BASED SOLUTION

## DCC derivation

| Class | Referenced user-defined classes | DCC |
|---|---|---|
| Product | none | 0 |
| ImportResult | Product | 1 |
| ValidationException | none | 0 |
| LegacyFlatFileReader | none | 0 |
| ProductParser | none | 0 |
| CsvParser | ProductParser | 1 |
| JsonParser | ProductParser | 1 |
| XmlParser | ProductParser | 1 |
| LegacyFlatFileAdapter | ProductParser, LegacyFlatFileReader | 2 |
| ProductMapper | Product, ValidationException | 2 |
| ImportJob | ImportResult, ProductParser, ProductMapper, Product, ValidationException | 5 |
| CsvImportJob | ImportJob, ProductParser, CsvParser | 3 |
| JsonImportJob | ImportJob, ProductParser, JsonParser | 3 |
| XmlImportJob | ImportJob, ProductParser, XmlParser | 3 |
| LegacyImportJob | ImportJob, ProductParser, LegacyFlatFileAdapter | 3 |
| Main | ImportJob, ImportResult, Product, CsvImportJob, JsonImportJob, XmlImportJob, LegacyImportJob | 7 |
| | **Total** | **32** |

## CAM derivation

| Class | Methods | AllTypes | \|AllTypes\| | Σ intersections | CAM |
|---|---|---|---|---|---|
| Product | 5 | String, double, int | 3 | 3 | 0.200 |
| ImportResult | 7 | Product, String | 2 | 2 | 0.143 |
| ValidationException | 1 | String | 1 | 1 | 1.000 |
| LegacyFlatFileReader | 1 | File | 1 | 1 | 1.000 |
| ProductParser | 1 | Path | 1 | 1 | 1.000 |
| CsvParser | 1 | Path | 1 | 1 | 1.000 |
| JsonParser | 1 | Path | 1 | 1 | 1.000 |
| XmlParser | 1 | Path | 1 | 1 | 1.000 |
| LegacyFlatFileAdapter | 2 | Path | 1 | 1 | 0.500 |
| ProductMapper | 2 | Map, Product | 2 | 2 | 0.500 |
| ImportJob | 2 | Path | 1 | 1 | 0.500 |
| CsvImportJob | 1 | none | 0 | — | undefined |
| JsonImportJob | 1 | none | 0 | — | undefined |
| XmlImportJob | 1 | none | 0 | — | undefined |
| LegacyImportJob | 1 | none | 0 | — | undefined |
| Main | 2 | String[], Path | 2 | 2 | 0.500 |
| | | | | **Total (12 defined)** | **8.343** |

The four ConcreteCreators declare only `createParser()`, which takes no parameters. Their parameter type set is empty and CAM is undefined.

Two conventions give: 8.343 ÷ 12 defined classes = **0.695**, or assigning 0 to undefined classes and dividing by 16 = **0.521**. The more conservative 0.521 is used below. The choice changes final reusability by 0.04 on a value near 8.7.

## Summary

| Class | DCC | CIS | CAM |
|---|---|---|---|
| Product | 0 | 5 | 0.200 |
| ImportResult | 1 | 7 | 0.143 |
| ValidationException | 0 | 1 | 1.000 |
| LegacyFlatFileReader | 0 | 2 | 1.000 |
| ProductParser | 0 | 1 | 1.000 |
| CsvParser | 1 | 2 | 1.000 |
| JsonParser | 1 | 2 | 1.000 |
| XmlParser | 1 | 2 | 1.000 |
| LegacyFlatFileAdapter | 2 | 2 | 0.500 |
| ProductMapper | 2 | 3 | 0.500 |
| ImportJob | 5 | 2 | 0.500 |
| CsvImportJob | 3 | 1 | undefined |
| JsonImportJob | 3 | 1 | undefined |
| XmlImportJob | 3 | 1 | undefined |
| LegacyImportJob | 3 | 1 | undefined |
| Main | 7 | 2 | 0.500 |
| **Total** | **32** | **35** | **8.343** |
| **Average** | **2.000** | **2.188** | **0.521** |

DSC = **16**

---

# CROSS-VALIDATION: DCC AGAINST CBO

DCC was hand-counted from source. CBO was computed from bytecode by ckjm 1.9. The two agree for every class in both solutions.

| Simple | DCC | CBO | Pattern | DCC | CBO |
|---|---|---|---|---|---|
| Product | 0 | 0 | Product | 0 | 0 |
| ImportResult | 1 | 1 | ImportResult | 1 | 1 |
| ValidationException | 0 | 0 | ValidationException | 0 | 0 |
| LegacyFlatFileReader | 0 | 0 | LegacyFlatFileReader | 0 | 0 |
| Main | 3 | 3 | ProductParser | 0 | 0 |
| CsvImporter | 3 | 3 | CsvParser | 1 | 1 |
| JsonImporter | 3 | 3 | JsonParser | 1 | 1 |
| XmlImporter | 3 | 3 | XmlParser | 1 | 1 |
| ImportManager | 7 | 7 | LegacyFlatFileAdapter | 2 | 2 |
| | | | ProductMapper | 2 | 2 |
| | | | ImportJob | 5 | 5 |
| | | | CsvImportJob | 3 | 3 |
| | | | JsonImportJob | 3 | 3 |
| | | | XmlImportJob | 3 | 3 |
| | | | LegacyImportJob | 3 | 3 |
| | | | Main | 7 | 7 |
| **Total** | **20** | **20** | **Total** | **32** | **32** |

Agreement on 25 of 25 classes.

---

# REUSABILITY

## Simple

```
Reusability = −0.25(2.222) + 0.25(0.520) + 0.50(2.778) + 0.50(9)
            = −0.556 + 0.130 + 1.389 + 4.500
            = 5.463
```

## Pattern-based

```
Reusability = −0.25(2.000) + 0.25(0.521) + 0.50(2.188) + 0.50(16)
            = −0.500 + 0.130 + 1.094 + 8.000
            = 8.724
```

Under the alternative CAM convention of 0.695, the value is 8.768. The difference is immaterial.

**Change: +59.7 percent.**

## Sensitivity check, design size held constant

Design size in classes enters the equation at +0.50, so a design with more classes scores higher irrespective of any other property. To isolate that effect, the pattern-based solution is recomputed with DSC fixed at the simple solution's value of 9, all other measured values unchanged.

```
Reusability = −0.25(2.000) + 0.25(0.521) + 0.50(2.188) + 0.50(9)
            = −0.500 + 0.130 + 1.094 + 4.500
            = 5.224
```

**Change with design size held constant: −4.4 percent.**

## Component contributions

| Component | Weight | Simple | Pattern | Contribution change |
|---|---|---|---|---|
| DCC | −0.25 | 2.222 | 2.000 | +0.056 |
| CAM | +0.25 | 0.520 | 0.521 | +0.000 |
| CIS | +0.50 | 2.778 | 2.188 | −0.295 |
| DSC | +0.50 | 9 | 16 | +3.500 |
| | | | **Net** | **+3.261** |

Coupling contributes +0.056 and cohesion is flat. Class Interface Size falls, costing −0.295, more than five times the coupling gain. The observed improvement is therefore attributable to design size.

---

# ALL MEASURES

| Measure | Simple (9) | Pattern (16) | Change |
|---|---|---|---|
| **QMOOD Reusability** | **5.463** | **8.724** | **+59.7%** |
| QMOOD Reusability, DSC = 9 | 5.463 | 5.224 | −4.4% |
| DCC average | 2.222 | 2.000 | −10.0% |
| CAM average | 0.520 | 0.521 | +0.2% |
| CIS average | 2.778 | 2.188 | −21.3% |
| DSC | 9 | 16 | +77.8% |
| CBO average | 2.222 | 2.000 | −10.0% |
| WMC average | 4.222 | 2.563 | −39.3% |
| DIT average | 1.222 | 1.375 | +12.5% |
| NOC average | 0.000 | 0.250 | — |
| RFC average | 23.111 | 11.500 | −50.2% |
| LCOM average | 5.444 | 1.188 | −78.2% |

CK values from `ckjm-simple.txt` and `ckjm-pattern.txt`, collected with ckjm 1.9 (build 1.9 April 4 2012) under Zulu 8.
