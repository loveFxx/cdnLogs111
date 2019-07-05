package mocean.logs.service;

import mocean.logs.dao.ChannelMapper;
import mocean.logs.domain.Audrating;
import mocean.logs.domain.CdnChannelBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Service
public class CdnChannelService {
    @Resource
    private ChannelMapper channelMapper;
    private Logger logger = LoggerFactory.getLogger( this.getClass() );

    public List<CdnChannelBean> getForChannelName(ArrayList channelid){
        if(channelid.size()==0){
            return new ArrayList<>(  );
        }
        String channels = "'";
        for (int i =0;i< channelid.size();i++){
            channels += String.format("%s','", channelid.get( i ));
        }
        channels=channels.substring( 0,channels.length()-2 );
//        logger.info( "channels= "+channels );
        return channelMapper.getForChannelName(channels );
    }


    public List<CdnChannelBean> getForChannelNameByOther(ArrayList channelid){
        if(channelid.size()==0){
            return new ArrayList<>(  );
        }
        String channels = "'";
        for (int i =0;i< channelid.size();i++){
            if(channelid.get( i ).toString().contains( "@" )){
                channels += String.format("%s','", channelid.get( i ).toString().split( "@" )[0]);
            }else {
                channels += String.format("%s','", channelid.get( i ));
            }
        }
        channels=channels.substring( 0,channels.length()-2 );
//        logger.info( "channels= "+channels );
        return channelMapper.getForChannelName(channels );
    }
}
