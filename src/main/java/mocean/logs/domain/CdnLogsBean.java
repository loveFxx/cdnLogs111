package mocean.logs.domain;

import java.io.Serializable;

import org.springframework.format.annotation.DateTimeFormat;

public class CdnLogsBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	/**
	 * 服务器ip
	 */
	private String server_address;
	/**
	 * 客户端播放协议，HTTP/RUDP/M3U8
	 */
	private String protocol;
	/**
	 * 客户端播放地址，IP:PORT
	 */
	private String address;
	/**
	 * 客户端播放的节目唯一标识
	 */
	private String channel_id;
	/**
	 * 客户端播放的Authinfo
	 */
	private String authinfo;
	/**
	 * 客户端的SN
	 */
	private String sn;
	/**
	 * 备注信息
	 */
	private String note;
	/**
	 * 客户端请求头UserAgent信息
	 */
	private String user_agent;
	/**
	 * 服务器给客户端的响应码
	 */
	private String response_code;
	/**int
	 * 连接状态，0表示开始连接，1表示推流中，2表示连接关闭
	 */
	private String connect_status;
	/**int
	 * 客户端连上服务器的时间戳
	 */
	private int connect_time;
	//int

	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private String connect_Times;
	/**
	 * 给客户端推流时长，单位：秒
	 */
	private String stream_duration;
	/**
	 * 给客户端推流的的速度【待实现】，单位：b/s
	 */
	private String stream_speed;
	/**
	 * 给客户端推流总数据大小，单位：b
	 */
	private String stream_total_len;
	/**
	 * 给客户端推流堆积的数据大小，单位：b
	 */
	private String stream_lose_len;
	/**
	 * 当前时间戳
	 */
	private int current_time;
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private String current_Times;

	public String getServer_address() {
		return server_address;
	}

	public void setServer_address(String server_address) {
		this.server_address = server_address;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}

	public String getAuthinfo() {
		return authinfo;
	}

	public void setAuthinfo(String authinfo) {
		this.authinfo = authinfo;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public int getConnect_time() {
		return connect_time;
	}

	public void setConnect_time(int connect_time) {
		this.connect_time = connect_time;
	}

	public String getStream_duration() {
		return stream_duration;
	}

	public void setStream_duration(String stream_duration) {
		this.stream_duration = stream_duration;
	}

	public String getStream_speed() {
		return stream_speed;
	}

	public void setStream_speed(String stream_speed) {
		this.stream_speed = stream_speed;
	}

	public String getStream_total_len() {
		return stream_total_len;
	}

	public void setStream_total_len(String stream_total_len) {
		this.stream_total_len = stream_total_len;
	}

	public String getStream_lose_len() {
		return stream_lose_len;
	}

	public void setStream_lose_len(String stream_lose_len) {
		this.stream_lose_len = stream_lose_len;
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

	public String getResponse_code() {
		return response_code;
	}

	public void setResponse_code(String response_code) {
		this.response_code = response_code;
	}

	public int getCurrent_time() {
		return current_time;
	}

	public void setCurrent_time(int current_time) {
		this.current_time = current_time;
	}

	public String getConnect_Times() {
		return connect_Times;
	}

	public void setConnect_Times(String connect_Times) {
		this.connect_Times = connect_Times;
	}

	public String getCurrent_Times() {
		return current_Times;
	}

	public void setCurrent_Times(String current_Times) {
		this.current_Times = current_Times;
	}

	public String getConnect_status() {
		return connect_status;
	}

	public void setConnect_status(String connect_status) {
		this.connect_status = connect_status;
	}
	
}
