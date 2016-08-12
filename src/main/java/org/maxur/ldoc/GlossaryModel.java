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

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.tools.javadoc.ClassDocImpl;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Domain model.
 *
 * @author Maxim Yunusov
 * @version 1.0
 * @since <pre>8/11/2016</pre>
 */
public class GlossaryModel {

    @Getter
    private final String name;

    @Getter
    private String description;

    @Getter
    private String title;

    @Getter
    private Stream<ConceptModel> concepts;

    /**
     * Instantiates a new Domain model.
     *
     * @param name the name
     */
    public GlossaryModel(final String name) {
        String[] strings = name.split("\\.");
        this.name = capitalize(strings[strings.length - 1]);
    }

    public static Optional<GlossaryModel> makeBy(final PackageDoc aPackage) {
        final List<AnnotationDesc> types = Arrays.stream(aPackage.annotations())
            .filter(ad -> "BoundedContext".equals(ad.annotationType().name()))
            .collect(Collectors.toList());

        switch (types.size()) {
            case 0: return Optional.empty();
            case 1: return Optional.of(makeBy(types.get(0), aPackage));
            default:
                throw new IllegalStateException("There are more than one BoundedContext annotations");
        }

    }

    private static GlossaryModel makeBy(final AnnotationDesc desc, final PackageDoc aPackage) {
        AnnotationDesc.ElementValuePair [] members = desc.elementValues();
        final GlossaryModel model = new GlossaryModel(aPackage.name());
        for (AnnotationDesc.ElementValuePair member : members) {
            switch (member.element().name()) {
                case "name":
                    model.title = getString(member);
                    break;
                case "description":
                    model.description = getString(member);
                    break;
                default:
            }
        }

        model.concepts = Arrays.stream(aPackage.allClasses())
            .map(model::classByDoc)
            .filter(c -> c.isAnnotationPresent(Concept.class))
            .map(ConceptModel::new);

        return model;
    }

    private static String getString(AnnotationDesc.ElementValuePair member) {
        return member.value().value().toString();
    }

    private Class<?> classByDoc(final ClassDoc doc) {
        return ((ClassDocImpl) doc).type.getClass();
    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1).toLowerCase();
    }

    public static class ConceptModel {

        private final String title;
        private final String name;
        private final String description;

        ConceptModel(final Class<?> aClass) {
            final Concept annotation = aClass.getAnnotation(Concept.class);
            this.title = annotation.name();
            this.name = aClass.getSimpleName();
            this.description = annotation.description();
        }

        public String getTitle() {
            return title;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }
}
