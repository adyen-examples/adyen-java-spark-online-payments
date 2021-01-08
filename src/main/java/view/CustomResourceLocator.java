package view;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import com.google.common.io.Files;
import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.loader.ResourceLocator;
import com.hubspot.jinjava.loader.ResourceNotFoundException;

public class CustomResourceLocator implements ResourceLocator {

    public static final String jinjaRoot = "src/main/resources/templates/";
    private File baseDir;

    public CustomResourceLocator() {
        this.baseDir = new File(".");
    }

    private File resolveFileName(String name) {
        File f = new File(name);

        if (f.isAbsolute()) {
            return f;
        }

        return new File(baseDir, name);
    }

    @Override
    public String getString(String name, Charset encoding, JinjavaInterpreter interpreter) throws IOException {
        File file = resolveFileName(jinjaRoot + name);

        if (!file.exists() || !file.isFile()) {
            throw new ResourceNotFoundException("Couldn't find resource: " + file);
        }

        return Files.toString(file, encoding);
    }
}
