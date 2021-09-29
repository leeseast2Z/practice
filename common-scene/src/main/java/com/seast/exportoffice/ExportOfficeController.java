package com.seast.exportoffice;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.seast.exportoffice.common.ImageToBase64Util;
import com.seast.exportoffice.common.WordExportUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: limf
 * @Date: 2021/9/27 14:36
 * @Description: 导出Office
 */
@Controller
@RequestMapping("/exportoffice")
public class ExportOfficeController {
    /**
     * 导出Word XML
     * @param response
     */
    @RequestMapping("/exportWordXML")
    public void exportWordXML(HttpServletResponse response) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("name", "李四");
        paramMap.put("sex", "男");
        paramMap.put("age", "22");
        WordExportUtils.response(paramMap, "/word/info.ftl", response, "测试22323.doc");
    }

    /**
     * 导出多个word，压缩成zip
     * @param response
     */
    @RequestMapping("/exportWordXMLBatch")
    public void exportWordXMLBatch(HttpServletResponse response) {
        List<Map<String, Object>> paramList = Lists.newArrayList();
        List<String> titleList = Lists.newLinkedList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", "李四");
        map.put("sex", "男");
        map.put("age", "22");
        paramList.add(map);
        titleList.add(String.valueOf(map.get("name")));
        map = Maps.newHashMap();
        map.put("name", "王琪");
        map.put("sex", "女");
        map.put("age", "29");
        paramList.add(map);
        titleList.add(String.valueOf(map.get("name")));
        map = Maps.newHashMap();
        map.put("name", "张思琪");
        map.put("sex", "女");
        map.put("age", "19");
        paramList.add(map);
        titleList.add(String.valueOf(map.get("name")));
        WordExportUtils.responseBatch(paramList, titleList, "/word/info.ftl", response);
    }


    /**
     * 导出Word DOCX
     * @param response
     */
    @RequestMapping("/exportWord")
    public void exportWord(HttpServletResponse response) {
        HashMap<String, Object> dataMap = Maps.newHashMap();
        Map<String, Object> paramsMap = Maps.newHashMap();
        paramsMap.put("name", "test");
        paramsMap.put("age", "29");
        paramsMap.put("imagePathBase64", ImageToBase64Util.doGetImageStrFromPath("C:\\Users\\WIN10\\Desktop\\image\\my\\imagePath.jpg"));
        dataMap.put("paramsMap", paramsMap);
        dataMap.put("originDocument", "/templates/word/template.docx");
        dataMap.put("documentXmlRels", "/word/document.xml.rels.ftl");
        dataMap.put("documentXml", "/word/document.xml.ftl");
        WordExportUtils.createDocx(dataMap, "template.docx", response);
    }
}
