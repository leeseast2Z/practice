package com.sea.exportoffice.common;

import com.seast.exportoffice.common.SystemPath;
import com.seast.exportoffice.common.ZipUtil;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @Author: limf
 * @Date: 2021/9/29 14:53
 * @Description: java ZIP测试
 */
public class ZipTest {
    public static void main(String[] args) {
        byte[] buffer = new byte[1024];
        int len = -1;
        String zipFilePath = "C:\\Users\\WIN10\\Desktop\\template.docx";
        String zipFileOutPath = "C:\\Users\\WIN10\\Desktop\\template2.zip";
        ZipOutputStream zipout = null;
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(zipFileOutPath);
            ZipFile zipFile = new ZipFile(zipFilePath);
            zipout = new ZipOutputStream(outputStream);
            Enumeration<? extends ZipEntry> zipEntrys = zipFile.entries();
            while (zipEntrys.hasMoreElements()) {// 替换文本内容和引用关系
                ZipEntry next = zipEntrys.nextElement();
                if (next.toString().indexOf("media") < 0) {

                }
                System.out.println("执行压缩的文件路径 = " + next);
                InputStream is = zipFile.getInputStream(next);
                zipout.putNextEntry(new ZipEntry(next.getName()));
                while ((len = is.read(buffer)) != -1) {
                    zipout.write(buffer, 0, len);
                }
                is.close();
            }

            // 处理media文件夹
//            ZipEntry mediaEntry = new ZipEntry("word/media");
//            zipout.putNextEntry(mediaEntry);
            // FileOutputStream读取流的时候如果是文件夹，就会出错，无论怎么读，都拒绝访问，应该在读取的目录后面加上文件名！
//            File file = new File("D:\\media");
//            FileInputStream fileInputStream = new FileInputStream(file);
//            while ((len = fileInputStream.read(buffer)) != -1) {
//                zipout.write(buffer, 0, len);
//            }
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

//        String unZipFilePath = "C:\\Users\\WIN10\\Desktop\\template";
//        ZipUtil.unZip(new File(zipFileOutPath), unZipFilePath);

//        File file = new File(unZipFilePath);
//        File[] files = file.listFiles(); // listFiles方法返回遍历结果数组
//        for (File f : files) { // 遍历File[]数组
//            if(f.isDirectory() && f.getName().indexOf("word") != -1) {
//                File[] wordFiles = f.listFiles();
//                for (File wordFile : wordFiles) {
//                    System.out.println("wordFile.getName() = " + wordFile.getName());
//                    if(wordFile.getName().equals("media")) {
//                        System.out.println("找到了");
//                    }
//                }
//            }
//        }
//        Path zipfile = Paths.get("C:\\Users\\WIN10\\Desktop\\template.zip");
//        try (FileSystem zipfs = FileSystems.newFileSystem(zipfile, null)) {
//            Path extFile = Paths.get("D:\\media\\imagePath.jpg"); // from normal file system
//            FileChannel fileChannel = FileChannel.open(extFile);
//            System.out.println("fileChannel.size() = " + fileChannel.size());
//            Path directory = zipfs.getPath("/word/media"); // from zip file system
//            Files.createDirectories(directory);
//            Files.copy(extFile, directory.resolve("image1.jpg"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
