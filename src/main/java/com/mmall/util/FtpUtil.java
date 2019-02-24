package com.mmall.util;

import com.google.common.collect.Lists;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author Maoxin
 * @ClassName FtpUtil
 * @date 2/14/2019
 */
public class FtpUtil {

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip","47.93.241.68");
    private static String ftpUser=PropertiesUtil.getProperty("ftp.user","ftpuser");;
    private static String ftpPassword =PropertiesUtil.getProperty("ftp.pass","20165919") ;

    private String ip;
    private int port;
    private String user;
    private String password;

    private FTPClient ftpClient;

    public FtpUtil(String ip,Integer port,String user,String password){
        this.ip = ip;
        this.port = port;
        this.user =user;
        this.password = password;
    }

    public static boolean uploadFile(List<File> fileList){
        boolean successed = true;
        FtpUtil ftpUtil = new FtpUtil(ftpIp,21,ftpUser,ftpPassword);
        try {
            successed = ftpUtil.uploadFile(fileList,"");
        } catch (IOException e) {
            successed = false;
            e.printStackTrace();
        }
        return successed;

    }
    //返回值指明是否上传成功，如果任然抛出IOException的异常则代表在关闭FTPClient的时候出现错误
    public boolean uploadFile(List<File> fileList,String remoteDir) throws IOException {
        boolean upLoaded = false;
        if (connect()){
            try {
                //切换工作文件夹
                ftpClient.changeWorkingDirectory(remoteDir);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("utf-8");
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                //上传文件
                for(File file:fileList){
                    FileInputStream fis = new FileInputStream(file);
                    ftpClient.storeFile(file.getName(),fis);
                    fis.close();
                }
                upLoaded = true;
            } catch (IOException e) {
                //上传文件异常
                e.printStackTrace();
            }finally {
                ftpClient.disconnect();
            }
        }
        return upLoaded;

    }

    private boolean connect(){
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,password);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static void main(String[] args) {
        File file = new File("C:\\Users\\lumao\\Pictures\\showimage.jpg");
        FtpUtil.uploadFile(Lists.newArrayList(file));
    }
}
