package be.belga.reporter.mobile.reporter.service;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.belga.reporter.mobile.reporter.application.ReporterApplication;
import be.belga.reporter.mobile.reporter.model.Post;
import be.belga.reporter.mobile.reporter.screens.myposts.AllPostsFragment;
import be.belga.reporter.mobile.reporter.screens.myposts.InProgressPostsFragment;
import io.tus.java.client.TusClient;
import io.tus.java.client.TusUpload;
import io.tus.java.client.TusUploader;

public class UploadFile extends AsyncTask<Void, Long, URL> {
    private Activity activity;
    private TusClient client;
    private TusUpload upload;
    private Post post;
    private int index;

    public UploadFile(Activity activity, TusClient client, TusUpload upload, Post post) {
        this.activity = activity;
        this.client = client;
        this.upload = upload;
        this.post = post;
        this.index = getIndexByProperty(ReporterApplication.getInstance().getPosts(), post);
    }

    @Override
    protected void onProgressUpdate(Long... updates) {
        long uploadedBytes = updates[0];
        long totalBytes = updates[1];
        NumberFormat formatter = new DecimalFormat("#0.00");
        String percentUpload = formatter.format((double) uploadedBytes / totalBytes * 100);
        AllPostsFragment.getInstance().setStatus(index, percentUpload);
        InProgressPostsFragment.getInstance().setStatus(index, percentUpload);
    }

    @Override
    protected void onPostExecute(URL url) {
        if (post.getWorkflowStatus().getStatus().equals(Post.PostWorkflowStatus.PUBLISHED)) {
            AllPostsFragment.getInstance().setStatus(index, Post.PostWorkflowStatus.PUBLISHED.getStatus(), Post.PostWorkflowStatus.PUBLISHED.getIconResource());
            InProgressPostsFragment.getInstance().setStatus(index, Post.PostWorkflowStatus.PUBLISHED.getStatus(), Post.PostWorkflowStatus.PUBLISHED.getIconResource());
        }

        if (post.getWorkflowStatus().getStatus().equals(Post.PostWorkflowStatus.FAILED)) {
            AllPostsFragment.getInstance().setStatus(index, Post.PostWorkflowStatus.FAILED.getStatus(), Post.PostWorkflowStatus.FAILED.getIconResource());
            InProgressPostsFragment.getInstance().setStatus(index, Post.PostWorkflowStatus.FAILED.getStatus(), Post.PostWorkflowStatus.FAILED.getIconResource());
        }
    }

    @Override
    protected URL doInBackground(Void... params) {
        try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("filename", post.getFileUpload().getGeneratedName());
            upload.setMetadata(metadata);

            upload.getFingerprint();

            TusUploader uploader = client.resumeOrCreateUpload(upload);
            long totalBytes = upload.getSize();
            long uploadedBytes = uploader.getOffset();

            // Upload file in 1MiB chunks
            uploader.setChunkSize(1024 * 1024);

            while (!isCancelled() && uploader.uploadChunk() > 0) {
                uploadedBytes = uploader.getOffset();
                publishProgress(uploadedBytes, totalBytes);
            }

            uploader.finish();
            return uploader.getUploadURL();

        } catch (Exception e) {
            Log.e(activity.getLocalClassName(), e.getMessage(), e);
        }

        return null;
    }

    private int getIndexByProperty(List<Post> posts, Post post) {
        for (int i = 0; i < posts.size(); i++) {
            if (post != null && posts.get(i).getCreateDate().equals(post.getCreateDate())) {
                return i;
            }
        }
        return -1;
    }

}
