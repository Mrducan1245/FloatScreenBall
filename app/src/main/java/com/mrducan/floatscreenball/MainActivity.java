package com.mrducan.floatscreenball;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MyApplication myApplication;
    private  Intent myIntent;

    private EditText edtIp;
    private InputMethodManager imm;
    private RadioGroup rgIfSaveImage;

    private String IP ;

    private boolean ifEditClick = false;
    private boolean ifExsitServer =false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtIp = findViewById(R.id.et_ip);
        rgIfSaveImage = findViewById(R.id.rg_ifSaveImage);

        rgIfSaveImage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.ib_yes){
                    myApplication.setIfsaveImage();
                }
            }
        });


        edtIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ifEditClick = true;
                edtIp.requestFocus();
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtIp,InputMethodManager.SHOW_FORCED);
            }
        });

        myApplication = (MyApplication) getApplication();

        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = mediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, 333);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 333 && data != null){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                myApplication.setMediaProjectionManager(mediaProjectionManager);
                myIntent = new Intent(MainActivity.this,FloatBallService.class);

                myIntent.putExtra("code",resultCode);
                myIntent.putExtra("data",data);
            }
        }
    }



    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        String TAG = "MainActivity";
        switch (view.getId()){
            case R.id.start_btn:
                if (IP==null){
                    IP = "192.168.43.17";
                    Toast.makeText(MainActivity.this,"未输入IP地址，将采用默认地址："+IP,Toast.LENGTH_LONG).show();
                    myApplication.setIP(IP);
                }
                startService(myIntent);
                ifExsitServer = true;
                break;
            case R.id.btn_confirm_ip:
                IP = edtIp.getText().toString();
                if (IP==null){
                    IP = "192.168.43.17";
                    Toast.makeText(MainActivity.this,"未输入IP地址，将采用默认地址："+IP,Toast.LENGTH_LONG).show();
                    myApplication.setIP(IP);
                }
                if (ifEditClick){
                    edtIp.clearFocus();
                    imm.hideSoftInputFromWindow(edtIp.getWindowToken(),0);
                }
                break;
            case R.id.close_btn:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ScreenShot.stopMediaProjection();
                }
                stopService(myIntent);
                ifExsitServer = false;
                break;
        }
    }
}