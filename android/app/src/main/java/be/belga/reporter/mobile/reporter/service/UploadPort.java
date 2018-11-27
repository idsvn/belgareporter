package be.belga.reporter.mobile.reporter.service;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import be.belga.reporter.mobile.reporter.application.ReporterApplication;
import be.belga.reporter.mobile.reporter.application.ReporterFragment;
import be.belga.reporter.mobile.reporter.manager.PostManager;
import be.belga.reporter.mobile.reporter.model.Post;
import be.belga.reporter.mobile.reporter.network.APIUrls;
import be.belga.reporter.mobile.reporter.network.HttpClient;
import be.belga.reporter.mobile.reporter.network.ReporterJsonHttpResponseHandler;
import be.belga.reporter.utils.FileUtil;
import belga.be.belgareporter.R;
import cz.msebera.android.httpclient.Header;
import io.tus.android.client.TusAndroidUpload;
import io.tus.java.client.TusClient;
import io.tus.java.client.TusUpload;

public class UploadPort extends AsyncTask<String, Long, Post> {
    private Activity activity;
    private ReporterFragment fragment;
    private Post post;
    private int index;

    private UploadFile uploadFile;
    private TusClient client;

    private String fileId;

    public UploadPort(Activity activity, ReporterFragment fragment, Post post) {
        this.activity = activity;
        this.fragment = fragment;
        this.post = post;
        this.client = FileUtil.getTusClient(activity);
        this.index = getIndexByProperty(ReporterApplication.getInstance().getPosts(), post);
    }

    @Override
    protected Post doInBackground(String... strings) {
        new Thread() {
            @Override
            public void run() {
                synchronized (this) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendPost(activity, fragment, post);
                        }
                    });
                }
            }
        }.start();

        return null;
    }

    public void sendPost(final Activity activity, final ReporterFragment fragment, final Post post) {
        try {
            post.setUser(ReporterApplication.getInstance().getUser());
            if (post.getFileUpload() != null) {
                resumeUpload();
                post.getFileUpload().setId(fileId);
            }
            // build jsonObject
            String jsonStr = new Gson().toJson(post);
            final JSONObject jsonObject = new JSONObject(jsonStr);
            HttpClient.post(activity, APIUrls.getPostUrl(), jsonObject, false,
                    new ReporterJsonHttpResponseHandler(activity, activity.getString(R.string.some_error_occurred)) {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                JSONObject data = response.getJSONObject("data");
                                Post post = new Gson().fromJson(data.toString(), Post.class);
                                post.setWorkflowStatus(Post.PostWorkflowStatus.PUBLISHED);
                                // store post into list page
                                ReporterApplication.getInstance().updatePost(index, post);
                                PostManager.getInstance().onPostsUpdated(fragment, true);
                            } catch (JSONException e) {
                                Log.e(activity.getLocalClassName(), e.getMessage(), e);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                            post.setWorkflowStatus(Post.PostWorkflowStatus.FAILED);
                            // store post into list page
                            ReporterApplication.getInstance().updatePost(index, post);
                            PostManager.getInstance().onPostsUpdated(fragment, true);
                        }
                    }, true);
        } catch (Exception e) {
            Log.e(activity.getLocalClassName(), e.getMessage(), e);
        }
    }

    private int getIndexByProperty(List<Post> posts, Post post) {
        for (int i = 0; i < posts.size(); i++) {
            if (post != null && posts.get(i).getCreateDate().equals(post.getCreateDate())) {
                return i;
            }
        }
        return -1;
    }

    private void resumeUpload() {
        try {
            String[] pathArr = null;
            TusUpload upload = new TusAndroidUpload(FileUtil.getImageContentUri(activity, new File(post.getFileUpload().getGeneratedUrl()), post.getType().getStatus()), activity);
            uploadFile = new UploadFile(activity, client, upload, post);
            uploadFile.execute(new Void[0]);

            while (pathArr == null) {
                pathArr = FileUtil.getTusURLStore(activity).get(upload.getFingerprint()).getPath().split(ReporterApplication.SLASH_CHARACTER);
            }

            fileId = pathArr[pathArr.length - 1];
        } catch (Exception e) {
            Log.e(activity.getLocalClassName(), e.getMessage(), e);
        }
    }

}
