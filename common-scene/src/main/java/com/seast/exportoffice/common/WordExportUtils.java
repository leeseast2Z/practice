package com.seast.exportoffice.common;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import freemarker.template.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import com.seast.exportoffice.common.ZipUtil;

/**
 * @Author: limf
 * @Date: 2021/9/27 09:34
 * @Description:
 */
public class WordExportUtils {
    private static Configuration cfg = null;
    static {
        cfg = new Configuration(Configuration.VERSION_2_3_31);
        //设置模板所在文件夹
        cfg.setClassForTemplateLoading(WordExportUtils.class, "/templates");
        // setEncoding这个方法一定要设置国家及其编码，不然在ftl中的中文在生成html后会变成乱码
        cfg.setEncoding(Locale.getDefault(), CharEncoding.UTF_8);
        // 设置对象的包装器
        cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_31));
        // 设置异常处理器,这样的话就可以${a.b.c.d}即使没有属性也不会出错
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
    }

    /**
     * 获取模板字符串输入流
     * @param dataMap        参数Map
     * @param templateName   模板名称
     * @return
     */
    public static ByteArrayInputStream getFreemarkerContentInputStream(Map<String, Object> dataMap, String templateName) {
        ByteArrayInputStream in = null;
        StringWriter stringWriter = new StringWriter();
        try {
            Template template = cfg.getTemplate(templateName);
            template.process(dataMap, stringWriter);
            // 这里一定要设置utf-8编码 否则导出的word中中文会是乱码
            in = new ByteArrayInputStream(stringWriter.toString().getBytes(CharEncoding.UTF_8));
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }

    /**
     * 生成word文档方法
     * @param dataMap       参数Map
     * @param templateName  模板名称
     * @param filePath      生成文件名称
     * @return
     */
    private static File createDoc(Map<String, Object> dataMap, String templateName, String filePath) {
        File file = new File(filePath);
        Writer writer = null;
        FileOutputStream fos = null;
        try {
            Template template = cfg.getTemplate(templateName);
            // 这个地方不能使用FileWriter因为需要指定编码类型否则生成的Word文档会因为有无法识别的编码而无法打开
            fos = new FileOutputStream(file);
            writer = new OutputStreamWriter(fos, CharEncoding.UTF_8);
            template.process(dataMap, writer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                fos.close();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 将文件流输入到请求响应的输出流中
     * @param paramMap      参数Map
     * @param templateName  模板名称
     * @param response      请求响应
     * @param fileName      文件名称
     */
    public static void response(Map<String, Object> paramMap, String templateName, HttpServletResponse response, String fileName) {
        response.reset();
        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.setContentType("application/msword");
        try {
            // 设置浏览器以下载的方式处理该文件名（PS浏览设置：下载前询问每个文件的保存位置）
            response.setHeader("Content-Disposition", "attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName, CharEncoding.UTF_8))));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        setResponseOutputStream(paramMap, templateName, response);
    }

    /**
     * 设置请求响应的输出流
     * @param paramMap      参数Map
     * @param templateName  模板名称
     * @param response      请求响应
     */
    public static void setResponseOutputStream(Map<String, Object> paramMap, String templateName, HttpServletResponse response) {
        ByteArrayInputStream inputStream = getFreemarkerContentInputStream(paramMap, templateName);
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            byte[] buffer = new byte[512];  // 缓冲区
            int bytesToRead = -1;
            // 通过循环将读入的Word文件的内容输出到浏览器中
            while((bytesToRead = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesToRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
     *压缩包方式导出多个word
     *由于一次请求浏览器只能响应一次，想导出多个必须打包，亲测for循环导出只能导一个
     *如果想做到分别单独下载，那就得用插件啦，这里不提供插件的做法
     *思路：生成临时目录-在临时目录生成word-将临时目录打zip包-zip文件下载-删除临时目录和zip包，
     * 回收系统资源
     */
    public static void responseBatch(List<Map<String, Object>> mapList, List<String> titleList, String ftlFile, HttpServletResponse response) {
        File zipfile = null;
        File directory = null;
        InputStream fin = null;
        ServletOutputStream out = null;
        String zipName = "testzip.zip";
        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment;filename=" + zipName);
        try {
            out = response.getOutputStream();
            // String serverPath = SystemPath.getSysPath();
            String path = "D:\\userfiles\\zipword\\个人信息";
            directory = new File(path);
            directory.mkdirs();
            for(int i = 0; i < mapList.size();i++) {
                Map<String, Object> map = mapList.get(i);
                String title = titleList.get(i);
                // 调用工具类的createDoc方法在临时目录下生成Word文档
                createDoc(map, ftlFile,directory.getPath()+ "/" + title + ".doc");
            }
            //压缩目录
            ZipUtil.createZip(path, zipName);
            //根据路径获取刚生成的zip包文件
            zipfile = new File(zipName);
            fin = new FileInputStream(zipfile);
            byte[] buffer = new byte[512]; // 缓冲区
            int bytesToRead = -1;
            // 通过循环将读入的Word文件的内容输出到浏览器中
            while ((bytesToRead = fin.read(buffer)) != -1) {
                out.write(buffer, 0, bytesToRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fin!=null) fin.close();
                if (out!=null) out.close();
                if (zipfile!=null) zipfile.delete();
                if (directory!=null) {
                    //递归删除目录及目录下文件
                    ZipUtil.deleteFile(directory);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }


    //outputStream 输出流可以自己定义 浏览器或者文件输出流
    public static void createDocx(Map<String, Object> dataMap, String fileName, HttpServletResponse response) {
        response.reset();
        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.setContentType("application/msword");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, CharEncoding.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String separator = SystemPath.getSeparator();
        String originDocument = dataMap.get("originDocument")+""; 			// 最初设计的模板docx
        String documentXmlRels = dataMap.get("documentXmlRels")+"";			// documentXmlRels
        String documentXml = dataMap.get("documentXml")+""; 				// documentXml
        OutputStream outputStream = null;
        ZipOutputStream zipout = null;
        try {
            List<Map<String, Object>> picList = Lists.newArrayList();
            @SuppressWarnings("unchecked")
            HashMap<String, Object> paramsMap = (HashMap<String, Object>)dataMap.get("paramsMap");
            HashMap<String, Object> itemMap = null;
            for(String key : paramsMap.keySet()) {
                if(key.contains("Base64")) {// 处理图片
                    String pName = key.replace("Base64", "");
                    String base64Str = paramsMap.get(key)+"";
                    itemMap = Maps.newHashMap();
                    itemMap.put("pName", pName+".jpg");
                    itemMap.put("base64", base64ToImage(base64Str));
                    picList.add(itemMap);
                    dataMap.put(pName, itemMap);
                }else {
                    dataMap.put(key, paramsMap.get(key));
                }
            }
            // 图片配置文件模板
            ByteArrayInputStream documentXmlRelsInput = getFreemarkerContentInputStream(dataMap, documentXmlRels);
            // 内容模板
            ByteArrayInputStream documentInput = getFreemarkerContentInputStream(dataMap, documentXml);
            // 最初设计的模板
            File docxFile = new File(WordExportUtils.class.getClassLoader().getResource(originDocument).getPath());
            if (!docxFile.exists()) {
                docxFile.createNewFile();
            }
            @SuppressWarnings("resource")
            ZipFile zipFile = new ZipFile(docxFile);
            Enumeration<? extends ZipEntry> zipEntrys = zipFile.entries();
            outputStream = response.getOutputStream();
            zipout = new ZipOutputStream(outputStream);
            //开始覆盖文档------------------
            int len = -1;
            byte[] buffer = new byte[1024];
            while (zipEntrys.hasMoreElements()) {
                ZipEntry next = zipEntrys.nextElement();
                System.out.println("next = " + next);
                InputStream is = zipFile.getInputStream(next);
                // 剔除media文件夹
                if (next.toString().indexOf("media") < 0) {
                    zipout.putNextEntry(new ZipEntry(next.getName()));
                    if (next.getName().indexOf("document.xml.rels") > 0) { //如果是document.xml.rels由我们输入
                        if (documentXmlRelsInput != null) {
                            while ((len = documentXmlRelsInput.read(buffer)) != -1) {
                                zipout.write(buffer, 0, len);
                            }
                            documentXmlRelsInput.close();
                        }
                    } else if ("word/document.xml".equals(next.getName())) {//如果是word/document.xml由我们输入
                        if (documentInput != null) {
                            while ((len = documentInput.read(buffer)) != -1) {
                                zipout.write(buffer, 0, len);
                            }
                            documentInput.close();
                        }
                    } else {
                        while ((len = is.read(buffer)) != -1) {
                            zipout.write(buffer, 0, len);
                        }
                        is.close();
                    }
                }
            }



            String zipEntryName;
            for (Map<String, Object> pic : picList) {
                for(Enumeration<?> e = zipFile.entries(); e.hasMoreElements(); ) {
                    ZipEntry entryIn = (ZipEntry) e.nextElement();
                    zipEntryName = entryIn.getName();
                    System.out.println("zipEntryName = " + zipEntryName);
                    if(zipEntryName.endsWith("media")) {
                        FileInputStream fis = new FileInputStream(String.valueOf(pic.get("pName")));
                        ZipEntry zipEntry = new ZipEntry(zipEntryName);
                        zipout.putNextEntry(zipEntry);
                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = fis.read(bytes)) >= 0) {
                            zipout.write(bytes, 0, length);
                        }
                        zipout.closeEntry();
                        fis.close();
                    }
                } // enf of for
            }
            // 写入图片
            for (Map<String, Object> pic : picList) {
//                ZipUtil.writeZip(new File("C:\\Users\\WIN10\\Desktop\\image\\media"),
//                        "word" + separator,
//                        zipout);

//                ZipEntry next = new ZipEntry("word" + separator + "media" + separator + pic.get("pName"));
//                zipout.putNextEntry(new ZipEntry(next.toString()));
//                // InputStream in = (ByteArrayInputStream)pic.get("base64");
//                InputStream in = new FileInputStream("C:\\Users\\WIN10\\Desktop\\image\\my\\imagePath.jpg");
//                while ((len = in.read(buffer)) != -1) {
//                    zipout.write(buffer, 0, len);
//                    zipout.flush();
//                }
//                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(zipout!=null){
                try {
                    zipout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ByteArrayInputStream base64ToImage(String base64Str) {
        ByteArrayInputStream bais = null;
        if (base64Str == null) {
            return bais;
        }
        Base64 base64 = new Base64();
        try {
            if(StringUtils.isEmpty(base64Str)) {
                return bais;
            }
            if(base64Str.indexOf(",")!=-1) {
                base64Str = base64Str.substring(base64Str.indexOf(",")+1);
            }
            byte[] bytes = base64.decodeBase64(base64Str);
            bais = new ByteArrayInputStream(bytes);
            return bais;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bais;
    }
}
