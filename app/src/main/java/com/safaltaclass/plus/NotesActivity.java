package com.safaltaclass.plus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;
import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.safaltaclass.plus.utility.DialogsUtil;
import com.safaltaclass.plus.utility.SafaltaPlusPreferences;
import com.safaltaclass.plus.utility.SafaltaPlusUtility;

import java.io.File;

public class NotesActivity extends AppCompatActivity implements OnDialogButtonClickListener {

    private WebView webView;
    private PDFView pdfView;
    private String url;
    private TextView tvTextData;
    private ImageView ivNotesImage;
    private ProgressBar pbNotes;
    private String date;
    private String category;
    private String contentformat;
    private String thumb;
    private String title;
    private String description;
    private String content;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private Toolbar toolbar;
    private String userKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_notes);

        Intent receivedIntent = getIntent();
        date = receivedIntent.getStringExtra("date");
        category = receivedIntent.getStringExtra("category");
        contentformat = receivedIntent.getStringExtra("contentformat");
        thumb = receivedIntent.getStringExtra("thumb");
        title = receivedIntent.getStringExtra("title");
        description = receivedIntent.getStringExtra("description");
        content = receivedIntent.getStringExtra("content");

        pbNotes = (ProgressBar) findViewById(R.id.progress_bar);
        tvTextData = (TextView) findViewById(R.id.tv_text_data);
        webView = (WebView) findViewById(R.id.wv_others);
        pdfView = (PDFView) findViewById(R.id.pdf_viewer);
        ivNotesImage = (ImageView) findViewById(R.id.iv_notes_image);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (SafaltaPlusUtility.getInstance().isConnected(this)) {
            loadData();
        }else{
            DialogsUtil.openAlertDialog(NotesActivity.this, getString(R.string.no_internet), getString(R.string.connection_message), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, NotesActivity.this);
        }


    }

    private void loadData(){
        if(content != null && !content.isEmpty()) {
            if (contentformat != null && contentformat.equals("pdf")) {
                pbNotes.setVisibility(View.VISIBLE);
                FileLoader.with(this)
                        .load(content)
                        .fromDirectory(getString(R.string.safalta_pdf_files), FileLoader.DIR_EXTERNAL_PUBLIC)
                        .asFile(new FileRequestListener<File>(){

                            @Override
                            public void onLoad(FileLoadRequest request, FileResponse<File> response) {
                                pbNotes.setVisibility(View.GONE);
                                pdfView.setVisibility(View.VISIBLE);
                                File file =  response.getBody();
                                pdfView.fromFile(file)
                                        .password(null)
                                        .defaultPage(0)
                                        .enableSwipe(true)
                                        .swipeHorizontal(false)
                                        .enableDoubletap(true)
                                        .onDraw(new OnDrawListener() {
                                            @Override
                                            public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                                            }
                                        })
                                        .onDrawAll(new OnDrawListener() {
                                            @Override
                                            public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                                            }
                                        })
                                        .onPageError(new OnPageErrorListener() {
                                            @Override
                                            public void onPageError(int page, Throwable t) {

                                            }
                                        })
                                        .onPageChange(new OnPageChangeListener() {
                                            @Override
                                            public void onPageChanged(int page, int pageCount) {

                                            }
                                        })
                                        .onTap(new OnTapListener() {
                                            @Override
                                            public boolean onTap(MotionEvent e) {
                                                return false;
                                            }
                                        })
                                        .onRender(new OnRenderListener() {
                                            @Override
                                            public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {

                                            }
                                        })
                                        .enableAnnotationRendering(true)
                                        .invalidPageColor(Color.WHITE)
                                        .load();
                            }

                            @Override
                            public void onError(FileLoadRequest request, Throwable t) {
                                pbNotes.setVisibility(View.GONE);
                            }
                        });

            } else if (contentformat != null && contentformat.equals("text") || contentformat.equals("html")) {
                tvTextData.setText(Html.fromHtml(content));
                tvTextData.setMovementMethod(LinkMovementMethod.getInstance());
                tvTextData.setVisibility(View.VISIBLE);
            } else if (contentformat != null && contentformat.equals("image")) {
                Glide.with(this).load(content)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivNotesImage);
                ivNotesImage.setVisibility(View.VISIBLE);
            } else if(contentformat != null && contentformat.equals("webview")){
                userKey = SafaltaPlusPreferences.getInstance().getKeyUser();
                webView.getSettings().setSupportZoom(true);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setAllowFileAccess(true);
                webView.setWebViewClient(new WebViewClient() {

                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }


                    public void onLoadResource(WebView view, String url) {
                        pbNotes.setVisibility(View.VISIBLE);
                    }

                    public void onPageFinished(WebView view, String url) {
                        pbNotes.setVisibility(View.GONE);
                        webView.setVisibility(View.VISIBLE);
                    }

                });
                if(content.contains("user_key")) {
                    webView.loadUrl(content+userKey);
                }else{
                    webView.loadUrl(content);
                }
            }
        }else{
            DialogsUtil.openAlertDialog(NotesActivity.this, getString(R.string.error), getString(R.string.data_not_found), getString(R.string.ok), getString(R.string.cancel), R.drawable.ic_error, false, NotesActivity.this);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportActionBar().hide();
        } else {
            getSupportActionBar().show();
        }
    }

    @Override
    public void onPositiveButtonClicked() {

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onErrorButtonClicked() {

    }
}
