package me.next.webviewscreenshot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static android.view.View.MeasureSpec.UNSPECIFIED;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ScreenShot";

    private int startPosition;
    private int endPosition;
    private ImageView ivImg;
    private WebView mWebView;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.wv_webview_activity);
        mScrollView = (ScrollView) findViewById(R.id.scroll_view);

        initWebView();

        mWebView.loadUrl("http://www.baidu.com");
        mWebView.setFocusableInTouchMode(true);
        mWebView.requestFocus();

        final FrameLayout fl_web_webview_activity = (FrameLayout)findViewById(R.id.fl_web_webview_activity);
        ivImg = (ImageView) findViewById(R.id.iv_img);

        findViewById(R.id.tv_screen_shot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bitmap b = loadBitmapFromView(fl_web_webview_activity, fl_web_webview_activity.getWidth(), fl_web_webview_activity.getHeight());
//                ivImg.setVisibility(View.VISIBLE);
//                ivImg.setImageBitmap(b);

                endPosition = mScrollView.getScrollY() + mScrollView.getHeight();
                initScreenShot();
            }
        });
    }

    private void initWebView() {
        if (Build.VERSION.SDK_INT >= 21) {
            WebView.enableSlowWholeDocumentDraw();
        }
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());
    }

    public void initScreenShot() {
        mWebView.measure(View.MeasureSpec.makeMeasureSpec(UNSPECIFIED, UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, UNSPECIFIED));
        mWebView.layout(0, 0, mWebView.getMeasuredWidth(), this.endPosition);
        mWebView.setDrawingCacheEnabled(true);
        mWebView.buildDrawingCache();

        new CaptureTask().execute("");
    }

    private class CaptureTask extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "onPreExecute() called");
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Log.i(TAG, "doInBackground(Params... params) called");
            return takeScreenShot();
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            super.onPostExecute(s);
            ivImg.setImageBitmap(s);
            ivImg.setVisibility(View.VISIBLE);
            Log.i(TAG, "onPostExecute(Result result) called");
        }

    }


    private Bitmap takeScreenShot() {
        Bitmap bitmap = Bitmap.createBitmap(mWebView.getMeasuredWidth(), endPosition - startPosition, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, new Paint());
        canvas.translate(0.0f, (float) (-this.startPosition));
        mWebView.draw(canvas);
        mWebView.destroyDrawingCache();
//        SaveImage(bitmap);
//        saveBitmap2file(getApplicationContext(), bitmap);
        return bitmap;
    }

    public static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory()
                .getPath(), "TestFolder");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/"
                + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    // 图片转为文件
    public static boolean saveBitmap2file(Context context,  Bitmap bmp) {
        String filePath = getFilename();
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bmp.compress(format, quality, stream);
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }
}
