# CWD-1113 CSV注入

**别名: **CSV文件中公式元素中和不当；Excel宏注入；公式注入；DDE代码注入

**描述**
将用户提供的信息保存到 CSV文件中，但未处理或不正确处理 CSV文件中的特殊元素，这些特殊元素在电子表格打开 CSV文件时可能被解释为命令。用户提供的数据通常保存到传统的数据库中。这些数据可以导出为 CSV文件，用户可以使用Excel、Numbers或Calc等电子表格软件读取数据。此软件将以`=`开头的条目解释为公式，然后由电子表格软件执行。软件的公式语言通常允许方法访问超链接或本地命令行，并且经常允许足够的字符来调用整个脚本。攻击者可以填充数据字段，这些数据字段在保存到 CSV文件时，可能会在电子表格软件自动执行时尝试信息泄漏或其他恶意活动，例如：


- 通过利用电子表格软件中的漏洞（如CVE-2014-3524）劫持用户的计算机。- 利用用户倾向于忽略从自己网站下载的电子表格中的安全警告，劫持用户的计算机。- 从电子表格或其他打开的电子表格中提取内容。
**语言: **JAVA

**严重等级**
严重

**cleancode特征**
安全,可靠

**示例**
**案例1: 输入验证并清理特殊字符预防CSV注入**
**语言: **JAVA

**描述**
一个Java Web应用程序，允许用户上传CSV文件以导入数据。系统后端使用`OpenCSV`库来解析 CSV文件，并将数据存储到数据库中。然而，如果系统没有对CSV文件中的内容进行验证和清理，攻击者可以通过上传包含恶意公式的 CSV文件，触发 CSV注入漏洞。

**案例分析**
1. 攻击者构造恶意CSV文件：攻击者构造了一个包含恶意公式的CSV文件，文件内容如下：
```vb
Name,Email,Score
Alice,alice@example.com,=IFERROR(IMPORTFILE("C:\Users\*\Documents\*"), "")
Bob,bob@example.com,=HYPERLINK("http://malicious.com", "Click Me")
```
2. 上传恶意CSV文件：攻击者将构造好的CSV文件上传到Java Web应用程序。
3. 系统解析CSV文件：系统后端使用`OpenCSV`库解析CSV文件，直接将公式视为普通文本处理。
4. 恶意公式被触发：由于系统没有对公式进行验证或清理，恶意公式被解析并执行，导致敏感数据泄露或恶意操作被执行。

**反例**
```java
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVProcessor {

    public static void main(String[] args) {
        String csvFile = "input.csv";
        processCSV(csvFile);
    }

    public static void processCSV(String csvFile) {
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            // 读取CSV文件的所有行
            List<String[]> rows = reader.readAll();

            // 危险：未验证和处理数据，直接存储。
            List<String[]> cleanedRows = new ArrayList<>();
            for (String[] row : rows) {
                String[] cleanedRow = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    cleanedRow[i] = row[i];
                }
                cleanedRows.add(cleanedRow);
            }

            // 存储到数据库或其他处理逻辑
            storeToDatabase(cleanedRows);

        } catch (IOException | CsvValidationException e) {
            // ...处理异常
        }
    }

    // 存储到数据库的方法（示例）
    private static void storeToDatabase(List<String[]> data) {
        // ...数据库存储逻辑
    }
}
```

**正例**
```java
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVProcessor {

    // 定义允许的字符集合
    private static final String ALLOWED_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_@.";

    public static void main(String[] args) {
        String csvFile = "input.csv";
        processCSV(csvFile);
    }

    public static void processCSV(String csvFile) {
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            // 读取CSV文件的所有行
            List<String[]> rows = reader.readAll();

            // 验证和清理每一行数据
            List<String[]> cleanedRows = new ArrayList<>();
            for (String[] row : rows) {
                String[] cleanedRow = new String[row.length];
                for (int i = 0; i < row.length; i++) {
                    cleanedRow[i] = sanitizeInput(row[i]);
                }
                cleanedRows.add(cleanedRow);
            }

            // 存储到数据库或其他处理逻辑
            storeToDatabase(cleanedRows);

        } catch (IOException | CsvValidationException e) {
            // ...处理异常
        }
    }

    // 输入验证和清理方法
    private static String sanitizeInput(String input) {
        // 1. 检查输入是否包含恶意字符（如公式前缀 '='）
        if (input.startsWith("=")) {
            // 如果是恶意公式，可以选择删除或转义
            return input.replace("=", "");
        }

        // 2. 验证输入是否只包含允许的字符
        for (char c : input.toCharArray()) {
            if (!ALLOWED_CHARACTERS.contains(String.valueOf(c))) {
                // 如果包含非法字符，抛出异常或清理
                return input.replace(c, ' ');
            }
        }

        return input;
    }

    // 存储到数据库的方法（示例）
    private static void storeToDatabase(List<String[]> data) {
        // ...数据库存储逻辑
    }
}
```

**修复建议**
- 输入验证和清理：`sanitizeInput` 方法用于验证和清理输入数据。
检查输入是否以`=`开头（这通常是公式前缀），如果是，则删除或转义`=`。
验证输入是否只包含允许的字符（如字母、数字、`_`、`@` 和`.`）。
- 使用安全的CSV解析库：使用`OpenCSV`库来解析CSV文件，避免直接执行恶意公式。

**案例2: 转义特殊字符`=`预防CSV注入**
**语言: **JAVA

**描述**
CSV注入是一种安全漏洞，攻击者通过在CSV文件中插入恶意公式，导致解析时执行恶意代码，可能造成数据泄露或其他安全问题。例如，攻击者上传一个包含恶意公式的CSV文件，导致解析时执行删除文件或窃取数据的操作。

**反例**
```java
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CSVInjectionExample {
    public static void main(String[] args) {
        try (CSVParser parser = new CSVParser(new FileReader("example.csv"), CSVParser.DEFAULT_SETTINGS)) {
            for (CSVRecord record : parser) {
                String formula = record.get(0);
                // 危险：直接执行公式，可能导致注入
                evaluateFormula(formula);
            }
        } catch (IOException e) {
            // 异常处理...;
        }
    }

    private static void evaluateFormula(String formula) {
        // 假设这是一个执行公式的函数
        // 恶意公式可能导致代码执行
        ...("Evaluating formula: " + formula);
        // 模拟执行，实际可能更危险
        if (formula.contains("=DELETION_FUNCTION()")) {
            log.info("Deleting critical files...");
            // 实际代码可能执行文件删除或其他恶意操作
        }
    }
}
```

**正例**
```java
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class SafeCSVProcessing {
    public static void main(String[] args) {
        try (CSVParser parser = new CSVParser(new FileReader("example.csv"), CSVParser.DEFAULT_SETTINGS)) {
            for (CSVRecord record : parser) {
                String formula = record.get(0);
                // 安全处理：转义特殊字符或使用安全的解析方法
                String safeFormula = sanitizeFormula(formula);
                evaluateFormula(safeFormula);
            }
        } catch (IOException e) {
            // 异常处理...;
        }
    }

    private static String sanitizeFormula(String formula) {
        // 转义特殊字符，防止注入
        return formula.replace("=", "");
    }

    private static void evaluateFormula(String formula) {
        // 安全执行公式
        ...("Evaluating safe formula: " + formula);
        // 检查是否有恶意模式
        if (formula.contains("DELETION_FUNCTION")) {
            throw new SecurityException("Malicious formula detected");
        }
    }
}
```

**修复建议**
- 转义特殊字符`=`防止CSV注入。`sanitizeInput`方法用于验证和清理输入数据，检查输入是否以`=`开头（这通常是公式前缀），如果是，则删除或转义`=`。

#### CWD-1113-000 CSV注入

#### CWD-1113-001 【内容外部控制】按照csv文件格式，使用原始的JDK文件操作动态构造csv文件内容，并输出csv后缀文件

#### CWD-1113-002 【创建Excel、CSV内容时，(部分)内容数据来源外部控制】使用com.csvreader.CsvWriter写CSV文件，内容来自不可信数据，直接写CSV文件

#### CWD-1113-003 【创建Excel、CSV内容时，(部分)内容数据来源外部控制】使用com.opencsv.CSVWriter写CSV文件，内容来自不可信数据，直接写CSV文件

#### CWD-1113-004 【创建Excel、CSV内容时，(部分)内容数据来源外部控制】使用JasperReports渲染输出csv文件时，数据内容不可信导致DDE注入问题(导出xls、xlsx表格文件时无问题)

#### CWD-1113-005 【创建Excel、CSV内容时，(部分)内容数据来源外部控制】使用org.apache.commons写CSV文件，内容来自不可信数据

#### CWD-1113-006 【文件外部控制】多用户系统中存在上传CSV/Excel文件，并可被其它用户下载的场景。用户从可信的平台下载的文件，警惕性低，用户容易受到攻击

#### CWD-1113-007 【创建Excel、CSV内容时，(部分)内容数据来源外部控制】使用org.apache.poi生成Excel文件，内容来自不可信数据，直接写Excel文件

#### CWD-1113-008 【创建Excel、CSV内容时，(部分)内容数据来源外部控制】使用com.alibaba.excel.EasyExcel生成Excel文件，内容来自不可信数据，直接写Excel文件

**业界缺陷**

- [CWE-1236: Improper Neutralization of Formula Elements in a CSV File](https://cwe.mitre.org/data/definitions/1236.html)
---

