package xyz.bcfriends.dra;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

public class WebViewActivity extends AppCompatActivity {

    public static FrameLayout mContainer;
    private WebView mWebView; // 웹뷰 선언
    private WebView mWebViewPop;
    private WebSettings mWebSettings; //웹뷰세팅

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        // 웹뷰 시작
        mContainer = (FrameLayout) findViewById(R.id.webViewFrame);
        mWebView = (WebView) findViewById(R.id.webView);
        mWebViewPop = (WebView) findViewById(R.id.webView);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        mWebView.loadUrl("http://192.168.219.105:3000/test"); // 접속 URL

        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());
        Log.d("tlqkf", "A");
    }

    public final class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            Log.d("tlqkf", request.getUrl().toString());
            return true;
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

            Log.d("tlqkf", String.valueOf(resultMsg.obj));

            // window.opener 시
            mWebViewPop = new WebView(view.getContext());
            mWebViewPop.getSettings().setJavaScriptEnabled(true);
            mWebViewPop.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            mWebViewPop.getSettings().setSupportMultipleWindows(true);
            mWebViewPop.getSettings().setDomStorageEnabled(true);
            mWebViewPop.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onCloseWindow(WebView window) {
                    mContainer.removeView(window);
                    window.destroy();
                }
            });

            mContainer.addView(mWebViewPop);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebViewPop);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.e("asdasdasda", consoleMessage.message());
            Log.d("asdasdasda", String.valueOf(consoleMessage.lineNumber()));
            Log.d("asdasdasda", String.valueOf(consoleMessage.sourceId()));
            return super.onConsoleMessage(consoleMessage);
        }
    }

}