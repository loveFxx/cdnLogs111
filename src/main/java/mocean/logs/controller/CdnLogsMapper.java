package mocean.logs.controller;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import mocean.logs.util.PageBean;

@Mapper
public interface CdnLogsMapper {
	
	public void saveCdnLogs(List<CdnLogsBean> beans);
	
	public int getCdnLogsCount(PageBean pageBean);
	
	public List<CdnLogsBean> getCdnLogsList(PageBean pageBean);
	
	public int getAllCount();
	
}
