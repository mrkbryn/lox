package tool;

import lox.Lox;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Integration {

    public static void main(String[] args) {
        List<String> fileNames = gatherTestFiles();
        for (String path : fileNames) {
            try {
                runAndCheckExpected(path);
            } catch (Exception exception) {
                System.out.println("Failed to run " + path);
            }
        }
    }

    static List<String> gatherTestFiles() {
        File dir = new File("/Users/mabryan/code/lox/lox");
        File[] files = dir.listFiles();
        List<String> paths = new ArrayList<>();
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".lox")) {
                paths.add(file.getAbsolutePath());
            }
        }
        return paths;
    }

    static void runAndCheckExpected(String path) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream prevStream = System.out;

        String result = null;
        try {
            // Reroute Lox output to a new PrintStream.
            PrintStream printStream = new PrintStream(byteArrayOutputStream, true, StandardCharsets.UTF_8.name());
            System.setOut(printStream);
            System.setErr(printStream);

            // Run Lox after updating System.out, System.err.
            Lox.main(new String[]{ path });

            // Store the System.out, System.err result.
            result = byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());
        } catch (Exception exception) {
            // Ignore.
        } finally {
            System.setOut(prevStream);
            System.setErr(prevStream);
        }

        String expected = getExpectedResult(path);
        compareResults(path, result, expected);
    }

    static void compareResults(String path, String output, String expected) {
        if (expected.equals(output)) {
            System.out.println("PASSED: " + path);
        } else {
            System.err.println("ERROR: " + path);
            System.out.println("> Expected: \n" + expected);
            System.out.println("> Output: \n" + output);
        }
    }

    static String getExpectedResult(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String contents = new String(bytes, Charset.defaultCharset());

        StringBuilder result = new StringBuilder();
        for (String line : contents.split("\n")) {
            String expect = "// expect: ";
            int index = line.indexOf(expect);
            if (index != -1) {
                result.append(line.substring(index + expect.length())).append("\n");
            }
        }

        return result.toString();
    }
}
