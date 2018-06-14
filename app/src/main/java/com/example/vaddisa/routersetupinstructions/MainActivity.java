package com.example.vaddisa.routersetupinstructions;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.vaddisa.routersetupinstructions.Constants.DEVELOPER_KEY;
import static com.example.vaddisa.routersetupinstructions.Constants.ROUTER_GATEWAY;
import static com.example.vaddisa.routersetupinstructions.Constants.VIDEO;


public class MainActivity extends YouTubeFailureRecoveryActivity {


    private String video_id;
    private YouTubePlayerView playerView;
    private WebView webView;
    ProgressDialog progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerView = (YouTubePlayerView) findViewById(R.id.videoView);
        playerView.initialize(DEVELOPER_KEY, this);

        webView = (WebView) findViewById(R.id.webView);
        setWebView();

    }

    private void setWebView() {
        progressBar = new ProgressDialog(MainActivity.this);
        progressBar.setMessage("webview loading...");

        webView.setWebViewClient(new MyWebViewClient());
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDisplayZoomControls(true);
        webView.loadUrl(ROUTER_GATEWAY);

        setWebViewBackButton();
    }

    private void setWebViewBackButton() {
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    WebView webView = (WebView) v;

                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webView.canGoBack()) {
                                webView.goBack();
                                return true;
                            }
                            break;
                    }
                }

                return false;
            }
        });


        
    }


    private String extractVideoId(String videoUrl) {
        String pattern = "(?:videos\\/|v=)([\\w-]+)";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(videoUrl);

        if (matcher.find()) {
            System.out.println(matcher.group().substring(2));
            video_id = matcher.group().substring(2);
        }
        return video_id;
    }


    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return playerView;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {

        player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

        if (!wasRestored) {
            player.loadVideo(extractVideoId(VIDEO));
        }

    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (!progressBar.isShowing()) {
                progressBar.show();
            }
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webView.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }
        }
    }
}
