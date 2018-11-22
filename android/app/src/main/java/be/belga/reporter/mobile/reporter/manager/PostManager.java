package be.belga.reporter.mobile.reporter.manager;

import android.support.v4.app.FragmentTransaction;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.belga.reporter.mobile.reporter.application.ReporterApplication;
import be.belga.reporter.mobile.reporter.application.ReporterFragment;
import be.belga.reporter.mobile.reporter.model.Post;

/**
 * Created by vinh.bui on 6/26/2018.
 */

public class PostManager {

    private static PostManager instance = null;
    private static Object mutex = new Object();
    private List<WeakReference<OnPostsUpdatedListener>> onPostsUpdatedListeners = new ArrayList<>();
    private Map<String, Post> postMap;
    private List<Post> allPosts;
    private List<Post> newPost;
    private List<Post> inProgressPost;
    private List<Post> publishedPost;
    private List<Post> failedPost;

    private PostManager() {
        allPosts = ReporterApplication.getInstance().getPosts();
        postMap = new HashMap<>();
        newPost = new ArrayList<>();
        inProgressPost = new ArrayList<>();
        publishedPost = new ArrayList<>();
        failedPost = new ArrayList<>();

        onPostsUpdated(null, false);
    }

    public static PostManager getInstance() {
        PostManager result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null) {
                    instance = result = new PostManager();
                }
            }
        }
        return result;
    }

    public void onPostsUpdated(ReporterFragment fragment, boolean callback) {
        postMap.clear();
        newPost.clear();
        inProgressPost.clear();
        publishedPost.clear();
        failedPost.clear();

        for (Post post : allPosts) {
            postMap.put(post.getId(), post);
            switch (post.getWorkflowStatus().getStatus()) {
                case "NEW":
                    newPost.add(post);
                    break;

                case "INPROGRESS":
                    inProgressPost.add(post);
                    break;

                case "PUBLISHED":
                    publishedPost.add(post);
                    break;

                case "FAILED":
                    failedPost.add(post);
                    break;
            }

        }

        List<WeakReference<OnPostsUpdatedListener>> listeners = onPostsUpdatedListeners;
        for (int i = 0; i < listeners.size(); i++) {
            OnPostsUpdatedListener curListener = listeners.get(i).get();
            if (curListener == null) {
                continue;
            }
            curListener.onPostsUpdated();
        }

        if (callback && fragment != null) {
            FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
            ft.detach(fragment).attach(fragment).commit();
        }
    }

    public void onPostsUpdatedListener(OnPostsUpdatedListener listener) {
        onPostsUpdatedListeners.add(new WeakReference<OnPostsUpdatedListener>(listener));
    }

    public Post getPost(String postId) {
        return postMap.get(postId);
    }

    public List<Post> getAllPosts() {
        return allPosts;
    }

    public List<Post> getNewPost() {
        return newPost;
    }

    public List<Post> getInProgressPost() {
        return inProgressPost;
    }

    public List<Post> getPublishedPost() {
        return publishedPost;
    }

    public List<Post> getFailedPost() {
        return failedPost;
    }

    public void removePost(Post post) {
        ReporterApplication.getInstance().removePost(post);
    }

    public void clear() {
        if (instance != null) {
            postMap.clear();
            allPosts.clear();
            newPost.clear();
            inProgressPost.clear();
            publishedPost.clear();
            failedPost.clear();
            onPostsUpdatedListeners.clear();
        }
    }

    public interface OnPostsUpdatedListener {
        void onPostsUpdated();
    }
}
