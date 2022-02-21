package com.mrducan.floatscreenball;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class MainActivity extends AppCompatActivity {

    private MyApplication myApplication;
    private  Intent myIntent;

    private EditText edtIp;
    private InputMethodManager imm;
    private RadioGroup rgIfSaveImage;
    private Spinner spinIp;
    private ImageView ivAdd;

    private String IP ;

    private boolean ifEditClick = false;
    private boolean ifExsitServer =false;

    private LinkedList<IpItem> ipItems;
    private IpAdapter ipAdapter;

    private String filePath;

    private SharedPreferences preference;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtIp = findViewById(R.id.et_ip);
        rgIfSaveImage = findViewById(R.id.rg_ifSaveImage);
        spinIp = findViewById(R.id.spin_ip);
        ivAdd = findViewById(R.id.iv_add);
        ipItems = new LinkedList<>();
        spinIp =findViewById(R.id.spin_ip);

        ipAdapter = new IpAdapter(ipItems,MainActivity.this,edtIp,IP);
        spinIp.setAdapter(ipAdapter);

        preference = (SharedPreferences) this.getPreferences(Context.MODE_PRIVATE);
        filePath =preference .getString("path","");
        if (!filePath.isEmpty()){
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filePath));
                ipItems = (LinkedList<IpItem>) objectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }


        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IP = edtIp.getText().toString();
                if (!IP.isEmpty() && !(ipItems.contains(IP))){
                    ipItems.add(new IpItem(R.drawable.ico,IP));
                    //点击后创建新的adpter
                    ipAdapter = new IpAdapter(ipItems,MainActivity.this,edtIp,IP);
                    spinIp.setAdapter(ipAdapter);
                }
            }
        });


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

    @Override
    protected void onStop() {
        super.onStop();
        File file = new File("ipSave");
        filePath = file.getPath();
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor =  preference.edit();
        editor.putString("path",filePath);
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStream.writeObject(ipItems);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_btn:
                if (IP.isEmpty() || IP == null){
                    Toast.makeText(MainActivity.this,"请输入IP地址",Toast.LENGTH_LONG).show();
                    return;
                }
                myApplication.setIP(IP);
                startService(myIntent);
                ifExsitServer = true;
                break;

            case R.id.btn_confirm_ip:
                IP = edtIp.getText().toString();
                if (IP.isEmpty() || IP == null){
                    Toast.makeText(MainActivity.this,"请输入IP地址",Toast.LENGTH_SHORT).show();
                    return;
                }
                myApplication.setIP(IP);
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

            case R.id.iv_icon:

                break;
        }
    }
}