package com.krish.automessaging.configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * The Class SpringMailConfiguration.
 *
 * Configure the {@link JavaMailSender}
 */
@Configuration
public class SpringMailConfiguration {

    /** The email username. */
    @Value("${email.username}")
    private String emailUsername;

    /** The email password. */
    @Value("${email.password}")
    private String emailPassword;

    /** The email host. */
    @Value("${email.host}")
    private String emailHost;

    /** The email port. */
    @Value("${email.port}")
    private Integer emailPort;

    /** The is use TLS. */
    @Value("${email.useTLS}")
    private boolean isUseTLS;

    /**
     * Java mail sender.
     *
     * @return the java mail sender
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Bean
    public JavaMailSender javaMailSender() throws IOException {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setUsername(emailUsername);
        mailSender.setPassword(emailPassword);
        mailSender.setHost(emailHost);
        mailSender.setPort(emailPort);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", isUseTLS);
        if (isUseTLS) {
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        }
        return mailSender;
    }

    /**
     * Email message source.
     *
     * @return the resource bundle message source
     */
    @Bean
    public ResourceBundleMessageSource emailMessageSource() {
        final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("mail/MailMessages");
        return messageSource;
    }

    /**
     * Email template engine.
     *
     * @return the template engine
     */
    @Bean
    public TemplateEngine emailTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(textTemplateResolver());
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        templateEngine.addTemplateResolver(stringTemplateResolver());
        return templateEngine;
    }

    /**
     * Text template resolver.
     *
     * @return the i template resolver
     */
    private ITemplateResolver textTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(1));
        templateResolver.setResolvablePatterns(Collections.singleton("text/*"));
        templateResolver.setPrefix("/mail/");
        templateResolver.setSuffix(".txt");
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    /**
     * Html template resolver.
     *
     * @return the i template resolver
     */
    private ITemplateResolver htmlTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(2));
        templateResolver.setResolvablePatterns(Collections.singleton("html/*"));
        templateResolver.setPrefix("/mail/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    /**
     * String template resolver.
     *
     * @return the i template resolver
     */
    private ITemplateResolver stringTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(3));
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        templateResolver.setCacheable(false);
        return templateResolver;
    }
}
