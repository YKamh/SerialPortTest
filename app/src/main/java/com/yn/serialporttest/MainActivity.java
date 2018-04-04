package com.yn.serialporttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yn.serialporttest.Util.SerialPortUtils;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getName();

    private EditText et_text;
    private Button btn_send;
    private SerialPortUtils mSerialPort = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSerialPort = new SerialPortUtils();
        mSerialPort.openSerialPort();
        mSerialPort.setOnDataReceiveListener(new SerialPortUtils.OnDataReceiveListener() {
            @Override
            public void onDataReceive(final byte[] buffer, final int size) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "onCreate: 接收到信息" + buffer +"大小" + size );
                    }
                });
            }
        });

        et_text = (EditText) findViewById(R.id.et_text);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = et_text.getText().toString();
                mSerialPort.sendSerialPort(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSerialPort!=null){
            mSerialPort.closeSerialPort();
        }
    }
}
