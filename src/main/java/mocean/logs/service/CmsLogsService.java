package mocean.logs.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import mocean.logs.domain.CmsLogsBean;
import mocean.logs.util.DateUtil;
import encrypt.*;

@Service
public class CmsLogsService {

    @Resource
    private RestHighLevelClient restClient;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<String> interfaceList = new ArrayList<>();

    public List<String> getInterfaceList() {
        return interfaceList.stream().distinct().collect(Collectors.toList());
    }

    /**
     *
     * @MethodName:searchAll
     * @Description:从elastic中查出指定sn的所有数据
     * @author yhy
     * @date 2018年10月9日 下午2:09:20
     */
    public List<CmsLogsBean> searchAll(String sn) {
        interfaceList = new ArrayList<>();
        List<CmsLogsBean> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            try {
                String searchIndex = "epg-" + DateUtil.getQueryDay(i);
                Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
                SearchRequest searchRequest = new SearchRequest(searchIndex);
                searchRequest.scroll(scroll);
                SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
                sourceBuilder.query(QueryBuilders.termQuery("sn", sn));
                searchRequest.source(sourceBuilder);
                SearchResponse searchResponse = restClient.search(searchRequest);
                String scrollId = searchResponse.getScrollId();
                do {
                    for (SearchHit hit : searchResponse.getHits().getHits()) {
                        JSONObject object = JSON.parseObject(hit.getSourceAsString());
                        String message = object.getString("message");
                        String[] messageParameters = message.split("[?]");
                        String[] messageParameter = messageParameters[1].split("&");
                        Map<String, String> map = new HashMap<>();
                        for (String messageParam : messageParameter) {
                            if (messageParam.split("=").length > 1) {
                                map.put(messageParam.split("=")[0], messageParam.split("=")[1]);
                            } else {
                                map.put(messageParam.split("=")[0], null);
                            }
                        }
                        CmsLogsBean cmsLogs = transToCmsLogs(map,object);
                        list.add(cmsLogs);
                        interfaceList.add(cmsLogs.getInterFace());
                    }
                    SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                    scrollRequest.scroll(scroll);
                    searchResponse = restClient.searchScroll(scrollRequest);
                    scrollId = searchResponse.getScrollId();
                } while (searchResponse.getHits().getHits().length != 0);
            } catch (IndexNotFoundException e) {
                logger.error("索引" + e.getIndex() + "不存在");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(list);
        return list;
    }

    private CmsLogsBean transToCmsLogs(Map<String, String> map,JSONObject object) {
        CmsLogsBean cmsLogs = new CmsLogsBean();
        if (map.containsKey("authenticator")) {
            String authenticator = map.get("authenticator");
            String encryption = map.get("encryption");
            String usertoken = map.get("usertoken");
            String decrypt = Decrypt.decryptAuthenticator(authenticator, Integer.valueOf(encryption),
                    usertoken);
            cmsLogs.setDecrypt(decrypt);
        } else {
            cmsLogs.setDecrypt("无需解密");
        }
        cmsLogs.setDate(DateUtil.getTransTime("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "MMMM dd'th' yyyy, HH:mm:ss.SSS", object.getString("index_day")));
        cmsLogs.setHost(object.getString("host"));
        cmsLogs.setClientIp(map.get("clientip"));
        cmsLogs.setSource(object.getString("source"));
        cmsLogs.setMessage(object.getString("message"));
        cmsLogs.setSn(object.getString("sn"));
        cmsLogs.setInterFace(object.getString("interface"));
        return cmsLogs;
    }

}
