package com.yn.serialporttest.Util;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

/**
 * Created by xbp on 2018/4/3.
 */

public class SerialPortUtils{

    private final String TAG = "SerialPortUtils";
    private static final String path = "dev/ttyS3";
    private static  final int baudrate = 9600;
    public boolean serialPortStatus = false;//是否打开串口标志
    public String data_;
    public boolean threadStatus;//线程状态，为了安全终止线程

    public SerialPort mSerialPort = null;
    public InputStream mInputStream = null;
    public OutputStream mOutputStream = null;


    /**
     * 打开串口
     * @return  serialPort串口对象
     */
    public SerialPort openSerialPort(){
        try{
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
            this.serialPortStatus = true;
            threadStatus = false;

            //获取打开的串口中的输入输出流，以便于串口数据的收发
            mInputStream = mSerialPort.getInputStream();
            mOutputStream = mSerialPort.getOutputStream();

            new ReadThread().start();//开启线程监控是否有数据的收发
        }catch (IOException e){
            Log.e(TAG, "openSerialPort:打开串口异常：" + e.toString());
            return mSerialPort;
        }
        Log.d(TAG, "openSerialPort: 打开串口");
        return mSerialPort;
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort(){
        try{
            if (mInputStream != null){
                mInputStream.close();
            }
            if (mOutputStream != null){
                mOutputStream.close();
            }
            this.threadStatus = true;
            this.serialPortStatus = false;
            mSerialPort.close();
        }catch (IOException e){
            Log.e(TAG, "closeSerialPort: 关闭串口异常" + e.toString());
            return;
        }
        Log.d(TAG, "closeSerialPort: 串口关闭成功");
    }

    public void sendSerialPort(String data){
        Log.d(TAG, "sendSerialPort: 发送数据" + data);
        try{
            byte[] sendData = data.getBytes();
            this.data_ = new String(sendData);
            if (sendData.length > 0){
                mOutputStream.write(sendData);
                mOutputStream.write('\n');
                mOutputStream.flush();
                Log.d(TAG, "sendSerialPort: 发送串口数据成功");
            }
        }catch (IOException e){
            Log.e(TAG, "sendSerialPort: 串口发送数据失败 " + e.toString());
        }
    }

    private class ReadThread extends Thread{
        @Override
        public void run() {
            super.run();
            //判断线程是否安全进行，更安全的结束线程
            while (!threadStatus){
                Log.d(TAG, "进入线程run");

                byte[] buffer = new byte[64];
                int size;
                try{
                    size = mInputStream.read(buffer);
                    if (size>0){
                        Log.d(TAG, "run: 接收到数据大小：" + size);
                        onDataReceiveListener.onDataReceive(buffer, size);
                    }
                }catch (IOException e){
                    Log.e(TAG, "run: 数据读取异常" + e.toString());
                }
            }
        }
    }

    public OnDataReceiveListener onDataReceiveListener = null;

    public static interface OnDataReceiveListener{
        void onDataReceive(byte[] buffer, int size);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener listener){
        this.onDataReceiveListener = listener;
    }

}
