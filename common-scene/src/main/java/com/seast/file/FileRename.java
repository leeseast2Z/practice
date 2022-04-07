package com.seast.file;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: limf
 * @Date: 2022/4/1 13:54
 * @Description:
 */
public class FileRename {

    public static final String HOME_DIR = "Y:\\osstemp";

    public static void main(String[] args) {
        File[] files = new File(HOME_DIR).listFiles();
        System.out.println("FileUtils.getUserDirectoryPath() = " + FileUtils.getUserDirectoryPath());
        System.out.println("FileUtils.getTempDirectoryPath() = " + FileUtils.getTempDirectoryPath());
        for (File file : files) {
            String fileName = file.getName();
            int index = fileName.lastIndexOf(".");
            String fileNameNoExtension = fileName.substring(0, index);
            System.out.println("fileNameNoExtension = " + fileNameNoExtension);
            if(fileNameNoExtension.length() < 14 || fileNameNoExtension.length() > 17) {
                System.out.println("不符合");
                continue;
            }
            // 格式化文件名
            Pattern pattern = Pattern.compile("\\d{14,17}");
            Matcher matcher = pattern.matcher(fileNameNoExtension);
            if(matcher.find()) {
                try {
                    Date date = DateUtils.parseDate(fileNameNoExtension, "yyyyMMddHHmmss");
                    DateFormatUtils.format(date, "yyyy-MM-dd.HHmmss");
                } catch (ParseException e) {
                    System.out.println(fileNameNoExtension + "：格式化出错");
                    e.printStackTrace();
                }
                System.out.println("符合");
            }
        }
    }

}
