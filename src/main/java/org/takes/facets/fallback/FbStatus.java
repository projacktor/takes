/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fallback;

import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.cactoos.Scalar;
import org.cactoos.iterable.Filtered;
import org.cactoos.list.ListOf;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Opt;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWithType;
import org.takes.tk.TkFixed;

/**
 * Fallback on status code that equals to the provided value.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.13
 */
@EqualsAndHashCode(callSuper = true)
public final class FbStatus extends FbWrap {

    /**
     * Whitespace pattern, used for splitting.
     */
    private static final Pattern WHITESPACE = Pattern.compile("\\s");

    /**
     * Ctor.
     * @param code HTTP status code
     * @since 0.16.10
     */
    public FbStatus(final int code) {
        this(new Filtered<>(value -> code == value.intValue(), code));
    }

    /**
     * Ctor.
     * @param check HTTP status code predicate
     * @since 0.16.10
     */
    public FbStatus(final Iterable<Integer> check) {
        this(check, req -> {
            final Response res = new RsWithStatus(req.code());
            return new Opt.Single<>(
                new RsWithType(
                    new RsWithBody(
                        res,
                        String.format(
                            "%s: %s", FbStatus.WHITESPACE.split(
                                res.head().iterator().next(),
                                2
                            )[1], req.throwable().getLocalizedMessage()
                        )
                    ), "text/plain"
                )
            );
        });
    }

    /**
     * Ctor.
     * @param code HTTP status code
     * @param response Response
     * @since 0.14
     */
    public FbStatus(final int code, final Response response) {
        this(code, new TkFixed(response));
    }

    /**
     * Ctor.
     * @param code HTTP status code
     * @param take Take
     */
    public FbStatus(final int code, final Take take) {
        this(
            code,
            (Fallback) req -> new Opt.Single<>(take.act(req))
        );
    }

    /**
     * Ctor.
     * @param code HTTP status code
     * @param fallback Fallback
     */
    public FbStatus(final int code, final Fallback fallback) {
        this(
            new Filtered<>(status -> code == status.intValue(), code),
            fallback
        );
    }

    /**
     * Ctor.
     * @param code HTTP status code
     * @param fallback Fallback
     */
    public FbStatus(final int code, final Scalar<Fallback> fallback) {
        this(
            new Filtered<>(status -> code == status.intValue(), code),
            fallback
        );
    }

    /**
     * Ctor.
     * @param check Check
     * @param fallback Fallback
     */
    public FbStatus(final Iterable<Integer> check, final Fallback fallback) {
        this(
            check,
            () -> fallback
        );
    }

    /**
     * Ctor.
     * @param check Check
     * @param fallback Fallback
     */
    @SuppressWarnings(
        {
            "PMD.CallSuperInConstructor",
            "PMD.ConstructorOnlyInitializesOrCallOtherConstructors"
        }
    )
    public FbStatus(final Iterable<Integer> check,
        final Scalar<Fallback> fallback) {
        super(
            req -> {
                Opt<Response> rsp = new Opt.Empty<>();
                if (new ListOf<>(check).contains(req.code())) {
                    rsp = fallback.value().route(req);
                }
                return rsp;
            }
        );
    }
}
