package org.eolang.sodg;

import com.jcabi.log.Logger;
import com.jcabi.xml.XML;
import com.yegor256.xsline.StLambda;
import com.yegor256.xsline.TrClasspath;
import com.yegor256.xsline.TrDefault;
import com.yegor256.xsline.TrEnvelope;
import com.yegor256.xsline.TrFast;
import com.yegor256.xsline.TrJoined;
import com.yegor256.xsline.TrLogged;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

final class TrFinish extends TrEnvelope {

    /**
     * Ctor.
     */
    TrFinish(final Level level) {
        super(new TrLogged(
            new TrFast(
                new TrJoined<>(
                    new TrClasspath<>(
                        "/org/eolang/maven/sodg-to/catch-lost-edges.xsl",
                        "/org/eolang/maven/sodg-to/catch-duplicate-vertices.xsl",
                        "/org/eolang/maven/sodg-to/catch-duplicate-edges.xsl",
                        "/org/eolang/maven/sodg-to/catch-singleton-greeks.xsl",
                        "/org/eolang/maven/sodg-to/catch-conflicting-greeks.xsl",
                        "/org/eolang/maven/sodg-to/catch-empty-edges.xsl"
                    ).back(),
                    new TrDefault<>(
                        new StLambda(
                            "graph-is-a-tree",
                            input -> {
                                final Set<String> seen = new HashSet<>();
                                TrFinish.traverse(input, "ν0", seen);
                                final List<String> ids = input.xpath("//v/@id");
                                if (ids.size() != seen.size()) {
                                    for (final String vid : ids) {
                                        if (!seen.contains(vid)) {
                                            Logger.error(
                                                Sodg.class,
                                                "Vertex is not in the tree: %s", vid
                                            );
                                        }
                                    }
                                    throw new IllegalStateException(
                                        String.format(
                                            "Not all vertices are in the tree, only %d out of %d, see log above",
                                            seen.size(), ids.size()
                                        )
                                    );
                                }
                                return input;
                            }
                        )
                    )
                ),
                Sodg.class
            ),
            Sodg.class,
            level
        ));
    }

    /**
     * Go through the graph recursively and visit all vertices.
     *
     * @param graph The XML graph
     * @param root The vertex to start from
     * @param seen List of <code>@id</code> attributes already seen
     */
    private static void traverse(
        final XML graph, final String root,
        final Set<String> seen
    ) {
        for (final XML edge : graph.nodes(String.format("//v[@id='%s']/e", root))) {
            final String kid = edge.xpath("@to").get(0);
            if (seen.contains(kid)) {
                continue;
            }
            seen.add(kid);
            TrFinish.traverse(graph, kid, seen);
        }
    }
}
