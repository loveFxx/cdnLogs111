package mocean.logs.util;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

public class PageBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int start;
	private int end;
	private String sort;
	private String order;
	
	private List<String> userIds;
	private String sn;
	private String server_address;
	private String address;
	private String channel;
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private String startTime;
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private String endTime;
	private String sn_filter;
	private String idStr;
	private String pid;
	private String cid;
	private String mid;
	private String note;
	private String connect_status;
	private int totalSize;

	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public List<String> getUserIds() {
		return userIds;
	}
	public void setUserIds(List<String> ids) {
		this.userIds = ids;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getServer_address() {
		return server_address;
	}
	public void setServer_address(String server_address) {
		this.server_address = server_address;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getStartTime() {
		if("0".equals(startTime)) {
			return null;
		}
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		if("0".equals(endTime)) {
			return null;
		}
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getSn_filter() {
		return sn_filter;
	}


	@Override
	public String toString() {
		return "PageBean{" +
				"start=" + start +
				", end=" + end +
				", sort='" + sort + '\'' +
				", order='" + order + '\'' +
				", userIds=" + userIds +
				", sn='" + sn + '\'' +
				", server_address='" + server_address + '\'' +
				", address='" + address + '\'' +
				", channel='" + channel + '\'' +
				", startTime='" + startTime + '\'' +
				", endTime='" + endTime + '\'' +
				", sn_filter=" + sn_filter +
				", idStr='" + idStr + '\'' +
				", pid='" + pid + '\'' +
				", cid='" + cid + '\'' +
				", mid='" + mid + '\'' +
				", note='" + note + '\'' +
				", connect_status=" + connect_status +
				", totalSize=" + totalSize +
				'}';
	}

	public void setSn_filter(String sn_filter) {
		this.sn_filter = sn_filter;
	}
	public String getIdStr() {
		return idStr;
	}
	public void setIdStr(String idStr) {
		this.idStr = idStr;
	}
	public String getPid() {
		if(StringUtils.isNotBlank(idStr)&&idStr.split(",").length>2) {
			return idStr.split(",")[0];
		}
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getCid() {
		if(StringUtils.isNotBlank(idStr)&&idStr.split(",").length>2) {
			return idStr.split(",")[2];
		}
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getMid() {
		if(StringUtils.isNotBlank(idStr)&&idStr.split(",").length>2) {
			return idStr.split(",")[1];
		}
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getConnect_status() {
		return connect_status;
	}
	public void setConnect_status(String connect_status) {
		this.connect_status = connect_status;
	}
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
	public boolean isCdnEmpty() {
		return StringUtils.isBlank(sn)&&StringUtils.isBlank(server_address)&&StringUtils.isBlank(address)&&StringUtils.isBlank(channel)&&connect_status.equals( "-1" )&&Integer.valueOf(note)==-1&&sn_filter.equals( "" );
	}
}
