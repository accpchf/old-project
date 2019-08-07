package com.ks0100.common.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Encoder;

/**
 * 图片压缩类
 *
 * 创建日期：2014-12-11
 * @author chengls
 */
public class PictureUtil{
	private File file = null; // 文件对象
	private InputStream in;//输入流
	private String inputDir; // 输入图路径
//	private String inputFileName; // 输入图文件名
//	private String outputFileName; // 输出图文件名
	private int outputWidth = 70; // 默认输出图片宽
	private int outputHeight = 70; // 默认输出图片高
	private boolean proportion = true; // 是否等比缩放标记(默认为等比缩放)
	
	/**
	 * 图片转成base64字符串后，需加该头部标示，才能读出该图片
	 */
	public static final String BASE64IMAGEHEAER = "data:image/gif;base64,";
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	public PictureUtil(){
		
	}
/*	public PictureUtil(InputStream in, int outputWidth,
			int outputHeight) {
		super();
		this.in = in;
		this.outputWidth = outputWidth;
		this.outputHeight = outputHeight;
	}*/
	
	public PictureUtil(InputStream in) {
		this.in = in;
	}
	
	public PictureUtil(String inputDir) {
		//super();
		this.inputDir = inputDir;
		changeToInputStream() ;
		
	}
	
	 public  void changeToInputStream() {
         file = new File(inputDir);
         try {
             in = new FileInputStream(file);
         } catch (FileNotFoundException e) {
             logger.error("", e);
         }
     }
	 
	 public byte[]  changeToBytes(){
		 byte[] result = null;
    	 ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
    	 try{
    		 
        	 byte[] b = new byte[4096];  
             int n;  
             while ((n = in.read(b)) != -1) {  
                 out.write(b, 0, n);  
             }  
             result = out.toByteArray();
 		}catch(IOException ioe){
 			logger.error("", ioe);
 		}finally{
 			if(out!=null){
 				try {
 					out.close();
 				} catch (IOException e) {
 					logger.error("",e);
 				}
 				out=null;
 			}
 		}

         return result;
	 }
	/**
	 * 按条件压缩图片
	 *
	 * @return
	 * @throws Exception
	 * 创建日期：2014-12-11
	 * 修改说明：
	 * @author chengls
	 * @throws IOException 
	 */
	public InputStream toComprress() {
		
		InputStream is=null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();  
		try{
			Image img = ImageIO.read(in);
			BufferedImage tag = null;
			if (img.getWidth(null) != -1) {
				int newWidth;
				int newHeight;
				// 判断是否是等比缩放
				if (this.proportion == true) {
					// 为等比缩放计算输出的图片宽度及高度
					double rate1 = ((double) img.getWidth(null))/ (double) outputWidth + 0.1;
					double rate2 = ((double) img.getHeight(null))/ (double) outputHeight + 0.1;
					// 根据缩放比率大的进行缩放控制
					double rate = rate1 > rate2 ? rate1 : rate2;
					newWidth = (int) (((double) img.getWidth(null)) / rate);
					newHeight = (int) (((double) img.getHeight(null)) / rate);
				} else {
					newWidth = outputWidth; // 输出的图片宽度
					newHeight = outputHeight; // 输出的图片高度
				}
				tag = new BufferedImage((int) newWidth,(int) newHeight, BufferedImage.TYPE_INT_RGB);

				/*
				 * Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
				 */
				tag.getGraphics().drawImage(
						img.getScaledInstance(newWidth, newHeight,Image.SCALE_SMOOTH), 0, 0, null);
			}
			
			ImageIO.write(tag, "jpg", os);  
			is = new ByteArrayInputStream(os.toByteArray()); 
		}catch(IOException ioe){
			logger.error("", ioe);
		}finally{
			if(os!=null){
				try {
					os.close();
				} catch (IOException e) {
					logger.error("",e);
				}
				os=null;
			}
			
		}

		return is;
	}
	
	
	public String changeToBASE64EncoderStr()throws IOException{
		return changeToBASE64EncoderStr(in);
	}
	/**
	 * 把图片InputStream 转换成BASE64Encoder字符串
	 * @param nowInput
	 * @return
	 */
	public String changeToBASE64EncoderStr(InputStream nowInput) throws IOException{
		String logo =null;

		byte[] picData = new byte[nowInput.available()];
    	nowInput.read(picData);
		BASE64Encoder encoder = new BASE64Encoder();
    	logo = encoder.encode(picData);
    	Pattern p = Pattern.compile("\\s*|\t|\r|\n");
    	Matcher m = p.matcher(logo);
        logo = m.replaceAll("");
        logo = BASE64IMAGEHEAER + logo;
    	return logo;
	}
	
	public static String changeToBASE64EncoderStr(byte[] picData) {
		String logo =null;
		if(picData!=null&&picData.length>0){
			BASE64Encoder encoder = new BASE64Encoder();
	    	logo = encoder.encode(picData);
	    	Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	    	Matcher m = p.matcher(logo);
	        logo = m.replaceAll("");
	        logo = BASE64IMAGEHEAER + logo;
		}

    	return logo;
	}
	
	public void closeInputStream(){
		if(in!=null){
			try{
				in.close();
			}catch(Exception ex){
				logger.error("",ex);
			}finally{
				in=null;
			}
		}
	}
	
	public void closeInputStream(InputStream nowInput){
		if(nowInput!=null){
			try{
				nowInput.close();
			}catch(Exception ex){
				logger.error("",ex);
			}finally{
				nowInput=null;
			}
		}
	}
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
/*	public InputStream getIn() {
		return in;
	}
	public void setIn(InputStream in) {
		this.in = in;
	}*/
	public String getInputDir() {
		return inputDir;
	}
	public void setInputDir(String inputDir) {
		this.inputDir = inputDir;
	}
	public int getOutputWidth() {
		return outputWidth;
	}
	public void setOutputWidth(int outputWidth) {
		this.outputWidth = outputWidth;
	}
	public int getOutputHeight() {
		return outputHeight;
	}
	public void setOutputHeight(int outputHeight) {
		this.outputHeight = outputHeight;
	}
	public boolean isProportion() {
		return proportion;
	}
	public void setProportion(boolean proportion) {
		this.proportion = proportion;
	}
	
	
}