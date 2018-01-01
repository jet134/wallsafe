package fi.kennyhei.wallsafe.config;

import java.util.prefs.Preferences;

public class Filters {

    // Filter settings (SFW, Sketchy, NSFW)
    private static boolean isGeneral;
    private static boolean isAnime;
    private static boolean isPeople;
    private static boolean isSFW;
    private static boolean isSketchy;
    private static boolean isNSFW;

    public static void setup(Preferences preferences) {

        isGeneral = Boolean.parseBoolean(preferences.get(Option.WS_IS_GENERAL, "true"));
        isAnime = Boolean.parseBoolean(preferences.get(Option.WS_IS_ANIME, "false"));
        isPeople = Boolean.parseBoolean(preferences.get(Option.WS_IS_PEOPLE, "true"));
        isSFW = Boolean.parseBoolean(preferences.get(Option.WS_IS_SFW, "true"));
        isSketchy = Boolean.parseBoolean(preferences.get(Option.WS_IS_SKETCHY, "false"));
        isNSFW = Boolean.parseBoolean(preferences.get(Option.WS_IS_NSFW, "false"));
    }

    public static boolean isGeneral() {

        return isGeneral;
    }

    public static void isGeneral(boolean isGeneral) {

        Filters.isGeneral = isGeneral;
    }

    public static boolean isAnime() {

        return isAnime;
    }

    public static void isAnime(boolean isAnime) {

        Filters.isAnime = isAnime;
    }

    public static boolean isPeople() {

        return isPeople;
    }

    public static void isPeople(boolean isPeople) {

        Filters.isPeople = isPeople;
    }

    public static boolean isSFW() {

        return isSFW;
    }

    public static void isSFW(boolean isSFW) {

        Filters.isSFW = isSFW;
    }

    public static boolean isSketchy() {

        return isSketchy;
    }

    public static void isSketchy(boolean isSketchy) {

        Filters.isSketchy = isSketchy;
    }

    public static boolean isNSFW() {

        return isNSFW;
    }

    public static void isNSFW(boolean isNSFW) {

        Filters.isNSFW = isNSFW;
    }
}
