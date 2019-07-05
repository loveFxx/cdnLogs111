package mocean.logs.util;

import java.io.Serializable;
import java.util.List;

public class Page<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long ltotal;
	private List<T> lrows;
	
	public long getTotal() {
		return ltotal;
	}
	public void setTotal(long total) {
		this.ltotal = total;
	}
	public List<T> getRows() {
		return lrows;
	}
	public void setRows(List<T> rows) {
		this.lrows = rows;
	}
	
}
