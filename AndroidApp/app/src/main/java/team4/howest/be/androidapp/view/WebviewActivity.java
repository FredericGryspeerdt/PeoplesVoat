package team4.howest.be.androidapp.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import team4.howest.be.androidapp.R;

public class WebviewActivity extends AppCompatActivity {

    public static final String URL = "URL";
    private String url;

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_webview);




        mWebView = (WebView) findViewById(R.id.webView);

        Bundle extras = getIntent().getExtras();

        if (extras != null){
            url = extras.getString(URL);
        }



        // Simplest usage: note that an exception will NOT be thrown
        // if there is an error loading this page (see below).
        //webview.loadUrl(url);

        // Let's display the progress in the activity title bar, like the
        // browser app does.

        mWebView.getSettings().setJavaScriptEnabled(true);

        final Activity activity = this;
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });

        //Now all links the user clicks load in your WebView.
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Toast.makeText(activity, "Oh no! " + error.toString(), Toast.LENGTH_SHORT).show();
            }

        });
        WebSettings webSettings = mWebView.getSettings();
        //webview instellen
        webSettings.setUseWideViewPort(true); //content in webview wordt aangepast aan grote scherm
        webSettings.setLoadWithOverviewMode(true); //Sets whether the WebView loads pages in overview mode, that is, zooms out the content to fit on screen by width. This setting is taken into account when the content width is greater than the width of the WebView control, for example, when getUseWideViewPort() is enabled. The default is false.
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING); //grote tekst in webview wordt automatisch aangepast om leesbaarder te maken
        webSettings.setBuiltInZoomControls(true); //user kan zoomen in webview

        //content laden
        mWebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        //wanneer gebruiker op back drukt in webview: kijk of pagina's op de stack zijn, zo ja -> keer terug naar vorige pagina in webview
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
