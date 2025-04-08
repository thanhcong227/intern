package viettelsoftware.intern.config.locale;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import viettelsoftware.intern.config.properties.AppConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Configuration
public class CustomLocaleResolver extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {

    private final List<Locale> locales = new ArrayList<>();
    private final Locale defaultLanguage;

    public CustomLocaleResolver(AppConfigurationProperties appConfigurationProperties) {
        appConfigurationProperties.getLocaleResolverLanguages().forEach(s -> this.locales.add(Locale.of(s)));
        this.defaultLanguage = Locale.of(appConfigurationProperties.getDefaultLanguage());
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String headerLang = request.getHeader("Accept-Language");
        return headerLang != null && !headerLang.isEmpty() ? Locale.lookup(Locale.LanguageRange.parse(headerLang), this.locales) : this.defaultLanguage;
    }

    @Bean
    @Primary
    public LocaleResolver localeResolver() {
        return this;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
        rs.setBasename("messages");
        rs.setDefaultEncoding("UTF-8");
        rs.setUseCodeAsDefaultMessage(true);
        return rs;
    }
}
