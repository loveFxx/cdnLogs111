package mocean.logs.domain;

import java.io.Serializable;

public class CdnChannelBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String callsign;
    private String country;
    private String name;

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
