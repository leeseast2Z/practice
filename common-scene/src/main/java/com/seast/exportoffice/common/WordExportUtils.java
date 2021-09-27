package com.seast.exportoffice.common;

import freemarker.template.*;
import org.apache.commons.lang3.CharEncoding;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
            response.setHeader("Content-Disposition", "attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName, "UTF-8"))));
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

}
