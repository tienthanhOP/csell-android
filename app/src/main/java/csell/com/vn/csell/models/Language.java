package csell.com.vn.csell.models;

public class Language {
    private String nameLanguage;
    private String isoCodeCountry;
    public boolean isCheck;

    public Language(String nameLanguage, String isoCodeCountry) {
        this.nameLanguage = nameLanguage;
        this.isoCodeCountry = isoCodeCountry;
    }

    public String getNameLanguage() {
        return nameLanguage;
    }

    public void setNameLanguage(String nameLanguage) {
        this.nameLanguage = nameLanguage;
    }

    public String getIsoCodeCountry() {
        return isoCodeCountry;
    }

    public void setIsoCodeCountry(String isoCodeCountry) {
        this.isoCodeCountry = isoCodeCountry;
    }
}
