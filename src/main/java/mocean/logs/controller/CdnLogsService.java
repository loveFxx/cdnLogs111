package mocean.logs.controller;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import mocean.logs.common.Common;
import mocean.logs.util.PageBean;


//@Service
public class CdnLogsService {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private CdnLogsMapper cdnLogsMapper;


	public void saveCdnLogs(List<CdnLogsBean> beans) {
		cdnLogsMapper.saveCdnLogs(beans);
	}

	public int getCdnLogsCount(PageBean pageBean) {
		pageBean.setTotalSize(Common.TOTALSIZE);
		return cdnLogsMapper.getCdnLogsCount(pageBean);
	}
	
	public List<CdnLogsBean> getCdnLogsList(PageBean pageBean) {
		logger.info(System.currentTimeMillis()+"  getCdnLogsList start");
		return cdnLogsMapper.getCdnLogsList(pageBean);
	}
}
