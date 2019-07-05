package mocean.logs.domain;

import java.io.Serializable;

import org.springframework.format.annotation.DateTimeFormat;

public class VideoInfoBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	/**
	 * 播放链接
	 */
	private String playurl;
	/**
	 * CDN地址
	 */
	private String cdnAddress;
	/**
	 * 当前网速，单位：kb/s
	 */
	private String speed;
	/**
	 * 分辨率
	 */
	private String resolution;
	/**
	 * 视频格式
	 */
	private String videoFormat;
	/**
	 * 音频格式
	 */
	private String audioFormat;
	/**
	 * 开始切台时间戳
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private String startSwitchTime;
	/**
	 * 开始SetDataSource时间戳
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private String startSetDataSourceTime;
	/**
	 * 开始播放时间戳
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private String startPlayTime;
	/**
	 * 播放错误状态码
	 */
	private String playErrorCode;
	/**
	 * 节目id
	 */
	private int programId;
	public String getPlayurl() {
		return playurl;
	}
	public void setPlayurl(String playurl) {
		this.playurl = playurl;
	}
	public String getCdnAddress() {
		return cdnAddress;
	}
	public void setCdnAddress(String cdnAddress) {
		this.cdnAddress = cdnAddress;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getVideoFormat() {
		return videoFormat;
	}
	public void setVideoFormat(String videoFormat) {
		this.videoFormat = videoFormat;
	}
	public String getAudioFormat() {
		return audioFormat;
	}
	public void setAudioFormat(String audioFormat) {
		this.audioFormat = audioFormat;
	}
	public String getStartSwitchTime() {
		return startSwitchTime;
	}
	public void setStartSwitchTime(String startSwitchTime) {
		this.startSwitchTime = startSwitchTime;
	}
	public String getStartSetDataSourceTime() {
		return startSetDataSourceTime;
	}
	public void setStartSetDataSourceTime(String startSetDataSourceTime) {
		this.startSetDataSourceTime = startSetDataSourceTime;
	}
	public String getStartPlayTime() {
		return startPlayTime;
	}
	public void setStartPlayTime(String startPlayTime) {
		this.startPlayTime = startPlayTime;
	}
	public String getPlayErrorCode() {
		return playErrorCode;
	}
	public void setPlayErrorCode(String playErrorCode) {
		this.playErrorCode = playErrorCode;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProgramId() {
		return programId;
	}

	@Override
	public String toString() {
		return  playurl  +
				"," + cdnAddress +
				"," + speed +
				"," + resolution +
				"," + videoFormat +
				"," + audioFormat +
				"," + startSwitchTime +
				"," + startSetDataSourceTime +
				"," + startPlayTime +
				"," + playErrorCode +
				"," + programId
				;
	}

	public void setProgramId(int programId) {
		this.programId = programId;
	}

}
