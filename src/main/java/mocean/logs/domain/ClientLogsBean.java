package mocean.logs.domain;

import java.io.Serializable;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

public class ClientLogsBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	/**
	 * 用户id(SN，激活码，用户名)
	 */
	private String userId;
	/**
	 * 发送日志时间戳
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private String sendTime;
	/**
	 * 当前F.TV版本
	 */
	private String apkVersion;
	/**
	 * 当前设备平台信息
	 */
	private String devicePlatform;
	/**
	 * 当前安卓系统版本
	 */
	private String systemVersion;
	/**
	 * 平台id
	 */
	private String pid;
	/**
	 * 型号id
	 */
	private String mid;
	/**
	 * 客户id
	 */
	private String cid;
	/**
	 * 当前Cpu总占用，单位：%
	 */
	private String totalCpu;
	/**
	 * 当前F.TV占用Cpu，单位：%
	 */
	private String ftvCpu;
	/**
	 * 设备总内存，单位：MB
	 */
	private String totalMemory;
	/**
	 * 设备剩余内存，单位：MB
	 */
	private String freeMemory;
	/**
	 * F.TV占用内存，单位：MB
	 */
	private String ftvMemory;
	/**
	 * 收到websocket通知后，ping最终播放地址的结果，ping10个数据包，只赋值一次，其他情况默认是空
	 */
	private String pingResult;
	/**
	 * 是否为异常信息
	 */
	private int isException;
	/**
	 * 已经播放过节目信息
	 */
	private List<ClientLogsProgramBean> program;
	/**
	 * 登录相关接口信息
	 */
	private List<ClientLogsLoginBean> login;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getApkVersion() {
		return apkVersion;
	}

	public void setApkVersion(String apkVersion) {
		this.apkVersion = apkVersion;
	}

	public String getDevicePlatform() {
		return devicePlatform;
	}

	public void setDevicePlatform(String devicePlatform) {
		this.devicePlatform = devicePlatform;
	}

	public String getSystemVersion() {
		return systemVersion;
	}

	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getTotalCpu() {
		return totalCpu;
	}

	public void setTotalCpu(String totalCpu) {
		this.totalCpu = totalCpu;
	}

	public String getFtvCpu() {
		return ftvCpu;
	}

	public void setFtvCpu(String ftvCpu) {
		this.ftvCpu = ftvCpu;
	}

	public String getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(String totalMemory) {
		this.totalMemory = totalMemory;
	}

	public String getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(String freeMemory) {
		this.freeMemory = freeMemory;
	}

	public String getFtvMemory() {
		return ftvMemory;
	}

	public void setFtvMemory(String ftvMemory) {
		this.ftvMemory = ftvMemory;
	}

	public int getIsException() {
		return isException;
	}

	public void setIsException(int isException) {
		this.isException = isException;
	}

	public String getPingResult() {
		return pingResult;
	}

	public void setPingResult(String pingResult) {
		this.pingResult = pingResult;
	}

	public List<ClientLogsProgramBean> getProgram() {
		return program;
	}

	public void setProgram(List<ClientLogsProgramBean> program) {
		this.program = program;
	}

	public List<ClientLogsLoginBean> getLogin() {
		return login;
	}

	public void setLogin(List<ClientLogsLoginBean> login) {
		this.login = login;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ClientLogsBean{" +
				"id=" + id +
				", userId='" + userId + '\'' +
				", sendTime='" + sendTime + '\'' +
				", apkVersion='" + apkVersion + '\'' +
				", devicePlatform='" + devicePlatform + '\'' +
				", systemVersion='" + systemVersion + '\'' +
				", pid='" + pid + '\'' +
				", mid='" + mid + '\'' +
				", cid='" + cid + '\'' +
				", totalCpu='" + totalCpu + '\'' +
				", ftvCpu='" + ftvCpu + '\'' +
				", totalMemory='" + totalMemory + '\'' +
				", freeMemory='" + freeMemory + '\'' +
				", ftvMemory='" + ftvMemory + '\'' +
				", pingResult='" + pingResult + '\'' +
				", isException=" + isException +
				", program=" + program +
				", login=" + login.toString() +
				'}';
	}
}
