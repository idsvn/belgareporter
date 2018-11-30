package be.belga.reporter.mobile.reporter.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import be.belga.reporter.mobile.reporter.analytics.AnalyticsManager;
import be.belga.reporter.mobile.reporter.manager.PostManager;
import be.belga.reporter.mobile.reporter.model.Metadata;
import be.belga.reporter.mobile.reporter.model.Post;
import be.belga.reporter.mobile.reporter.model.User;
import be.belga.reporter.mobile.reporter.persistantStorage.PersistantStorageManager;

/**
 * Created by vinh.bui on 6/1/2018.
 */

public class ReporterApplication extends Application {
    public static final String KEY_POSTS = "belga.be.belgareporter.mobile.reporter.posts.KEY_POSTS";
    public static final String KEY_ClONE_METADATA = "belga.be.belgareporter.mobile.reporter.posts.KEY_ClONE_METADATA";
    public static final String SEMICOLON_CHARACTER = ";";
    public static final String SLASH_CHARACTER = "/";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static final int CHANGE_IMAGE_REQUEST_CODE = 10001;
    public static final int REQUEST_TAKE_PHOTO = 10002;
    public static final int REQUEST_TAKE_GALLERY_VIDEO = 10003;
    private static final String PERSISTENT_PREFERENCES = "belga.be.belgareporter.application.PERSISTENT_PREFERENCES";
    private static final String KEY_USER = "be.belga.belgareporter.application.KEY_USER";
    private static final String KEY_USER_METADATA = "be.belga.belgareporter.application.KEY_USER_METADATA";
    private static final String KEY_APP_TOKEN = "be.belga.belgareporter.application.KEY_APP_TOKEN";
    private static final String KEY_STATUS_CONNECTION = "be.belga.belgareporter.application.KEY_STATUS_CONNECTION";
    private static final String KEY_CHECK_SETTING = "be.belga.belgareporter.application.KEY_CHECK_SETTING";
    private static ReporterApplication instance = null;
    private static Object mutex = new Object();
    private User user;
    private Metadata userMetadata;
    private String token = null;
    private boolean statusConnection;
    private boolean checkSetting;
    private List<WeakReference<OnUserChangedListener>> onUserChangedListeners = new ArrayList<>();
    private SharedPreferences persistentPreferences;
    private List<Post> posts = new ArrayList<>();

    public static ReporterApplication getInstance() {
        ReporterApplication result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new ReporterApplication();
            }
        }
        return result;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        PersistantStorageManager.configureDB(this);

        persistentPreferences = getSharedPreferences(PERSISTENT_PREFERENCES, MODE_PRIVATE);
        this.user = new Gson().fromJson(persistentPreferences.getString(KEY_USER, null), User.class);
        this.userMetadata = new Gson().fromJson(persistentPreferences.getString(KEY_USER_METADATA, null), Metadata.class);
        this.posts = Post.parseAllFromJSON(this.getPersistentPreference().getString(KEY_POSTS, null));
        if (posts == null) {
            posts = new ArrayList<>();
        }
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void addPosts(List<Post> posts) {
        this.posts.addAll(posts);
        getPersistentPreference().edit().putString(KEY_POSTS, new Gson().toJson(this.posts)).commit();
    }

    public void updatePost(int index, Post post) {
        this.posts.set(index, post);
        getPersistentPreference().edit().putString(KEY_POSTS, new Gson().toJson(this.posts)).commit();
    }

    public void addPost(Post post) {
        this.posts.add(post);
        getPersistentPreference().edit().putString(KEY_POSTS, new Gson().toJson(this.posts)).commit();
    }

    public void addPost(List<Post> posts) {
        this.posts.addAll(posts);
        getPersistentPreference().edit().putString(KEY_POSTS, new Gson().toJson(this.posts)).commit();
    }

    public void removePost(Post post) {
        this.posts.remove(post);
        getPersistentPreference().edit().putString(KEY_POSTS, new Gson().toJson(this.posts)).commit();
    }

    public void createMetadata(Metadata metadata) {
        getPersistentPreference().edit().putString(KEY_ClONE_METADATA, new Gson().toJson(metadata)).commit();
    }

    public void clearCloneMetadata() {
        getPersistentPreference().edit().putString(KEY_ClONE_METADATA, null).commit();
    }

    public boolean isStatusConnection() {
        return statusConnection;
    }

    public void setStatusConnection(boolean statusConnection) {
        this.statusConnection = statusConnection;
        this.persistentPreferences.edit().putBoolean(KEY_STATUS_CONNECTION, statusConnection).commit();
    }

    public boolean isCheckSetting() {
        return checkSetting;
    }

    public void setCheckSetting(boolean checkSetting) {
        this.checkSetting = checkSetting;
        this.persistentPreferences.edit().putBoolean(KEY_CHECK_SETTING, checkSetting).commit();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(String userSource) {
        if (userSource == null) {
            this.user = null;
        } else {
            this.user = new Gson().fromJson(userSource, User.class);

            // start Analytic
            HashMap<String, String> analyticData = new HashMap<>();
            analyticData.put("display_name", this.user.getFirstName() + " " + this.user.getLastName());
            analyticData.put("username", this.user.getEmail());

            AnalyticsManager.getInstance().initialize(analyticData);
        }

        this.persistentPreferences.edit().putString(KEY_USER, userSource).commit();

        List<WeakReference<OnUserChangedListener>> listeners = this.onUserChangedListeners;

        if (this.user != null && listeners != null && listeners.size() > 0) {
            for (int i = 0; i < listeners.size(); i++) {
                OnUserChangedListener curListener = listeners.get(i).get();
                if (curListener == null) {
                    continue;
                }
                curListener.onUserChanged(getInstance().user);
            }
        } else {
            this.onUserChangedListeners = new ArrayList<>();
        }
    }

    //Created by Tai 21/11/2018
    public void setUpdateUser(String userSource, String firstname, String lastName, String email, String gender) {
        user.setFirstName(firstname);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setGender(gender);

        if (userSource == null) {
            this.user = null;
        } else {
            this.user = new Gson().fromJson(userSource, User.class);

            // start Analytic
            HashMap<String, String> analyticData = new HashMap<>();
            analyticData.put("first_name", firstname);
            analyticData.put("last_name", lastName);
            analyticData.put("email", email);
            analyticData.put("gender", gender);

            AnalyticsManager.getInstance().initialize(analyticData);
        }

        this.persistentPreferences.edit().putString(KEY_USER, userSource).commit();

        List<WeakReference<OnUserChangedListener>> listeners = this.onUserChangedListeners;

        if (this.user != null && listeners != null && listeners.size() > 0) {
            for (int i = 0; i < listeners.size(); i++) {
                OnUserChangedListener curListener = listeners.get(i).get();
                if (curListener == null) {
                    continue;
                }
                curListener.onUserChanged(getInstance().user);
            }
        } else {
            this.onUserChangedListeners = new ArrayList<>();
        }
    }

    public Metadata getUserMetadata() {
        return this.userMetadata;
    }

    public void setUserMetadata(String metadataSource) {
        if (metadataSource == null) {
            this.userMetadata = null;
        } else {
            this.userMetadata = new Gson().fromJson(metadataSource, Metadata.class);
        }
        this.persistentPreferences.edit().putString(KEY_USER_METADATA, metadataSource).commit();
    }

    public boolean isLoggedIn() {
        return this.persistentPreferences.getString(KEY_USER, null) != null;
    }

    public SharedPreferences getPersistentPreference() {
        return this.persistentPreferences;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
        this.persistentPreferences.edit().putString(KEY_APP_TOKEN, token).commit();
    }

    public void signout() {
        this.persistentPreferences.edit().clear().commit();
        setUser(null);
        setToken(null);
        PostManager.getInstance().clear();
    }

    public interface OnUserChangedListener {
        void onUserChanged(User user);
    }
}
