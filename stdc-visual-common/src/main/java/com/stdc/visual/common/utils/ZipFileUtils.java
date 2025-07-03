package com.stdc.visual.common.utils;

import com.google.common.io.ByteStreams;
import com.stdc.core.tool.utils.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author: wang_jie
 * @data: 2023/3/29--9:34
 * @describe: 压缩包工具类
 */
@Slf4j
public class ZipFileUtils {

    /**
     * 将本地目录打成zip文件并且写入响应体
     * @param response
     * @param zipName
     * @throws IOException
     */
   public static void writeZipToResp(HttpServletResponse response,String filePath,String zipName) throws IOException {

       // 创建一个字节数组输出流
       ByteArrayOutputStream baos = new ByteArrayOutputStream();

       // 创建一个Zip输出流
       ZipOutputStream zos = new ZipOutputStream(baos);

       // 将目录下的所有文件和子目录压缩到zip文件中
       File folder = new File(filePath);
       addFolderToZip(folder, "", zos);

       // 关闭Zip输出流
       zos.close();

       // 将压缩后的字节数组转换为Blob类型并写入响应体
       byte[] bytes = baos.toByteArray();
       Blob blob = null;
       try {
           blob = new SerialBlob(bytes);
       } catch (SQLException e) {
           e.printStackTrace();
       }
       response.setContentType("application/zip");
       response.setHeader("Content-Disposition", "attachment; filename=\"" + zipName + "\"");
       try {
           if (ObjectUtil.isNotEmpty(blob)){
               response.getOutputStream().write(ByteStreams.toByteArray(blob.getBinaryStream()));
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
       response.flushBuffer();


//       // 设置响应头，指定文件名和文件类型
//       response.setContentType("application/zip");
//       response.setHeader("Content-Disposition", "attachment; filename=\""+zipName+"\"");
//
//       // 创建ZipOutputStream对象，将要压缩的文件写入ZipOutputStream
//       ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
//       File folder = new File(filePath);
//       addFolderToZip("", folder, out);
//       out.close();
   }

    /**
     * 将文件夹及其内容添加到ZipOutputStream
     */
//    private static void addFolderToZip(String path, File folder, ZipOutputStream zip) throws IOException {
//        for (File file : folder.listFiles()) {
//            if (file.isDirectory()) {
//                addFolderToZip(path + "/" + file.getName() + "/", file, zip);
//            } else {
//                byte[] buf = new byte[1024];
//                FileInputStream in = new FileInputStream(file);
//                zip.putNextEntry(new ZipEntry(path + file.getName()));
//                int len;
//                while ((len = in.read(buf)) > 0) {
//                    zip.write(buf, 0, len);
//                }
//                zip.closeEntry();
//                in.close();
//            }
//        }
//    }

    // 将目录下的所有文件和子目录压缩到Zip输出流中
    private static void addFolderToZip(File folder, String parentFolderName, ZipOutputStream zos) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                addFolderToZip(file, parentFolderName + "/" + file.getName(), zos);
            } else {
                ZipEntry zipEntry = new ZipEntry(parentFolderName + "/" + file.getName());
                zos.putNextEntry(zipEntry);
                FileInputStream fis = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zos.write(bytes, 0, length);
                }
                fis.close();
                zos.closeEntry();
            }
        }
    }

    /**
     * 将文件夹压缩为zip包并且导入到本地
     * @param sourceFolderPath 源文件夹的路径
     * @param outputZipPath 输出Zip文件的路径
     */
    public static void writeFolderToZip(String sourceFolderPath,String outputZipPath ){
        try {
            // 创建Zip输出流
            FileOutputStream fos = new FileOutputStream(outputZipPath);
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            // 递归压缩文件夹
            File fileToZip = new File(sourceFolderPath);
            zipFile(fileToZip, "", zipOut);

            // 关闭Zip输出流
            zipOut.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

}
