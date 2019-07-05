package mocean.logs.domain;

import org.springframework.format.annotation.DateTimeFormat;

public class Audrating {

    private static final long serialVersionUID = 1L;
    /**
     * 客户端播放的节目唯一标识
     */
    private String channel_id;

    /**
     * 客户端的SN
     */
    private String sn;

    private int count;//相同的sn

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    private int num;//查询时间不同sn

    private String country;
    private  String name;


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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /**int
     * 连接状态，0表示开始连接，1表示推流中，2表示连接关闭
     */
    private String connect_status;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private String connect_Times;

    private String stream_duration;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private String current_Times;

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getConnect_status() {
        return connect_status;
    }

    public void setConnect_status(String connect_status) {
        this.connect_status = connect_status;
    }

    public String getConnect_Times() {
        return connect_Times;
    }

    public void setConnect_Times(String connect_Times) {
        this.connect_Times = connect_Times;
    }

    public String getStream_duration() {
        return stream_duration;
    }

    public void setStream_duration(String stream_duration) {
        this.stream_duration = stream_duration;
    }

    public String getCurrent_Times() {
        return current_Times;
    }

    public void setCurrent_Times(String current_Times) {
        this.current_Times = current_Times;
    }
}
