package dz.storemaster.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.graphics.Color;

public class MainActivity extends Activity {

    private WebView webView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        getWindow().setStatusBarColor(Color.rgb(17, 17, 17));
        getWindow().setNavigationBarColor(Color.rgb(17, 17, 17));

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(244, 244, 241));

        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setSupportMultipleWindows(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setMixedContentMode(
                WebSettings.MIXED_CONTENT_NEVER_ALLOW
        );

        // تفعيل عرض الصفحة بمقياس 67%
        settings.setUseWideViewPort(true);
        webView.setInitialScale(67);

        // الحفاظ على وظائف التكبير دون إظهار أزرار مزعجة
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        CookieManager.getInstance()
                .setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view,
                    WebResourceRequest request
            ) {
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(
                    WebView view,
                    boolean isDialog,
                    boolean isUserGesture,
                    android.os.Message resultMsg
            ) {
                final WebView popup = new WebView(MainActivity.this);

                popup.getSettings().setJavaScriptEnabled(true);
                popup.getSettings().setDomStorageEnabled(true);

                popup.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(
                            WebView v,
                            WebResourceRequest r
                    ) {
                        String url = r.getUrl().toString();

                        if (url.startsWith("file:///android_asset/")
                                || url.startsWith("https://")
                                || url.startsWith("http://")) {

                            webView.loadUrl(url);

                            if (popup.getParent() != null) {
                                ((android.view.ViewGroup) popup.getParent())
                                        .removeView(popup);
                            }

                            return true;
                        }

                        return false;
                    }
                });

                WebView.WebViewTransport transport =
                        (WebView.WebViewTransport) resultMsg.obj;

                transport.setWebView(popup);
                resultMsg.sendToTarget();

                return true;
            }
        });

        setContentView(webView);

        if (state == null) {
            webView.loadUrl("file:///android_asset/index.html");
        } else {
            webView.restoreState(state);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (webView != null) {
            webView.saveState(outState);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("هل تريد الخروج من STOREMASTER؟")
                    .setNegativeButton("البقاء",
                            (d, w) -> d.dismiss())
                    .setPositiveButton("خروج",
                            (d, w) -> finish())
                    .show();
        }
    }
}
