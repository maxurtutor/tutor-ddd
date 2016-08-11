/*
 * Copyright 2016 Russian Post
 *
 * This source code is Russian Post Confidential Proprietary.
 * This software is protected by copyright. All rights and titles are reserved.
 * You shall not use, copy, distribute, modify, decompile, disassemble or reverse engineer the software.
 * Otherwise this violation would be treated by law and would be subject to legal prosecution.
 * Legal use of the software provides receipt of a license from the right holder only.
 */

package org.maxur.ldoc;


import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.sun.javadoc.PackageDoc;
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
 * The type Glossary.
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>07.08.2016</pre>
 */
public class Glossary {

    /**
     * Start boolean.
     *
     * @param root the root
     * @return the boolean
     */
    public static boolean start(final RootDoc root) {
        final Glossary glossary = new Glossary();
        final List<DomainModel> models = glossary.createModels(root);
        glossary.writeGlossary(models);
        return true;
    }

    /**
     * Create models list.
     *
     * @param root the root
     * @return the list
     */
    private List<DomainModel> createModels(final RootDoc root) {
        final PackageDoc[] packages = root.specifiedPackages();
        final List<DomainModel> result = new ArrayList<>();
        for (PackageDoc aPackage : packages) {
            Tag[] tags = aPackage.tags("boundedContext");
            if (tags.length > 0) {
                final DomainModel model = new DomainModel(aPackage.name());
                result.add(model);
                for (Tag tag : tags) {
                    model.addDescription(tag.text());
                }
                model.addClasses(aPackage.allClasses());
            }
        }
        return result;

    }

    /**
     * Write glossary.
     *
     * @param models the models
     */
    private void writeGlossary(final List<DomainModel> models) {
        for (DomainModel model : models) {

            try {
                final Handlebars handlebars = new Handlebars();
                final Path file = Paths.get(model.getName() + "-glossary.md");
                final Template template = handlebars.compile("glossary");
                byte[] bytes = template.apply(model).getBytes(Charset.forName("UTF-8"));
                Files.write(file, bytes);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
