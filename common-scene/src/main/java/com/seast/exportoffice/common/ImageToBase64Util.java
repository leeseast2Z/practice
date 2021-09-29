package com.seast.exportoffice.common;

import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;

/**
 * @Author: limf
 * @Date: 2021/9/28 11:24
 * @Description:
 */
public class ImageToBase64Util {
    /**
     * 将网络图片转换成base码
     * @param imgURL
     * @return
     * @throws Exception
     */
    public static String doGetImageBase64StrFromUrl(String imgURL) throws Exception{
        byte[] data = null;
        InputStream inStream = null;
        try {
            // 创建URL
            URL url = new URL(imgURL);
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            inStream = conn.getInputStream();
            data = new byte[inStream.available()];
            inStream.read(data);
        } catch (IOException e) {
            e.printStackTrace();
            String exMsg = MessageFormat.format("通过图片URL获取Base64字符串时发生异常：{0}", e.getMessage());
        } finally {
            try {
                if (null != inStream) {
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                String exMsg = MessageFormat.format("通过图片URL获取Base64字符串关闭InputStream时发生异常：{0}", e.getMessage());
            }
        }
        // 对字节数组Base64编码
        byte[] encodeBase64 = Base64.encodeBase64(data);
        // 返回Base64编码过的字节数组字符串
        String imageBase64String = new String(encodeBase64);
        return imageBase64String;
    }

    /***
     * 将本地图片转换成base吗
     * @param imgPath
     * @return
     */
    public static String doGetImageStrFromPath(String imgPath){
        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imgPath);
            data = new byte[in.available()];
            in.read(data);
        } catch (IOException e) {
            e.printStackTrace();
            String exMsg = MessageFormat.format("通过图片文件路径获取Base64字符串时发生异常：{0}", e.getMessage());
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                String exMsg = MessageFormat.format("通过图片URL获取Base64字符串关闭InputStream时发生异常：{0}", e.getMessage());
            }
        }
        // 对字节数组Base64编码
        byte[] encodeBase64 = Base64.encodeBase64(data);
        // 返回Base64编码过的字节数组字符串
        String imageBase64String = new String(encodeBase64);
        if (null == imageBase64String || imageBase64String.length() == 0) {
        }
        return imageBase64String;
    }

    /**
     * 将base64转换成图片
     * @param imgBase64String
     * @param dir
     * @param fileName
     * @return
     */
    public static void doSaveBase64AsImageFile(String imgBase64String, String dir, String fileName) {
        // 图像数据为空
        if (imgBase64String == null) {
            // log.error("将Base64字符串保存为本地图片文件时发生异常：图片Base64字符串不能为空");
        }
        // Base64解码
        byte[] bytes = Base64.decodeBase64(new String(imgBase64String).getBytes());
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {
                // 调整异常数据
                bytes[i] += 256;
            }
        }
        OutputStream out = null;
        try {
            File directory = new File(dir);
            // 判断文件目录是否存在
            if (!directory.exists() && directory.isDirectory()) {
                // 如果不存在则创建目录
                directory.mkdirs();
            }
            // 本地图片文件保存路径
            String imageSaveToFilePath = dir + File.separator + fileName;
            out = new FileOutputStream(imageSaveToFilePath);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // log.error("请检查本地图片文件保存路径是否存在：{0}", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            // log.error("图片Base64字符串保存为本地图片文件时发生异常：{0}", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            // log.error("图片Base64字符串保存为本地图片文件时发生异常：{0}", e.getMessage());
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                // log.error("图片Base64字符串保存为本地图片文件关闭OutputStream时发生异常：{0}", e.getMessage());
            }
        }
    }


    //图片转化成base64字符串
    public static String GetImageStr(String imgPath)
    {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try
        {
            in = new FileInputStream(imgPath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//返回Base64编码过的字节数组字符串
    }

    //base64字符串转化成图片
    public static boolean GenerateImage(String imgStr)
    {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            String imgFilePath = "D:\\360CloudUI\\tupian\\new.jpg";//新生成的图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
