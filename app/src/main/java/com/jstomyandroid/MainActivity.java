package com.jstomyandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "CameraWebviewActivity";

    private Button bt;
    private WebView wv;
    public String fileFullName;//照相后的照片的全整路径
    private boolean fromTakePhoto; //是否是从摄像界面返回的webview
    private ImageView show;

    // 记录文件保存位置
    private String mFilePath;
    private FileInputStream is = null;
    private Uri uri;
    private String result = "";//拍照后保存的图片字符串数据。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    public void initViews() {

//        bt = (Button) findViewById(R.id.bt);
//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("----------------");
//                takePhoto(Math.random() * 1000 + 1 + ".jpg");
//            }
//        });
        show = (ImageView) findViewById(R.id.show);

        wv = (WebView) findViewById(R.id.wv);
        WebSettings setting = wv.getSettings();
        setting.setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        wv.setWebChromeClient(new WebChromeClient() {
            @Override//实现js中的alert弹窗在Activity中显示
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.d(TAG, message);
                result.confirm();
                return true;
            }
        });
        wv.loadUrl("file:///android_asset/index.html");
        final Handler mHandler = new Handler();
        //webview增加javascript接口，监听html页面中的js点击事件
        wv.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public String clickOnAndroid() {//将被js调用
                mHandler.post(new Runnable() {
                    public void run() {
                        fromTakePhoto = true;
                        //调用 启用摄像头的自定义方法
//                        takePhoto("testimg" + Math.random() * 1000 + 1 + ".jpg");
//                        System.out.println("========fileFullName: " + fileFullName);
                        mFilePath = Environment.getExternalStorageDirectory().getPath();
                        startCarmera(1);
                    }
                });
                return fileFullName;
            }
        }, "demo");
    }

    private void testEvaluateJavascript(WebView webView) {
        webView.evaluateJavascript("getGreetings()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e("onReceiveValue", "onReceiveValue value=" + value);
            }});
    }

    /*
     * 调用摄像头的方法
     */
//    public void takePhoto(String filename) {
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_MEDIA_TITLE, "TakePhoto");
//
//        //判断是否有SD卡
//        String sdDir = null;
//        boolean isSDcardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//        if (isSDcardExist) {
//            sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();
//        } else {
//            sdDir = Environment.getRootDirectory().getAbsolutePath();
//        }
//        //确定相片保存路径
//        String targetDir = sdDir + "/" + "webview_camera";
//        File file = new File(targetDir);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        fileFullName = targetDir + "/" + filename;
//        System.out.println("----taking photo fileFullName: " + fileFullName);
//        //初始化并调用摄像头
//        intent.putExtra(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileFullName)));
//        startActivityForResult(intent, 1);
//    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     * 重写些方法，判断是否从摄像Activity返回的webview activity
     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        System.out.println("----requestCode: " + requestCode + "; resultCode " + resultCode + "; fileFullName: " + fileFullName);
//        if (fromTakePhoto && requestCode ==1 && resultCode ==-1) {
//            wv.loadUrl("javascript:wave2('" + fileFullName + "')");
//        } else {
//            wv.loadUrl("javascript:wave2('Please take your photo')");
//        }
//        fromTakePhoto = false;
//        if(data!=null){
//           Bundle bundle= data.getExtras();
//            if(bundle!=null){
//                Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
//                String result2Server=compressBitmap(bitmap);
//                Log.e("result2Server",result2Server.length()+"");
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
    /**
     * 调用系统相机拍照
     */
    // 启动相机
    private void startCarmera(int nums) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mFilePath = mFilePath + "/" + "photo" + nums + ".png";
        uri = Uri.fromFile(new File(mFilePath));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != RESULT_OK) {
            if (requestCode == 1) {
                Bitmap bitmap = null;
                DisplayMetrics metrics=new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int widthPixels=metrics.widthPixels;
                int heightPixels=metrics.heightPixels;

                if (fromTakePhoto && requestCode == 1 && resultCode == -1) {
                    wv.loadUrl("javascript:wave2('" + mFilePath + "')");
                } else {
                    wv.loadUrl("javascript:wave2('Please take your photo')");
                }
                fromTakePhoto = false;
                try {
                    String tempPath = Environment.getExternalStorageDirectory() + "/temp.jpg";
                    bitmap = BitmapUtils.getFitSampleBitmap(getContentResolver().openInputStream(uri),
                            tempPath, widthPixels, heightPixels);
                    if (bitmap != null) {
                        show.setImageBitmap(bitmap);
                        result = compressBitmap(bitmap);
                        Log.e("result", result.length() + "");
                    }
//                    File file=new File(mFilePath);
//                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private String compressBitmap(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            bm.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        String uploadBuffer = new String(Base64.encodeToString(baos.toByteArray(), 1));
        return uploadBuffer;
    }
}
