package mocean.logs.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import mocean.logs.service.CmsLogsService;
import net.sf.json.JSONArray;

@Controller
public class CmsLogsController {

    @Resource
    private CmsLogsService cmsLogsService;

    @RequestMapping("getDetailBySn")
    @ResponseBody
    public JSONArray getDetailBySn(String sn) {
        if(StringUtils.isBlank(sn)) {
            return JSONArray.fromObject(new ArrayList<>());
        }
        return JSONArray.fromObject(cmsLogsService.searchAll(sn));
    }

    @RequestMapping("getIntegerFaceList")
    @ResponseBody
    public JSONArray getIntegerFaceList() {
        List<Map<String,String>> interFaceList = new ArrayList<>();
        List<String> list = cmsLogsService.getInterfaceList();
        if(list.size()>0) {
            for(int i=0;i<list.size();i++) {
                Map<String,String> tempMap = new HashMap<>();
                tempMap.put("text", list.get(i));
                interFaceList.add(tempMap);
            }
        }
        return JSONArray.fromObject(interFaceList);
    }
}
