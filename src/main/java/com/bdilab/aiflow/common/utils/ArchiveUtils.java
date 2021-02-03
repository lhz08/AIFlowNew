package com.bdilab.aiflow.common.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @Decription 压缩包格式，解压算法
 * @author liran
 */
public final class ArchiveUtils {
    public static final String FORMAT_ZIP;
    private static final Set<String> FORMATS;
    private static int BUFFER_SIZE = 2 << 11;

    private ArchiveUtils() {
        throw new AssertionError("can not instances this Class for you!");
    }

    static {
        FORMAT_ZIP = ".zip";
        FORMATS = new HashSet<>();
        FORMATS.add(FORMAT_ZIP);
    }

    /**
     * 根据后缀名判断是否是压缩包格式
     * @param suffixName 后缀名
     */
    public static boolean isArchive(String suffixName){
        return FORMATS.contains(suffixName);
    }

    /**
     * 根据不同格式解压缩
     * @param file 要解压的zip文件对象
     * @param outputDir 要解压到某个指定的目录下
     * @param suffixName 后缀名
     * @throws IOException
     */
    public static void unArchive(File file, String outputDir, String suffixName) throws IOException {
        if (FORMAT_ZIP.equals(suffixName)){
            unZip(file, outputDir);
        }else{
            throw new IOException("无法解压'"+suffixName+"'文件");
        }
    }


    /**
     * 解压缩zipFile
     * @param file 要解压的zip文件对象
     * @param outputDir 要解压到某个指定的目录下
     * @throws IOException
     */
    private static void unZip(File file, String outputDir) throws IOException {
        ZipFile zipFile = null;
        try {
            Charset gbk = Charset.forName("GBK");
            zipFile =  new ZipFile(file, gbk);
            createDirectory(outputDir,null);//创建输出目录

            Enumeration<?> enums = zipFile.entries();
            while(enums.hasMoreElements()){

                ZipEntry entry = (ZipEntry) enums.nextElement();

                if(entry.isDirectory()){//是目录
                    createDirectory(outputDir,entry.getName());//创建空目录
                }else{//是文件
                    File tmpFile = new File(outputDir + File.separator + entry.getName());
                    createDirectory(tmpFile.getParent() + File.separator,null);//创建输出目录
                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = zipFile.getInputStream(entry);
                        out = new FileOutputStream(tmpFile);
                        int length = 0;
                        byte[] b = new byte[BUFFER_SIZE];
                        while ((length = in.read(b)) != -1) {
                            out.write(b, 0, length);
                        }
                    } catch (IOException ex) {
                        throw ex;
                    } finally {
                        try {
                            if(in!=null) in.close();
                            if(out!=null) out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("解压文件出现异常",e);
        }finally{
            try{
                if(zipFile != null){
                    zipFile.close();
                }
            }catch(IOException ex){
                throw new IOException("关闭zipFile出现异常",ex);
            }
        }
    }

    /**
     * 构建目录
     */
    private static void createDirectory(String outputDir,String subDir){
        File file = new File(outputDir);
        if(!(subDir == null || subDir.trim().equals(""))){//子目录不为空
            file = new File(outputDir + File.separator + subDir);
        }
        if(!file.exists()){
            if(!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            file.mkdirs();
        }
    }

    /**
     * 压缩文件或文件夹为zip
     * @param filePath 要压缩的文件或文件夹路径
     * @param outputPath 压缩后的输出路径
     * @throws IOException
     */
    public static void zip(String filePath, String outputPath) throws IOException {
        if(outputPath.startsWith(filePath))
            throw new IOException("输出压缩包不能在要压缩的文件夹内");
        zip(new File(filePath), new FileOutputStream(outputPath));
    }

    public static void zip(File file, OutputStream outputStream) throws IOException {
        if(!file.exists())throw new IOException("occur in ArchiveUtils->zip方法：文件不存在");
        try(ZipOutputStream zos = new ZipOutputStream(outputStream)){

            String relativePath = file.getName();
            if(file.isDirectory()) {
                relativePath += File.separator;
            }
            //递归压缩文件
            zipFile(file, relativePath, zos);
        }catch (IOException e){
            throw new IOException("压缩文件失败",e);
        }
    }

    private static void zipFile(File file, String relativePath, ZipOutputStream zos) throws IOException {
        InputStream is = null;
        try {
            if(!file.isDirectory()) {
                ZipEntry zp = new ZipEntry(relativePath);
                zos.putNextEntry(zp);
                is = new FileInputStream(file);
                byte[] buffer = new byte[BUFFER_SIZE];
                int length = 0;
                while ((length = is.read(buffer)) >= 0) {
                    zos.write(buffer, 0, length);
                }
                zos.flush();
                zos.closeEntry();
            } else {
                String tempPath = null;
                for(File f: file.listFiles())
                {
                    tempPath = relativePath + f.getName();
                    if(f.isDirectory())
                    {
                        tempPath += File.separator;
                    }
                    zipFile(f, tempPath, zos);
                }
            }
        } catch (Exception e) {
            throw new IOException("zipFile方法出错",e);
        } finally {
            try {
                if(is != null)
                {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
