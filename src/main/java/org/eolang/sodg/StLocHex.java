package org.eolang.sodg;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.yegor256.xsline.Shift;
import java.util.List;
import java.util.function.Function;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Shift for turning all `@loc`s into hexes.
 * @since 0.0.2
 */
final class StLocHex implements Shift {

    /**
     * The XPath.
     */
    private final String xpath;

    /**
     * The mapping function.
     */
    private final Function<XML, Iterable<Directive>> fun;

    /**
     * Empty ctor.
     */
    StLocHex() {
        this("(//o[@name and @atom and not(@base) and @loc and not(@lambda)])[1]");
    }

    /**
     * Ctor.
     * @param xpth The XPath
     */
    StLocHex(final String xpth) {
        this(
            xpth,
            xml -> {
                final String loc = xml.xpath("@loc").get(0);
                return new Directives().attr(
                    "lambda",
                    new HexedUtf(
                        loc.substring(loc.indexOf('.') + 1)
                    ).asString()
                );
            }
        );
    }

    /**
     * Ctor.
     * @param xpth The XPath
     * @param fnc The mapping function
     */
    StLocHex(final String xpth, final Function<XML, Iterable<Directive>> fnc) {
        this.xpath = xpth;
        this.fun = fnc;
    }

    @Override
    public String uid() {
        return this.getClass().getSimpleName();
    }

    @Override
    public XML apply(final int position, final XML xml) {
        final List<XML> nodes = xml.nodes(this.xpath);
        final XML doc;
        if (nodes.isEmpty()) {
            doc = xml;
        } else {
            final Directives dirs = new Directives();
            final String path = String.format("(%s)[1]", this.xpath);
            for (final XML node : nodes) {
                dirs.xpath(path).strict(1).append(this.fun.apply(node));
            }
            doc = new XMLDocument(
                new Xembler(dirs).applyQuietly(xml.inner())
            );
        }
        return doc;
    }
}
