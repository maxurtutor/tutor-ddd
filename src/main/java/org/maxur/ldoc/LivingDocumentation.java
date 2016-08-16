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

import com.github.jabbalaci.graphviz.GraphViz;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.sun.javadoc.RootDoc;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * The type Living Documentation doclet.
 *
 * @author myunusov
 * @version 1.0
 * @since <pre>07.08.2016</pre>
 */
@Slf4j
public class LivingDocumentation {

    /**
     * Start boolean.
     *
     * @param root the root
     * @return the boolean
     */
    public static boolean start(final RootDoc root) {
        final LivingDocumentation livingDocumentation = new LivingDocumentation();
        livingDocumentation.writeGlossary(root);
        livingDocumentation.drawContextMap(root);
        return true;
    }

    private void drawContextMap(RootDoc root) {
        final GraphViz gv = new GraphViz();
        gv.setImageDpi(GraphViz.DpiSizes.DPI_249);

        final Map<String, Map<String, GlossaryModel.LinkModel>> linkModelMap = new HashMap<>();

        gv.startGraph("ContextMap");
        for (GlossaryModel model : glossaryModels(root)) {
            gv.node(model.getId(), model.getTitle());
            for (GlossaryModel.LinkModel link : model.getLinks()) {
                final  Map<String, GlossaryModel.LinkModel> map =
                    linkModelMap.computeIfAbsent(model.getId(), id -> new HashMap<>());
                map.put(link.getRelated(), link);
            }
        }

        for (String downLink : linkModelMap.keySet()) {
            Map<String, GlossaryModel.LinkModel> map = linkModelMap.get(downLink);

            for (Map.Entry<String, GlossaryModel.LinkModel> entry : map.entrySet()) {

                final List<String> attr = new ArrayList<>();
                final List<String> labels = new ArrayList<>();

                final String label = entry.getValue().getLabel();
                if (!label.isEmpty()) {
                    labels.add(label);
                }

                final String upLink = entry.getKey();
                if (linkModelMap.get(upLink).keySet().contains(downLink)) {
                    final String label2 = linkModelMap.get(upLink).get(downLink).getLabel();
                    if (!label2.isEmpty()) {
                        labels.add(label2);
                    }
                    attr.add("[dir=\"both\"]");
                    linkModelMap.get(upLink).remove(downLink);
                }

                attr.add(format("[label=\"%s\"]", labels.stream().collect(Collectors.joining("/"))));

                gv.addln(format("%s -> %s %s;",
                    downLink,
                    upLink,
                    attr.stream().collect(Collectors.joining()))
                );
            }
        }

        gv.endGraph();
        log.debug(gv.source());

        gv.writeTo("contextMap");
    }

    /**
     * Write glossary.
     *
     */
    private void writeGlossary(final RootDoc root) {
        for (GlossaryModel model : glossaryModels(root)) {
            try {
                final Handlebars handlebars = new Handlebars();
                final Path file = Paths.get(model.getName() + "-glossary.md");
                final Template template = handlebars.compile("glossary");
                byte[] bytes = template.apply(model).getBytes(Charset.forName("UTF-8"));
                Files.write(file, bytes);

            } catch (IOException e) {
                log.debug(e.getMessage(), e);
            }
        }

    }

    /**
     * Create models list.
     *
     * @param root the root
     * @return the list
     */
    private List<GlossaryModel> glossaryModels(final RootDoc root) {
        return Arrays.stream(root.specifiedPackages())
            .map(GlossaryModel::makeBy)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }


}
