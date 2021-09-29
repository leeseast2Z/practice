package com.seast.exportoffice.common;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @Author: limf
 * @Date: 2021/9/27 11:26
 * @Description:
 */
public class ZipUtil {

    private static final int  BUFFER_SIZE = 2 * 1024;

    public static void zip(String inputFileName, String zipFileName)
            throws Exception {
        zip(zipFileName, new File(inputFileName));
    }

    private static void zip(String zipFileName, File inputFile)
            throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                zipFileName));
        zip(out, inputFile, "");
        System.out.println("zip done");
        out.close();
    }

    private static void zip(ZipOutputStream out, File f, String base)
            throws Exception {
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            out.putNextEntry(new ZipEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], base + fl[i].getName());
            }
        } else {
            out.putNextEntry(new ZipEntry(base));
            FileInputStream in = new FileInputStream(f);
            int b;
            //System.out.println(base);
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            in.close();
        }
    }

    /**
     * 创建ZIP文件
     * @param sourcePath 文件或文件夹路径
     * @param zipPath 生成的zip文件存在路径（包括文件名）
     */
    public static void createZip(String sourcePath, String zipPath) {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipPath);
            zos = new ZipOutputStream(fos);
            writeZip(new File(sourcePath), "", zos);
        } catch (FileNotFoundException e) {
            // log.error("ZipUtils createZip  Failed to create ZIP file", e);
        } finally {
            try {
                if (zos != null) {
                    // log.debug("ZipUtils createZip Create a ZIP file successfully! the path in:{}",zipPath);
                    zos.close();
                    //压缩成功后，删除打包前的文件
                    deleteFile( new File(sourcePath) );
                }
            } catch (IOException e) {
                // log.error("ZipUtils createZip  Failed to create ZIP file", e);
            }
        }
    }

    public static void writeZip(File file, String parentPath,
                                 ZipOutputStream zos) {
        if (file.exists()) {
            if (file.isDirectory()) {// 处理文件夹
                parentPath += file.getName() + File.separator;
                File[] files = file.listFiles();
                for (File f : files) {
                    writeZip(f, parentPath, zos);
                }
            } else {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    byte[] content = new byte[1024];
                    int len;
                    while ((len = fis.read(content)) != -1) {
                        zos.write(content, 0, len);
                        zos.flush();
                    }
                } catch (FileNotFoundException e) {
                    // log.error("ZipUtils createZip  Failed to create ZIP file",e);
                } catch (IOException e) {
                    // log.error("ZipUtils createZip  Failed to create ZIP file",e);
                } finally {
                    try {
                        if (fis != null) {
                            fis.close();
                        }
                    } catch (IOException e) {
                        // log.error("ZipUtils createZip  Failed to create ZIP file",e);
                    }
                }
            }
        }
    }

    public static void copyResource(List<String> oldResPath, String newResPath) {
        for (int m = 0; m < oldResPath.size(); m++) {
            try {
                // 如果文件夹不存在 则建立新文件夹
                (new File(newResPath)).mkdirs();
                File a = new File(oldResPath.get(m));
                // 如果已经是具体文件，读取
                if (a.isFile()) {
                    FileInputStream input = new FileInputStream(a);
                    FileOutputStream output = new FileOutputStream(newResPath + "/" + (a.getName()).toString());
                    byte[] b = new byte[1024 * 4];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                    // 如果文件夹下还存在文件，遍历，直到得到具体的文件
                } else {
                    String[] file = a.list();
                    File temp = null;
                    for (int i = 0; i < file.length; i++) {
                        if (oldResPath.get(m).endsWith(File.separator)) {
                            temp = new File(oldResPath.get(m) + file[i]);
                        } else {
                            temp = new File(oldResPath.get(m) + File.separator + file[i]);
                        }

                        if (temp.isFile()) {
                            FileInputStream input = new FileInputStream(temp);
                            FileOutputStream output = new FileOutputStream(newResPath + "/" + (temp.getName()).toString());
                            byte[] b = new byte[1024 * 4];
                            int len;
                            while ((len = input.read(b)) != -1) {
                                output.write(b, 0, len);
                            }
                            output.flush();
                            output.close();
                            input.close();
                        }
                        if (temp.isDirectory()) {
                            List<String> oldChildPath = new ArrayList<String>();
                            oldChildPath.add(oldResPath.get(m) + "/" + file[i]);
                            newResPath = newResPath + "/" + file[i];
                            // 如果是子文件夹 递归循环
                            copyResource(oldChildPath, newResPath);
                        }
                    }
                }
            } catch (Exception e) {
                // log.error("copy all files failed", e);
            }
        }
    }
    /**
     * 删除文件夹
     * @param file
     */
    public static void deleteFile(File file) {
        if (file.exists()) {                               // 判断文件是否存在
            if (file.isFile()) {                           // 判断是否是文件
                file.delete();
            } else if (file.isDirectory()) {               // 否则如果它是一个目录
                File files[] = file.listFiles();           // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) {   // 遍历目录下所有的文件
                    deleteFile(files[i]);                  // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        }
    }

    /**
     * 时间格式化
     *
     * @return
     */
    public static String dateToString() {
        Date d = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = formatter.format(d);
        return time;
    }


    //main方法测试
    public static void main(String[] args) {
        List<String> oldResPath = new ArrayList<String>();
        // excel文件路径
        oldResPath.add("d:\\a.xlsx");
        oldResPath.add("d:\\b.xlsx");
        String newResPath = "d:\\" + "excel" + dateToString(); // 生成的文件夹名
        String zipPath = newResPath + ".zip";                  // 压缩问价夹名

        copyResource(oldResPath, newResPath);                  // 把excel拷贝到同个文件目录下
        createZip(newResPath, zipPath);                        // 打包改目录成.zip包
    }

    /**
     * zip解压
     * @param srcFile        zip源文件
     * @param destDirPath     解压后的目标文件夹
     * @throws RuntimeException 解压失败会抛出运行时异常
     */
    public static void unZip(File srcFile, String destDirPath) throws RuntimeException {
        long start = System.currentTimeMillis();
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
        }
        // 开始解压
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(srcFile);
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                System.out.println("解压" + entry.getName());
                // 如果是文件夹，就创建个文件夹
                if (entry.isDirectory()) {
                    String dirPath = destDirPath + "/" + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                    File targetFile = new File(destDirPath + "/" + entry.getName());
                    // 保证这个文件的父文件夹必须要存在
                    if(!targetFile.getParentFile().exists()){
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    // 将压缩文件内容写入到这个文件中
                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[BUFFER_SIZE];
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    // 关流顺序，先打开的后关闭
                    fos.close();
                    is.close();
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("解压完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            throw new RuntimeException("unzip error from ZipUtils", e);
        } finally {
            if(zipFile != null){
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
