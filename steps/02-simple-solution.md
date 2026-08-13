# Step 02 — Sample Data and the Simple Solution

Read `RULES.md` and `CONTEXT.md` first. Everything in them applies.

Build the sample data files, then the nine classes of the simple solution.
Follow `uml/simple-solution.puml` exactly. Do not add classes, methods, or
fields that are not in this document.

---

## Reminders that will break the build if ignored

- No lambdas, no method references, no streams. Anonymous inner classes and
  explicit `for` loops only.
- No external libraries. No `javax.xml`, no JSON library. Parsing is
  hand-written with `String` operations.
- Java 8 syntax only. No `var`, no diamond-free-for-all beyond Java 8.
- String concatenation with `+` is allowed.
- Package: `com.dpat.simple`
- Source root: `simple-solution/src/com/dpat/simple/`

---

## 1. Sample data

Create four files under `data/`. Each holds the same five products so the
four import paths can be compared directly.

The five products:

| sku | name | price | quantity |
|---|---|---|---|
| SKU-1001 | Steel Hex Bolt M8 | 0.45 | 5000 |
| SKU-1002 | Nylon Washer 10mm | 0.12 | 12000 |
| SKU-1003 | Cordless Drill 18V | 129.99 | 45 |
| SKU-1004 | Safety Goggles Clear | 8.75 | 300 |
| SKU-1005 | Measuring Tape 5m | 14.50 | 180 |

### `data/products.csv`

Header row, then five records.

```
sku,name,price,quantity
SKU-1001,Steel Hex Bolt M8,0.45,5000
```

No quoted fields, no embedded commas. Split on `,` is sufficient.

### `data/products.json`

Flat array of objects. All values are strings, including numbers.

```json
[
  {"sku": "SKU-1001", "name": "Steel Hex Bolt M8", "price": "0.45", "quantity": "5000"}
]
```

One object per line. No nesting, no arrays inside objects, no escaped
characters.

### `data/products.xml`

```xml
<products>
  <product>
    <sku>SKU-1001</sku>
    <name>Steel Hex Bolt M8</name>
    <price>0.45</price>
    <quantity>5000</quantity>
  </product>
</products>
```

No attributes, no namespaces, no self-closing tags.

### `data/products.txt`

Fixed-width, no header, no delimiters. Column layout:

| Field | Start | Length |
|---|---|---|
| sku | 0 | 10 |
| name | 10 | 30 |
| price | 40 | 10 |
| quantity | 50 | 8 |

Every line is exactly 58 characters, right-padded with spaces.

```
SKU-1001  Steel Hex Bolt M8             0.45      5000    
```

Verify the alignment before moving on. Count the characters.

---

## 2. The nine classes

### `Product`

Immutable value object.

```java
private final String sku;
private final String name;
private final double price;
private final int quantity;

public Product(String sku, String name, double price, int quantity)
public String getSku()
public String getName()
public double getPrice()
public int getQuantity()
```

No `toString`, no `equals`, no `hashCode`. Nothing uses them.

### `ValidationException`

```java
public class ValidationException extends Exception {
    public ValidationException(String message)
}
```

Checked exception. Nothing else.

### `ImportResult`

```java
private final List<Product> products;
private final List<String> errors;

public ImportResult()                        // initialises both lists
public void addProduct(Product p)
public void addError(String message)
public List<Product> getProducts()
public List<String> getErrors()
public int getSuccessCount()                 // products.size()
public int getFailureCount()                 // errors.size()
```

Counts are derived. Do not store them as fields.

### `LegacyFlatFileReader`

**This class represents an existing third-party component.** It is
byte-identical in the pattern-based solution. It must not know about
`Product`, `ImportResult`, or anything else in this project.

```java
public String[][] readAll(File file) throws IOException
```

Reads the fixed-width file and returns one `String[]` of length 4 per line,
in column order sku, name, price, quantity. Each value is trimmed. Blank
lines are skipped. Substring positions come from the layout table above.

Returns raw strings only. No parsing of numbers, no validation.

### `CsvImporter`, `JsonImporter`, `XmlImporter`

All three share this shape:

```java
public ImportResult read(Path file) throws IOException
private List<Map<String,String>> parseRecords(Path file) throws IOException
private Product mapFields(Map<String,String> record) throws ValidationException
private void validate(Product p) throws ValidationException
```

`read` is identical in all three:

```java
public ImportResult read(Path file) throws IOException {
    ImportResult result = new ImportResult();
    List<Map<String,String>> records = parseRecords(file);
    for (int i = 0; i < records.size(); i++) {
        try {
            Product p = mapFields(records.get(i));
            validate(p);
            result.addProduct(p);
        } catch (ValidationException e) {
            result.addError("Record " + (i + 1) + ": " + e.getMessage());
        }
    }
    return result;
}
```

`mapFields` and `validate` are **identical copies** across all three classes.
Write them out in full in each class. Do not extract them to a shared helper,
do not create a base class, do not use an interface. That duplication is a
property of this design and removing it changes what is being measured.

`parseRecords` is the only method that differs.

- **CSV:** read all lines. First line is the header, split on `,` for keys.
  Each later line splits on `,` and pairs positionally with the keys.
- **JSON:** read all lines. Skip lines that do not contain `{`. For each
  object line, strip `{`, `}`, and a trailing `,`, split on `,`, then split
  each pair on `:` and strip `"` and whitespace from both sides.
- **XML:** read the whole file as one string. Find each `<product>` to
  `</product>` block. Inside each block extract the text between `<sku>` and
  `</sku>`, and the same for `name`, `price`, `quantity`.

### `mapFields`, the exact body for all three

```java
private Product mapFields(Map<String,String> record) throws ValidationException {
    String sku = record.get("sku");
    String name = record.get("name");
    String priceText = record.get("price");
    String quantityText = record.get("quantity");

    if (sku == null || name == null || priceText == null || quantityText == null) {
        throw new ValidationException("missing field");
    }

    double price;
    int quantity;
    try {
        price = Double.parseDouble(priceText);
    } catch (NumberFormatException e) {
        throw new ValidationException("price is not a number: " + priceText);
    }
    try {
        quantity = Integer.parseInt(quantityText);
    } catch (NumberFormatException e) {
        throw new ValidationException("quantity is not a number: " + quantityText);
    }

    return new Product(sku, name, price, quantity);
}
```

### `validate`, the exact body for all three

```java
private void validate(Product p) throws ValidationException {
    if (p.getSku().trim().length() == 0) {
        throw new ValidationException("sku is empty");
    }
    if (p.getName().trim().length() == 0) {
        throw new ValidationException("name is empty");
    }
    if (p.getPrice() < 0) {
        throw new ValidationException("price is negative");
    }
    if (p.getQuantity() < 0) {
        throw new ValidationException("quantity is negative");
    }
}
```

### `ImportManager`

```java
public ImportResult importFile(Path file) throws IOException
private String extensionOf(Path file)
private ImportResult importLegacy(Path file) throws IOException
private Product mapLegacyRow(String[] row) throws ValidationException
private void validate(Product p) throws ValidationException
```

`importFile` selects by extension using a conditional chain that names each
concrete type directly:

```java
public ImportResult importFile(Path file) throws IOException {
    String ext = extensionOf(file);
    if (ext.equals("csv")) {
        CsvImporter importer = new CsvImporter();
        return importer.read(file);
    } else if (ext.equals("json")) {
        JsonImporter importer = new JsonImporter();
        return importer.read(file);
    } else if (ext.equals("xml")) {
        XmlImporter importer = new XmlImporter();
        return importer.read(file);
    } else if (ext.equals("txt")) {
        return importLegacy(file);
    } else {
        ImportResult result = new ImportResult();
        result.addError("Unsupported file type: " + ext);
        return result;
    }
}
```

`importLegacy` calls `LegacyFlatFileReader` and converts the rows here,
inside `ImportManager`:

```java
private ImportResult importLegacy(Path file) throws IOException {
    ImportResult result = new ImportResult();
    LegacyFlatFileReader reader = new LegacyFlatFileReader();
    String[][] rows = reader.readAll(file.toFile());
    for (int i = 0; i < rows.length; i++) {
        try {
            Product p = mapLegacyRow(rows[i]);
            validate(p);
            result.addProduct(p);
        } catch (ValidationException e) {
            result.addError("Record " + (i + 1) + ": " + e.getMessage());
        }
    }
    return result;
}
```

`mapLegacyRow` maps positionally by array index rather than by key, since
the legacy reader returns no field names. `validate` is a fourth identical
copy of the method above.

### `Main`

```java
public static void main(String[] args)
```

Takes one argument, the path to an import file. If no argument is given,
print `Usage: Main <file>` and exit.

Otherwise call `ImportManager.importFile` and print, exactly:

```
Import complete: 5 succeeded, 0 failed
```

If there are errors, print a blank line, then `Errors:`, then each error
indented by two spaces on its own line.

**This output format is fixed.** The pattern-based solution must produce
byte-identical output for the same input, or the comparison in Section 2 is
invalid.

Catch `IOException` in `main` and print `Error reading file: ` followed by
the message.

---

## 3. Verify

Run from the repository root.

```bash
ZULU8=/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home
CKJM=tools/ckjm-1.9/build/ckjm-1.9.jar

mkdir -p build/simple
$ZULU8/bin/javac -d build/simple $(find simple-solution/src -name "*.java")
```

Then all four formats:

```bash
$ZULU8/bin/java -cp build/simple com.dpat.simple.Main data/products.csv
$ZULU8/bin/java -cp build/simple com.dpat.simple.Main data/products.json
$ZULU8/bin/java -cp build/simple com.dpat.simple.Main data/products.xml
$ZULU8/bin/java -cp build/simple com.dpat.simple.Main data/products.txt
```

All four must print `Import complete: 5 succeeded, 0 failed`.

Then confirm the metrics tool can read the output:

```bash
$ZULU8/bin/java -jar $CKJM $(find build/simple -name "*.class")
```

Nine lines of numbers, one per class. A `ClassFormatException: Invalid byte
tag in constant pool: 18` means a banned construct was used somewhere. Find
it and remove it before continuing.

---

## 4. Commit

```bash
git add .
git commit -m "step 02: sample data and simple solution"
```

---

## Do not

- Do not extract `mapFields` or `validate` into a shared class or interface.
- Do not add a common interface or base class for the importers.
- Do not modify `LegacyFlatFileReader` to know about `Product`.
- Do not add logging, tests, a build file, or a `toString`.
- Do not "improve" `ImportManager` by removing the conditional chain.

The weaknesses in this design are deliberate and specific. They are what the
pattern-based solution is measured against. Removing them makes the
comparison meaningless.
