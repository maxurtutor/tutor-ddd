package org.maxur.ldoc;


import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author myunusov
 * @version 1.0
 * @since <pre>07.08.2016</pre>
 */
public class Glossary {

    public static boolean start(RootDoc root){
        String tagName = "concept";
        writeContents(root.classes(), tagName);
        return true;
    }

    private static void writeContents(ClassDoc[] classes, String tagName) {
        List<String> lines = new ArrayList<>();

        Path file = Paths.get("Glossary.md");


        for (int i=0; i < classes.length; i++) {
            Tag[] tags = classes[i].tags(tagName);
            if (tags.length > 0) {
                for (int k=0; k < tags.length; k++) {
                    lines.add(
                            "  \n" + classes[i].name() + ": "
                            + tags[k].text());
                }
            }
        }

        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
