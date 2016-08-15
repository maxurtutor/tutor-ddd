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
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private List<ConceptModel> concepts;

    /**
     * Instantiates a new Domain model.
     *
     */
    private GlossaryModel(final PackageDoc doc, final AnnotationDesc desc) {
        final String[] strings = doc.name().split("\\.");
        this.name = capitalize(strings[strings.length - 1]);

        for (AnnotationDesc.ElementValuePair member : desc.elementValues()) {
            switch (member.element().name()) {
                case "name":
                    this.title = getString(member);
                    break;
                case "description":
                    this.description = getString(member);
                    break;
                default:
            }
        }

        this.concepts = Arrays.stream(doc.allClasses())
            .map(ConceptModel::makeBy)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    /**
     * Make by optional.
     *
     * @param aPackage the a package
     * @return the optional
     */
    static Optional<GlossaryModel> makeBy(final PackageDoc aPackage) {
        final List<AnnotationDesc> types = Arrays.stream(aPackage.annotations())
            .filter(ad -> isAnnotatedAsBoundedContext(ad.annotationType()))
            .collect(Collectors.toList());

        switch (types.size()) {
            case 0: return Optional.empty();
            case 1: return Optional.of(new GlossaryModel(aPackage, types.get(0)));
            default:
                throw new IllegalStateException("There are more than one BoundedContext annotations");
        }

    }

    private static boolean isAnnotatedAsBoundedContext(final AnnotationTypeDoc annotationType) {
        return BusinessDomain.class.getCanonicalName().equals(annotationType.qualifiedTypeName());
    }

    private static String getString(AnnotationDesc.ElementValuePair member) {
        return member.value().value().toString();
    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1).toLowerCase();
    }

    /**
     * The type Concept model.
     */
    public static class ConceptModel {

        private final String name;
        private String title;
        private String description;


        private ConceptModel(final ClassDoc doc, final AnnotationDesc desk) {
            this.name = doc.simpleTypeName();
            for (AnnotationDesc.ElementValuePair member : desk.elementValues()) {
                switch (member.element().name()) {
                    case "name":
                        this.title = getString(member);
                        break;
                    case "description":
                        this.description = getString(member);
                        break;
                    default:
                }
            }
        }

        static Optional<ConceptModel> makeBy(final ClassDoc doc) {
            final Optional<AnnotationDesc> desc = conceptAnnotation(doc);
            return desc.isPresent() ?
                Optional.of(new ConceptModel(doc, desc.get())) :
                Optional.empty();
        }

        /**
         * Gets title.
         *
         * @return the title
         */
        public String getTitle() {
            return title;
        }

        /**
         * Gets name.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets description.
         *
         * @return the description
         */
        public String getDescription() {
            return description;
        }


        private static Optional<AnnotationDesc> conceptAnnotation(final ProgramElementDoc doc) {
            return Arrays.stream(doc.annotations())
                    .filter(d -> isAnnotatedAsConcept(d.annotationType()))
                    .findFirst();
        }

        private static boolean isAnnotatedAsConcept(final AnnotationTypeDoc annotationType) {
            return Concept.class.getCanonicalName().equals(annotationType.qualifiedTypeName());
        }


    }
}
