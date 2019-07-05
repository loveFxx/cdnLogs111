package mocean.logs.domain;

import java.io.Serializable;

public class GslbLogsBean implements Serializable {
    private static final long serialVersionUID = 3L;

    private String server;
    private String protocol;
    private String address;
    private String note;
    private String user_agent;
    private String redirect_url;
    private String current_time;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }

    public String getRedirect_url() {
        return redirect_url;
    }

    public void setRedirect_url(String redirect_url) {
        this.redirect_url = redirect_url;
    }

    public String getCurrent_time() {
        return current_time;
    }

    public void setCurrent_time(String current_time) {
        this.current_time = current_time;
    }

    @Override
    public String toString() {
        return "GslbLogsBean{" +
                "server='" + server + '\'' +
                ", protocol='" + protocol + '\'' +
                ", address='" + address + '\'' +
                ", note='" + note + '\'' +
                ", user_agent='" + user_agent + '\'' +
                ", redirect_url='" + redirect_url + '\'' +
                ", current_time=" + current_time +
                '}';
    }
}
