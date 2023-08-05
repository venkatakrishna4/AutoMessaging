package com.krish.automessaging.datamodel.record;

import org.thymeleaf.context.Context;

import jakarta.annotation.Nonnull;

/**
 * To string.
 *
 * @return the java.lang. string
 */
public record EmailOptionsRecord(@Nonnull String fromEmail, @Nonnull String toEmail, String toName,
        @Nonnull String type, @Nonnull String subject, String message, String fileName, Context context,
        String imageResourceName) {

    /** The Constant TYPE_TEXT. */
    public static final String TYPE_TEXT = "text";

    /** The Constant TYPE_HTML. */
    public static final String TYPE_HTML = "html";

    /** The Constant TYPE_HTML_IMAGE. */
    public static final String TYPE_HTML_IMAGE = "html_image";
}
