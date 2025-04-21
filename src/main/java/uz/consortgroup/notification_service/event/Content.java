package uz.consortgroup.notification_service.event;

import uz.consortgroup.notification_service.entity.enumeration.Language;

import java.util.Locale;

public interface Content {
   default Locale getLocale() {
       return Locale.ENGLISH;
   }

    default Locale resolveLocale(Language language, Locale fallback) {
        if (language != null) {
            return Locale.forLanguageTag(language.getCode());
        }
        return fallback != null ? fallback : Locale.ENGLISH;
    }
}
