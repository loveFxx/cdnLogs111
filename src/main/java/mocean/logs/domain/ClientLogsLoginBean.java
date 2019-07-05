package mocean.logs.domain;

import java.io.Serializable;

import org.springframework.format.annotation.DateTimeFormat;

public class ClientLogsLoginBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	/**
	 * 请求接口名称
	 */
	private String requestName;
	/**
	 * 请求Url
	 */
	private String url;
	/**
	 * 开始请求时间戳
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private String startTime;
	/**
	 * 请求结束时间戳
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private String endTime;
	/**
	 * 返回码
	 */
	private int returnCode;
	/**
	 * 客户端日志id
	 */
	private int logsid;
	public String getRequestName() {
		return requestName;
	}
	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}
	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "ClientLogsLoginBean{" +
				"id=" + id +
				", requestName='" + requestName + '\'' +
				", url='" + url + '\'' +
				", startTime='" + startTime + '\'' +
				", endTime='" + endTime + '\'' +
				", returnCode=" + returnCode +
				", logsid=" + logsid +
				'}';
	}

	public void setId(int id) {
		this.id = id;
	}
	public int getLogsid() {
		return logsid;
	}
	public void setLogsid(int logsid) {
		this.logsid = logsid;
	}
	

}
