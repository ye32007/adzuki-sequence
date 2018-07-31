package com.adzuki.sequence.biz.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappedFileUtil {

	private final static Logger  log = LoggerFactory.getLogger(MappedFileUtil.class.getName());
	// 文件名
    private String fileName;
 
    // 文件所在目录路径
    private String fileDirPath;
 
    // 文件对象
    private File file;
 
    private MappedByteBuffer mappedByteBuffer;
    private FileChannel fileChannel;
    private boolean boundSuccess = false;
 
    private int MAX_FILE_SIZE = 6;
     
    private int MAX_FLUSH_DATA_SIZE = 1024 * 512;
    
    private final static int MAX_WRITE_INTERVAL = 10000;
    
    private int WRITE_INTERVAL = -1;

	private int MAX_FLUSH_TIME_GAP = 1000*60;
 
    private int writePosition = 0;
 
    private long lastFlushTime;
 
    private int lastFlushFilePosition = 0;
     
    public MappedFileUtil(String fileName, String fileDirPath) {
        this.fileName = fileName;
        this.fileDirPath = fileDirPath;
        File path = new File(fileDirPath);
        if(!path.exists()){
        	try {
        		path.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.file = new File(fileDirPath + "/" + fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
 
    /**
     * 
     * 内存映照文件绑定
     * @return
     */
    public boolean boundChannelToByteBuffer() {
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            this.fileChannel = raf.getChannel();
            
            this.mappedByteBuffer = this.fileChannel
                    .map(FileChannel.MapMode.READ_WRITE, 0, MAX_FILE_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
            this.boundSuccess = false;
            return false;
        }
        this.boundSuccess = true;
        return true;
    }
    
    /**
     * 读文件内容,可用读锁
     * @return
     * @throws IOException 
     */
    public String readData(){ 
//    	if (!boundSuccess) {
//    		synchronized (this) {
//    			if (!boundSuccess) {
//    				boundChannelToByteBuffer();
//    			}
//			}
//        }
    	//如果文件已经被占用，抛出异常
    	try {
			return FileUtils.readFileToString(file);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(file.getPath()+" is busy.");
		}
    }
    
    public long readModifyTime(){
    	return file.lastModified();
    }
    
    public long readLength(){
    	return file.length();
    }
     
    /**
     * 写数据：先将之前的文件删除然后重新
     * @param data
     * @return
     */
    public synchronized boolean writeData(byte[] data) {
    	if (!boundSuccess) {
//    		synchronized (this) {
//    			if (!boundSuccess) {
    				boundChannelToByteBuffer();
//    			}
//			}
        }
    	
    	mappedByteBuffer.clear();
    	mappedByteBuffer.put(data);
    	WRITE_INTERVAL++;
    	
    	// 检查是否需要把内存缓冲刷到磁盘
        if ( WRITE_INTERVAL >= this.MAX_WRITE_INTERVAL || WRITE_INTERVAL == 0 || 
             System.currentTimeMillis() - lastFlushTime > this.MAX_FLUSH_TIME_GAP ) {
        	log.info(file.getName() + " write io.... "+new String(data));
	    	WRITE_INTERVAL = 0;
	    	this.lastFlushTime = System.currentTimeMillis();
	    	file.setLastModified(lastFlushTime);
//	    	mappedByteBuffer.flip();
	    	mappedByteBuffer.force();
        }
    	
        return true;
    }
    
    public synchronized boolean writeBioData(byte[] data) {
    	try {
			FileUtils.writeByteArrayToFile(file, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return true;
    }
     
    /**
     * 在文件末尾追加数据
     * @param data
     * @return
     * @throws Exception
     */
    public synchronized boolean appendData(byte[] data) throws Exception {
        if (!boundSuccess) {
            boundChannelToByteBuffer();
        }
         
        writePosition = writePosition + data.length;
        if (writePosition >= MAX_FILE_SIZE) {   // 如果写入data会超出文件大小限制，不写入
            flush();
            writePosition = writePosition - data.length;
            System.out.println("File=" 
                                + file.toURI().toString() 
                                + " is written full.");
            System.out.println("already write data length:" 
                                + writePosition
                                + ", max file size=" + MAX_FILE_SIZE);
            return false;
        }
 
        this.mappedByteBuffer.put(data);
 
        // 检查是否需要把内存缓冲刷到磁盘
        if ( (writePosition - lastFlushFilePosition > this.MAX_FLUSH_DATA_SIZE)
             ||
             (System.currentTimeMillis() - lastFlushTime > this.MAX_FLUSH_TIME_GAP
              && writePosition > lastFlushFilePosition) ) {
            flush();   // 刷到磁盘
        }
         
        return true;
    }
 
    public synchronized void flush() {
        this.mappedByteBuffer.force();
        this.lastFlushTime = System.currentTimeMillis();
        this.lastFlushFilePosition = writePosition;
    }
    
    public static void unmap(final MappedByteBuffer mappedByteBuffer) {
        try {
            if (mappedByteBuffer == null) {
                return;
            }
             
            mappedByteBuffer.force();
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                @SuppressWarnings("restriction")
                public Object run() {
                    try {
                        Method getCleanerMethod = mappedByteBuffer.getClass()
                                .getMethod("cleaner", new Class[0]);
                        getCleanerMethod.setAccessible(true);
                        sun.misc.Cleaner cleaner = 
                                (sun.misc.Cleaner) getCleanerMethod
                                    .invoke(mappedByteBuffer, new Object[0]);
                        cleaner.clean();
                         
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println("clean MappedByteBuffer completed");
                    return null;
                }
            });
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    public long getLastFlushTime() {
        return lastFlushTime;
    }
 
    public String getFileName() {
        return fileName;
    }
 
    public String getFileDirPath() {
        return fileDirPath;
    }
 
    public boolean isBundSuccess() {
        return boundSuccess;
    }
 
    public File getFile() {
        return file;
    }
 
    public int getMaxFileSize() {
        return MAX_FILE_SIZE;
    }
    
    //为了方便加上长度设置，调用此方法要特别注意
    public void setMaxFileSize(int size) {
        MAX_FILE_SIZE = size;
    }
 
    public long getWritePosition() {
        return writePosition;
    }
 
    public long getLastFlushFilePosition() {
        return lastFlushFilePosition;
    }
 
    public long getMAX_FLUSH_DATA_SIZE() {
        return MAX_FLUSH_DATA_SIZE;
    }
 
    public long getMAX_FLUSH_TIME_GAP() {
        return MAX_FLUSH_TIME_GAP;
    }

	public void setInitWRITE_INTERVAL() {
		WRITE_INTERVAL = -1;
	}
	
	
	
	
	
	
	
	public static void main(String[] args) {
		try {
			final MappedFileUtil util = new MappedFileUtil("aaa.uid", "c:\\");
//			util.writeData("0".getBytes());
			long now = System.currentTimeMillis();

			for (int i = 0; i < 100; i++) {
				Thread a = new Thread() {
					public void run() {
						for (int i = 0; i < 9999; i++) {
//							util.writeBioData(("gggg"+i).getBytes());
//							byte[] data = (""+i).getBytes();
//							byte[] data = new byte[] {(byte)(i),(byte)(i>> 8),(byte)(i>> 16),(byte)(i>> 24)};
						}

					}
				};
				a.start();
				a.join();
			}
//			util.flush();
			long end = System.currentTimeMillis();
			System.out.println("======" + (end - now));
		} catch (Exception e) {
			e.printStackTrace();
		}
			

		
	}

}
