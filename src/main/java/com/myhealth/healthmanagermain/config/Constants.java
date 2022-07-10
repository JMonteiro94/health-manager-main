package com.myhealth.healthmanagermain.config;

public final class Constants {

    // Spring profiles available
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_TEST = "test";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_.@A-Za-z0-9-]*$";
    public static final String HEADER_AUTH = "Authorization";

    public static final String ANONYMOUS_USER = "anonymoususer";

    private Constants() {
    }
}
