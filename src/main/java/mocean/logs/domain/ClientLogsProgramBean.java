package mocean.logs.domain;

import java.io.Serializable;
import java.util.List;

public class ClientLogsProgramBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	/**
	 * 节目名称
	 */
	private String name;
	/**
	 * 客户端日志id
	 */
	private int logsid;
	/**
	 * 节目具体信息
	 */
	private List<VideoInfoBean> videoInfo;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
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
	public List<VideoInfoBean> getVideoInfo() {
		return videoInfo;
	}
	public void setVideoInfo(List<VideoInfoBean> videoInfo) {
		this.videoInfo = videoInfo;
	}
	
	
}
