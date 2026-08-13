# Step 03 — Pattern-Based Solution

Read `RULES.md` and `CONTEXT.md` first. Everything in them applies.

Build the sixteen classes of the pattern-based solution. Follow
`uml/pattern-solution.puml` exactly. Do not add classes, methods, or fields
that are not in this document.

**This solution must produce byte-identical output to the simple solution
for every input.** That equivalence is what makes the measurement in
Section 2 valid. Any behavioural difference invalidates the whole comparison.

---

## Reminders that will break the build if ignored

- No lambdas, no method references, no streams.
- No external libraries. Parsing is hand-written.
- Java 8 syntax only. No `var`, no diamond operator on anonymous classes.
- String concatenation with `+` is allowed.
- Package: `com.dpat.patterns`
- Source root: `pattern-solution/src/com/dpat/patterns/`

---

## 1. Classes copied unchanged from the simple solution

Copy these four from `simple-solution/src/com/dpat/simple/`, changing only
the package declaration to `com.dpat.patterns`. Change nothing else, not a
character of logic.

- `Product.java`
- `ImportResult.java`
- `ValidationException.java`
- `LegacyFlatFileReader.java`

`LegacyFlatFileReader` matters most. It represents an existing third-party
component and is the fixed point of the comparison. It must still know
nothing about `Product`, `ImportResult`, or `ProductParser`. Its only method
remains `readAll(File): String[][]`.

---

## 2. `ProductParser` — the Target interface

```java
package com.dpat.patterns;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface ProductParser {
    List<Map<String,String>> parse(Path file) throws IOException;
}
```

Parsers handle syntax only. They do not map to `Product` and they do not
validate.

---

## 3. `CsvParser`, `JsonParser`, `XmlParser`

Each implements `ProductParser` with one public method:

```java
public List<Map<String,String>> parse(Path file) throws IOException
```

**The body of each is the `parseRecords` method from the corresponding
importer in the simple solution, copied verbatim.** `CsvParser.parse` is
`CsvImporter.parseRecords`. `JsonParser.parse` is `JsonImporter.parseRecords`.
`XmlParser.parse` is `XmlImporter.parseRecords`.

Do not improve the parsing. Do not fix edge cases. If the parsing logic
differs between the two solutions, the comparison measures parsing
differences rather than design differences.

No other methods. No fields.

---

## 4. `LegacyFlatFileAdapter` — the Adapter

```java
public class LegacyFlatFileAdapter implements ProductParser {
    private final LegacyFlatFileReader reader;

    public LegacyFlatFileAdapter() {
        this.reader = new LegacyFlatFileReader();
    }

    public List<Map<String,String>> parse(Path file) throws IOException {
        String[][] rows = reader.readAll(file.toFile());
        List<Map<String,String>> records = new ArrayList<Map<String,String>>();
        for (int i = 0; i < rows.length; i++) {
            Map<String,String> record = new HashMap<String,String>();
            record.put("sku", rows[i][0]);
            record.put("name", rows[i][1]);
            record.put("price", rows[i][2]);
            record.put("quantity", rows[i][3]);
            records.add(record);
        }
        return records;
    }
}
```

This is an object adapter. It holds the Adaptee by composition and
translates the positional `String[][]` into the keyed records the Target
declares. That translation is the same logic that sat inside
`ImportManager.mapLegacyRow` in the simple solution, minus the mapping and
validation, which now live in `ProductMapper`.

---

## 5. `ProductMapper`

```java
public class ProductMapper {
    public Product map(Map<String,String> record) throws ValidationException
    public void validate(Product p) throws ValidationException
}
```

`map` is the body of `mapFields` from the simple solution's importers,
copied verbatim and made public.

`validate` is the body of `validate` from the same place, copied verbatim
and made public.

**This is the point of the refactoring.** Logic that appeared four times in
the simple solution appears once here. No other class may contain mapping
or validation logic.

---

## 6. `ImportJob` — the abstract Creator

```java
public abstract class ImportJob {

    public ImportResult run(Path file) throws IOException {
        ImportResult result = new ImportResult();
        ProductParser parser = createParser();
        ProductMapper mapper = new ProductMapper();
        List<Map<String,String>> records = parser.parse(file);
        for (int i = 0; i < records.size(); i++) {
            try {
                Product p = mapper.map(records.get(i));
                mapper.validate(p);
                result.addProduct(p);
            } catch (ValidationException e) {
                result.addError("Record " + (i + 1) + ": " + e.getMessage());
            }
        }
        return result;
    }

    protected abstract ProductParser createParser();
}
```

`run` holds the invariant workflow. `createParser` is the factory method.
`ImportJob` never names a concrete parser class.

The error message format must match the simple solution exactly, including
the one-based record numbering.

---

## 7. The four ConcreteCreators

Each is a complete class with one method.

```java
public class CsvImportJob extends ImportJob {
    protected ProductParser createParser() {
        return new CsvParser();
    }
}
```

Same shape for `JsonImportJob` returning `new JsonParser()`,
`XmlImportJob` returning `new XmlParser()`, and `LegacyImportJob` returning
`new LegacyFlatFileAdapter()`.

No fields. No other methods. No constructors.

---

## 8. `Main`

```java
public class Main {
    public static void main(String[] args)
    private static ImportJob jobFor(Path file)
}
```

`jobFor` extracts the extension and returns the matching job, or `null` for
an unrecognised extension:

```java
private static ImportJob jobFor(Path file) {
    String name = file.getFileName().toString();
    int dot = name.lastIndexOf('.');
    String ext = "";
    if (dot >= 0) {
        ext = name.substring(dot + 1).toLowerCase();
    }
    if (ext.equals("csv")) {
        return new CsvImportJob();
    } else if (ext.equals("json")) {
        return new JsonImportJob();
    } else if (ext.equals("xml")) {
        return new XmlImportJob();
    } else if (ext.equals("txt")) {
        return new LegacyImportJob();
    } else {
        return null;
    }
}
```

The extension extraction must behave identically to `extensionOf` in the
simple solution's `ImportManager`. Read that method and match it.

`main` handles arguments, runs the job, and prints. When `jobFor` returns
`null`, build an `ImportResult`, add the error
`"Unsupported file type: " + ext`, and print it through the same path, so
the output matches the simple solution for an unsupported file.

Output format, byte-identical to the simple solution:

```
Import complete: 5 succeeded, 0 failed
SKU-1001 | Steel Hex Bolt M8 | 0.45 | 5000
```

Summary line, then the error block if there are errors, then one line per
product with price to two decimal places. Read the simple solution's `Main`
and match its output exactly, including blank lines and indentation.

---

## 9. Verify

```bash
cd /Users/qicai/Projects/DPAT/DPAT_Assignment/dpat-coursework
ZULU8=/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home
CKJM=tools/ckjm-1.9/build/ckjm-1.9.jar

rm -rf build/patterns && mkdir -p build/patterns
$ZULU8/bin/javac -d build/patterns $(find pattern-solution/src -name "*.java")
echo "exit: $?"
ls -1 build/patterns/com/dpat/patterns/ | wc -l
```

Expect exit 0 and sixteen class files.

```bash
for f in csv json xml txt; do
  echo "=== $f ==="
  $ZULU8/bin/java -cp build/patterns com.dpat.patterns.Main data/products.$f
done
```

All four print `Import complete: 5 succeeded, 0 failed` and the same five
product lines.

### The equivalence check

This is the one that matters.

```bash
rm -rf build/simple && mkdir -p build/simple
$ZULU8/bin/javac -d build/simple $(find simple-solution/src -name "*.java")

for f in csv json xml txt; do
  $ZULU8/bin/java -cp build/simple   com.dpat.simple.Main   data/products.$f > /tmp/s.$f
  $ZULU8/bin/java -cp build/patterns com.dpat.patterns.Main data/products.$f > /tmp/p.$f
  diff /tmp/s.$f /tmp/p.$f > /dev/null && echo "$f: IDENTICAL" || echo "$f: DIFFERS"
done

$ZULU8/bin/java -cp build/simple   com.dpat.simple.Main   /tmp/bad.csv > /tmp/s.bad
$ZULU8/bin/java -cp build/patterns com.dpat.patterns.Main /tmp/bad.csv > /tmp/p.bad
diff /tmp/s.bad /tmp/p.bad > /dev/null && echo "errors: IDENTICAL" || echo "errors: DIFFERS"
```

All five lines must say IDENTICAL. `DIFFERS` on any of them means the two
programs are not solving the same problem, and the fault must be found and
fixed before proceeding.

### Metrics

```bash
mkdir -p metrics
$ZULU8/bin/java -jar $CKJM $(find build/patterns -name "*.class") \
  | tee metrics/ckjm-pattern.txt
```

Sixteen lines. A `ClassFormatException` means a banned construct was used.

---

## 10. Commit

```bash
git add .
git commit -m "step 03: pattern-based solution"
```

---

## Do not

- Do not modify anything under `simple-solution/`.
- Do not modify `LegacyFlatFileReader` beyond its package declaration.
- Do not add a parser registry, a map of extensions to jobs, or a static
  factory method. `jobFor` stays a conditional chain in `Main`.
- Do not give `ProductParser` a second method.
- Do not move mapping or validation out of `ProductMapper`.
- Do not add `toString`, `equals`, `hashCode`, logging, or tests.
- Do not improve the parsing logic. It must match the simple solution
  verbatim so the comparison isolates design, not parsing.
