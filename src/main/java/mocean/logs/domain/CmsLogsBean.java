package mocean.logs.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsLogsBean implements Serializable, Comparable<CmsLogsBean> {

	/**
	 * 
	 */
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 1L;
	private String date;
	private String source;
	private String message;
	private String sn;
	private String interFace;
	private String host;
	private String decrypt;
	private String clientIp;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getInterFace() {
		return interFace;
	}

	public void setInterFace(String interFace) {
		this.interFace = interFace;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getDecrypt() {
		return decrypt;
	}

	public void setDecrypt(String decrypt) {
		this.decrypt = decrypt;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	@Override
	public int compareTo(CmsLogsBean o) {
		SimpleDateFormat df = new SimpleDateFormat("MMMM dd'th' yyyy, HH:mm:ss.SSS",Locale.ENGLISH);
		try {
			Date dt1 = df.parse(this.date);
			Date dt2 = df.parse(o.date);
			if (dt1.getTime() > dt2.getTime()) {
				return -1;
			} else if (dt1.getTime() < dt2.getTime()) {
				return 1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			logger.info(exception.getMessage());
		}
		return 0;
	}

}
