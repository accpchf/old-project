package com.ks0100.common.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

/**
 * FTP客户端
 * 
 * @author summersun_ym
 * @version $Id: FTPClientTemplate.java 2010-11-22 上午12:54:47 $
 */
public class FTPClientTemplate {
    //---------------------------------------------------------------------
    // Instance data
    //---------------------------------------------------------------------
    /** logger */
	public static final Logger LOGGER = Logger.getLogger(FTPClientTemplate.class);

    private String         host;
    private int            port=Integer.parseInt(ReadPropertiesUtil.getStringContextProperty("ftp.port"));
    private String         username;
    private String         password;
    private int			   bufferMB =1;
    private boolean        binaryTransfer = true;
    private boolean        passiveMode    = true;
    private String         encoding       = "UTF-8";
    private int            clientTimeout  = 600000;


	public int getBufferMB() {
		return bufferMB*1024*1024;
	}

	public void setBufferMB(int bufferMB) {
		this.bufferMB = bufferMB;
	}

	public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isBinaryTransfer() {
        return binaryTransfer;
    }

    public void setBinaryTransfer(boolean binaryTransfer) {
        this.binaryTransfer = binaryTransfer;
    }

    public boolean isPassiveMode() {
        return passiveMode;
    }

    public void setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public int getClientTimeout() {
        return clientTimeout;
    }

    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }

    //---------------------------------------------------------------------
    // private method
    //---------------------------------------------------------------------
    /**
     * 返回一个FTPClient实例
     * 
     * @throws FTPClientException
     */
    private FTPClient getFTPClient() throws FTPClientException {
        FTPClient ftpClient = new FTPClient(); //构造一个FtpClient实例
        ftpClient.setControlEncoding(encoding); //设置字符集
        
        //连接到ftp服务器
        if(!connect(ftpClient)){
        	 throw new FTPClientException("登录ftp失败！");
        }
        
        //设置为passive模式
        if (passiveMode) {
            ftpClient.enterLocalPassiveMode();
        }
        setFileType(ftpClient); //设置文件传输类型
        
        try {
            ftpClient.setSoTimeout(clientTimeout);
        } catch (SocketException e) {
            throw new FTPClientException("Set timeout error.", e);
        }

        return ftpClient;
    }

    /**
     * 设置文件传输类型
     * 
     * @throws FTPClientException
     * @throws IOException
     */
    private void setFileType(FTPClient ftpClient) throws FTPClientException {
        try {
            if (binaryTransfer) {
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            } else {
                ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
            }
        } catch (IOException e) {
            throw new FTPClientException("Could not to set file type.", e);
        }
    }

    /**
     * 连接到ftp服务器
     * 
     * @param ftpClient
     * @return 连接成功返回true，否则返回false
     * @throws FTPClientException
     */
    public boolean connect(FTPClient ftpClient) throws FTPClientException {
        try {
        	// ftpClient.enterLocalPassiveMode();
            ftpClient.connect(host, port);

            // 连接后检测返回码来校验连接是否成功
            int reply = ftpClient.getReplyCode();

            if (FTPReply.isPositiveCompletion(reply)) {
                //登陆到ftp服务器
                if (ftpClient.login(username, password)) {
                    setFileType(ftpClient);
                    return true;
                }
            } else {
                ftpClient.disconnect();
                throw new FTPClientException("FTP server refused connection.");
            }
        } catch (IOException e) {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect(); //断开连接
                } catch (IOException e1) {
                    throw new FTPClientException("Could not disconnect from server.", e);
                }

            }
            throw new FTPClientException("Could not connect to server.", e);
        }
        return false;
    }

    /**
     * 断开ftp连接
     * 
     * @throws FTPClientException
     */
    private void disconnect(FTPClient ftpClient) throws FTPClientException {
        try {
            ftpClient.logout();
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
                ftpClient=null;
            }
        } catch (IOException e) {
            throw new FTPClientException("Could not disconnect from server.", e);
        }
    }

    //---------------------------------------------------------------------
    // public method
    //---------------------------------------------------------------------
    
    private boolean makeDirectory(FTPClient ftpClient,String serverPath) throws FTPClientException{
    	boolean flag=false;
    	try{
            if(!ftpClient.changeWorkingDirectory(serverPath)){
            	ftpClient.makeDirectory(serverPath);
            	flag=ftpClient.changeWorkingDirectory(serverPath);
            }else{
            	flag=true;
            }
    	}catch(IOException ioe){
    		flag=false;
    		throw new FTPClientException("changeWorkingDirectory fail.", ioe);
    	}
        return flag;
    }
    
    
    
    /**
     * 上传一个本地文件到远程指定文件
     * 
     * @param serverFile 服务器端文件名(包括完整路径)
     * @param localFile 本地文件名(包括完整路径)
     * @return 成功时，返回true，失败返回false
     * @throws FTPClientException
     */
    public boolean put(String serverFile, String serverPath,String localFile) throws FTPClientException {
        return put(serverFile,serverPath, localFile, false);
    }

    /**
     * 上传一个本地文件到远程指定文件
     * 
     * @param serverFile 服务器端文件名(包括完整路径)
     * @param serverPath 服务器端的目录路径，按"/"分隔
     * @param localFile 本地文件名(包括完整路径)
     * @param delFile 成功后是否删除文件
     * @return 成功时，返回true，失败返回false
     * @throws FTPClientException
     */
    public boolean put(String serverFile, String serverPath,InputStream localInput) throws FTPClientException {
        FTPClient ftpClient = null;
       // InputStream input = null;
        try {
            ftpClient = getFTPClient();
            ftpClient.changeToParentDirectory();
            // 处理传输
           // input = new FileInputStream(localInput);
            //先在服务器上建立多级文件目录
            String[] directories=serverPath.split("/");
            for(String dir:directories){
            	if(StringUtils.isNotBlank(dir)){
            		makeDirectory(ftpClient,dir);
            	}
            }
            ftpClient.setBufferSize(getBufferMB()); 
            ftpClient.storeFile(serverFile, localInput);
          //  LOGGER.debug("put " + localInput.);
         //   localInput.close();
  /*          if (delFile) {
                (new File(localFile)).delete();
                LOGGER.debug("delete " + localFile);
            }*/
            return true;
        } catch (FileNotFoundException e) {
            throw new FTPClientException("local file not found.", e);
        } catch (IOException e) {
            throw new FTPClientException("Could not put file to server.", e);
        } finally {
            try {
                if (localInput != null) {
                	localInput.close();
                	localInput=null;
                }
            } catch (Exception e) {
                throw new FTPClientException("Couldn't close FileInputStream.", e);
            }
            if (ftpClient != null) {
                disconnect(ftpClient); //断开连接
            }
        }
    }
    
    /**
     * 上传一个本地文件到远程指定文件
     * 
     * @param serverFile 服务器端文件名(包括完整路径)
     * @param serverPath 服务器端的目录路径，按"/"分隔
     * @param localFile 本地文件名(包括完整路径)
     * @param delFile 成功后是否删除文件
     * @return 成功时，返回true，失败返回false
     * @throws FTPClientException
     */
    public boolean put(String serverFile, String serverPath,String localFile, boolean delFile) throws FTPClientException {
        FTPClient ftpClient = null;
        InputStream input = null;
        try {
            ftpClient = getFTPClient();
            ftpClient.changeToParentDirectory();
            // 处理传输
            input = new FileInputStream(localFile);
            //先在服务器上建立多级文件目录
            String[] directories=serverPath.split("/");
            for(String dir:directories){
            	if(StringUtils.isNotBlank(dir)){
            		makeDirectory(ftpClient,dir);
            	}
            }
            ftpClient.setBufferSize(getBufferMB()); 
            ftpClient.storeFile(serverFile, input);
            LOGGER.debug("put " + localFile);
            input.close();
            if (delFile) {
                (new File(localFile)).delete();
                LOGGER.debug("delete " + localFile);
            }
            return true;
        } catch (FileNotFoundException e) {
            throw new FTPClientException("local file not found.", e);
        } catch (IOException e) {
            throw new FTPClientException("Could not put file to server.", e);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
                throw new FTPClientException("Couldn't close FileInputStream.", e);
            }
            if (ftpClient != null) {
                disconnect(ftpClient); //断开连接
            }
        }
    }

    /**
     * 下载一个远程文件到本地的指定文件
     * 
     * @param serverFile 服务器端文件名(包括完整路径)
     * @param localFile 本地文件名(包括完整路径)
     * @return 成功时，返回true，失败返回false
     * @throws FTPClientException
     */
    public boolean get(String serverFile, String localFile) throws FTPClientException {
        return get(serverFile, localFile, false);
    }

    /**
     * 下载一个远程文件到本地的指定文件
     * 
     * @param serverFile 服务器端文件名(包括完整路径)
     * @param localFile 本地文件名(包括完整路径)
     * @return 成功时，返回true，失败返回false
     * @throws FTPClientException
     */
    public boolean get(String serverFile, String localFile, boolean delFile) throws FTPClientException {
        OutputStream output = null;
        try {
            output = new FileOutputStream(localFile);
            return get(serverFile, output, delFile);
        } catch (FileNotFoundException e) {
            throw new FTPClientException("local file not found.", e);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                throw new FTPClientException("Couldn't close FileOutputStream.", e);
            }
        }
    }
    
    /**
     * 下载一个远程文件到指定的流
     * 处理完后记得关闭流
     * 
     * @param serverFile
     * @param output
     * @return
     * @throws FTPClientException
     */
    public boolean get(String serverFile, OutputStream output) throws FTPClientException {
        return get(serverFile, output, false);
    }
    
    /**
     * 下载一个远程文件到指定的流
     * 处理完后记得关闭流
     * 
     * @param serverFile
     * @param output
     * @param delFile
     * @return
     * @throws FTPClientException
     */
    public boolean get(String serverFile, OutputStream output, boolean delFile) throws FTPClientException {
        FTPClient ftpClient = null;
        try {
            ftpClient = getFTPClient();
            ftpClient.setBufferSize(getBufferMB()); 
            // 处理传输
           
            ftpClient.retrieveFile(serverFile, output);
            if (delFile) { // 删除远程文件
                ftpClient.deleteFile(serverFile);
            }
            return true;
        } catch (IOException e) {
            throw new FTPClientException("Couldn't get file from server.", e);
        } finally {
            if (ftpClient != null) {
                disconnect(ftpClient); //断开连接
            }
        }
    }
    
    /**
     * 从ftp服务器上删除一个文件
     * 
     * @param delFile
     * @return
     * @throws FTPClientException
     */
    public boolean delete(String delFile) throws FTPClientException {
        FTPClient ftpClient = null;
        try {
            ftpClient = getFTPClient();
            ftpClient.deleteFile(delFile);
            return true;
        } catch (IOException e) {
            throw new FTPClientException("Couldn't delete file from server.", e);
        } finally {
            if (ftpClient != null) {
                disconnect(ftpClient); //断开连接
            }
        }
    }
    
    /**
     * 批量删除
     * 
     * @param delFiles
     * @return
     * @throws FTPClientException
     */
    public boolean delete(String[] delFiles) throws FTPClientException {
        FTPClient ftpClient = null;
        try {
            ftpClient = getFTPClient();
            for (String s : delFiles) {
                ftpClient.deleteFile(s);
            }
            return true;
        } catch (IOException e) {
            throw new FTPClientException("Couldn't delete file from server.", e);
        } finally {
            if (ftpClient != null) {
                disconnect(ftpClient); //断开连接
            }
        }
    }

    /**
     * 列出远程默认目录下所有的文件
     * 
     * @return 远程默认目录下所有文件名的列表，目录不存在或者目录下没有文件时返回0长度的数组
     * @throws FTPClientException
     */
    public String[] listNames() throws FTPClientException {
        return listNames(null);
    }

    /**
     * 列出远程目录下所有的文件
     * 
     * @param remotePath 远程目录名
     * @return 远程目录下所有文件名的列表，目录不存在或者目录下没有文件时返回0长度的数组
     * @throws FTPClientException
     */
    public String[] listNames(String remotePath) throws FTPClientException {
        FTPClient ftpClient = null;
        try {
            ftpClient = getFTPClient();
            String[] listNames = ftpClient.listNames(remotePath);
            return listNames;
        } catch (IOException e) {
            throw new FTPClientException("列出远程目录下所有的文件时出现异常", e);
        } finally {
            if (ftpClient != null) {
                disconnect(ftpClient); //断开连接
            }
        }
    }

    public static void main(String[] args) throws FTPClientException {
        FTPClientTemplate ftp = new FTPClientTemplate();
        ftp.setHost("localhost");
       // ftp.setPort(2121);
        ftp.setUsername("admin");
        ftp.setPassword("admin");
       // ftp.setBinaryTransfer(true);
      //  ftp.setPassiveMode(true);
      //  ftp.setEncoding("utf-8");
        
        
       // ftp.put("协同2.pptx", "D:\\协同.pptx");//上传
        //ftp.put("38.5MB.zip","/admin/pic/123" ,"D:\\测试文件\\38.5MB.zip");//上传
       DateTime begin = new DateTime();
       ftp.delete("/admin/pic/123/38.5MB.zip");//下载
       DateTime end = new DateTime();
       System.out.println(end.getMillis()-begin.getMillis());
      //  String[] aa = {"qqq/111.txt", "qqq/222.zip"};
       // ftp.delete(aa);
    }
}
