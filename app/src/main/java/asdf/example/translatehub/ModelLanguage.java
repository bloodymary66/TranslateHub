package asdf.example.translatehub;

public class ModelLanguage {


    String LanguageCode;
    String LanguageTitle;

    public ModelLanguage(String languageCode, String languageTitle) {
        LanguageCode = languageCode;
        LanguageTitle = languageTitle;
    }


    public String getLanguageCode() {
        return LanguageCode;
    }

    public void setLanguageCode(String languageCode) {
        LanguageCode = languageCode;
    }

    public String getLanguageTitle() {
        return LanguageTitle;
    }

    public void setLanguageTitle(String languageTitle) {
        LanguageTitle = languageTitle;
    }
}
