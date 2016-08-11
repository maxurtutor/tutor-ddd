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

import com.sun.javadoc.ClassDoc;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Domain model.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>8/11/2016</pre>
 */
public class DomainModel {

    private final List<String> description = new ArrayList<>();

    private final String name;

    private List<Class> classes;


    /**
     * Instantiates a new Domain model.
     *
     * @param name the name
     */
    public DomainModel(final String name) {
        String[] strings = name.split("\\.");
        this.name = capitalize(strings[strings.length - 1]);
    }

    /**
     * Glossary list.
     *
     * @return the list
     */
    public List<String> glossary() {
        final List<String> result = new ArrayList<>();
        result.add("#Словарь");
        result.add("");
        result.add(String.format("##Контекст: %s (*%s*)", description(), name));
        result.add("");
        for (Class aClass : classes) {
            Concept annotation = (Concept) aClass.getAnnotation(Concept.class);
            result.add(
                String.format("1. **%s**(*%s*) - %s ",
                    annotation.name(),
                    aClass.getSimpleName(),
                    annotation.description()
                )
            );
        }
        return result;
    }

    /**
     * Name string.
     *
     * @return the string
     */
    public String name() {
        return name;
    }

    /**
     * Add description.
     *
     * @param text the text
     */
    public void addDescription(final String text) {
        description.add(text);
    }

    /**
     * Description string.
     *
     * @return the string
     */
    public String description() {
        return description.stream().collect(Collectors.joining("\n"));
    }


    /**
     * Add docs.
     *
     * @param docs the class docs
     */
    public void addClasses(final ClassDoc[] docs) {
        this.classes = Arrays.stream(docs)
            .map(this::classByDoc)
            .filter(c -> c.isAnnotationPresent(Concept.class))
            .collect(Collectors.toList());
    }

    @SneakyThrows
    private Class<?> classByDoc(final ClassDoc doc)  {
        final String className = doc.name();
        System.out.println(className);
        final String packageName = doc.containingPackage().name();
        System.out.println(packageName);
        return Class.forName(packageName + "." +className);
    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1).toLowerCase();
    }

}
