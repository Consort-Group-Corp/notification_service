package uz.consortgroup.notification_service.event;

import uz.consortgroup.notification_service.entity.Language;

import java.util.Locale;

public interface Content {
    Locale getLocale();

    default Locale resolveLocale(Language language, Locale fallback) {
        if (language != null) {
            return new Locale(language.getCode());
        }
        return fallback != null ? fallback : Locale.ENGLISH;
    }
}
