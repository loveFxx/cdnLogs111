package mocean.logs.dao;

import mocean.logs.domain.CdnChannelBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChannelMapper {

    public List<CdnChannelBean> getForChannelName(@Param("channel") String channel);

}

