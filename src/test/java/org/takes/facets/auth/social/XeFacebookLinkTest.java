/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.social;

import com.jcabi.matchers.XhtmlMatchers;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.xe.RsXembly;
import org.takes.rs.xe.XeAppend;

/**
 * Test case for {@link XeFacebookLink}.
 * @since 0.5
 */
final class XeFacebookLinkTest {

    @Test
    void generatesCorrectLink() throws IOException {
        MatcherAssert.assertThat(
            "Generated XML must contain Facebook link",
            IOUtils.toString(
                new RsXembly(
                    new XeAppend(
                        "root",
                        new XeFacebookLink(new RqFake(), "abcdef")
                    )
                ).body(),
                StandardCharsets.UTF_8
            ),
            XhtmlMatchers.hasXPaths(
                "/root/links/link[@rel='takes:facebook']"
            )
        );
    }

}
