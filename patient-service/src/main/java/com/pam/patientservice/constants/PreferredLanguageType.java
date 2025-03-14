package com.pam.patientservice.constants;
public enum PreferredLanguageType {
    ENGLISH("en_US"),
    SPANISH("es_ES"),
    FRENCH("fr_FR"),
    CHINESE("zh_CN"),
    JAPANESE("ja_JP"),
    KOREAN("ko_KR"),
    GERMAN("de_DE"),
    ITALIAN("it_IT"),
    RUSSIAN("ru_RU");

    private final String language;

    PreferredLanguageType(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public PreferredLanguageType fromString(String type) {

        try {
            return valueOf(type.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unrecognized type" + type);
        }
    }
}
