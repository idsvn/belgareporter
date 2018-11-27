package be.belga.reporter.mobile.reporter.screens.myposts;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import be.belga.reporter.mobile.reporter.application.ReporterApplication;
import be.belga.reporter.mobile.reporter.application.ReporterFragment;
import be.belga.reporter.mobile.reporter.manager.PostManager;
import be.belga.reporter.mobile.reporter.model.Post;
import be.belga.reporter.mobile.reporter.network.APIUrls;
import be.belga.reporter.mobile.reporter.screens.main.MainActivity;
import be.belga.reporter.mobile.reporter.service.UploadPort;
import belga.be.belgareporter.R;

/**
 * Created by vinh.bui on 6/5/2018.
 */

public class NewPostsFragment extends ReporterFragment {
    private static final String TAG = NewPostsFragment.class.getSimpleName();

    private static NewPostsFragment instance;
    private static Object mutex = new Object();

    private static final String PARAM_POSTS = "Slots";
    private static final String PARAM_TITLE = "Title";
    private static final String PARAM_MESSAGE = "Message";

    private MainActivity mainActivity;

    private SwipeMenuListView swipeMenuLstPosts;
    private PostsListViewAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayoutPosts;
    private TextView txtEmpty;

    private boolean isLongClick = false;

    private List<Post> posts;
    private String title;
    private int emptyMessage;

    //Upload File
    private UploadPort uploadPort;

    public NewPostsFragment() {
        // Required empty public constructor
    }

    public static NewPostsFragment getInstance() {
        return instance;
    }

    public static NewPostsFragment getInstance(List<Post> posts, String title, int emptyMessage) {
        instance = new NewPostsFragment();
        final Bundle args = new Bundle();
        args.putString(PARAM_POSTS, new Gson().toJson(posts));
        args.putString(PARAM_TITLE, title);
        args.putInt(PARAM_MESSAGE, emptyMessage);
        instance.setArguments(args);
        return instance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mainActivity = (MainActivity) getActivity();

        if (getArguments() != null) {
            title = getArguments().getString(PARAM_TITLE);
            emptyMessage = getArguments().getInt(PARAM_MESSAGE);
            posts = Post.parseAllFromJSON(getArguments().getString(PARAM_POSTS));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View result = inflater.inflate(R.layout.fragment_my_posts_list, container, false);

        swipeRefreshLayoutPosts = (SwipeRefreshLayout) result.findViewById(R.id.swipe_posts);
        swipeMenuLstPosts = (SwipeMenuListView) result.findViewById(R.id.list_swipe_posts);
        txtEmpty = (TextView) result.findViewById(R.id.txtview_empty);

        swipeMenuLstPosts.setAdapter(adapter = new PostsListViewAdapter(getActivity(), posts));
        swipeMenuLstPosts.setEmptyView(result.findViewById(R.id.container_empty));

        final SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                Point sizeDefaut = mainActivity.getSizeWindowDisplay();
                switch (menu.getViewType()) {
                    case 0:
                        setSwipeMenuItem(menu, sizeDefaut.x * 2 / 9, R.color.rose_white, R.mipmap.listview_menu_delete_icon, R.string.delete_menu, R.color.flamingo);
                        setSwipeMenuItem(menu, sizeDefaut.x * 2 / 9, R.color.mint_cream, R.mipmap.listview_menu_send_icon, R.string.send_menu, R.color.viking);
                        break;
                    case 1:
                        setSwipeMenuItem(menu, sizeDefaut.x * 2 / 9, R.color.rose_white, R.mipmap.listview_menu_delete_icon, R.string.delete_menu, R.color.flamingo);
                        break;
                    case 2:
                        setSwipeMenuItem(menu, sizeDefaut.x * 2 / 9, R.color.mint_cream, R.mipmap.listview_menu_send_icon, R.string.send_menu, R.color.viking);
                        setSwipeMenuItem(menu, sizeDefaut.x * 2 / 9, R.color.rose_white, R.mipmap.listview_menu_delete_icon, R.string.delete_menu, R.color.flamingo);
                        break;
                }
            }
        };

        swipeMenuLstPosts.setMenuCreator(creator);

        swipeMenuLstPosts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                isLongClick = true;
                swipeMenuLstPosts.smoothOpenMenu(position);
                return false;
            }
        });

        swipeMenuLstPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isLongClick == false) {
                    switch (posts.get(position).getType()) {
                        case ALERT:
                            mainActivity.openShortDetailFragment(R.string.alert_text, posts.get(position), getIndexByProperty(posts, posts.get(position)));
                            break;
                        case SHORT:
                            mainActivity.openShortDetailFragment(R.string.short_text, posts.get(position), getIndexByProperty(posts, posts.get(position)));
                            break;
                        case AUDIO:
                            mainActivity.openMediaDetailFragment(posts.get(position), getIndexByProperty(posts, posts.get(position)), R.string.audio_detail);
                            break;
                        case PICTURE:
                            mainActivity.openPictureDetailFragment(posts.get(position), getIndexByProperty(posts, posts.get(position)));
                            break;
                        case VIDEO:
                            mainActivity.openMediaDetailFragment(posts.get(position), getIndexByProperty(posts, posts.get(position)), R.string.video_detail);
                            break;
                    }
                }
                isLongClick = false;
            }
        });

        swipeMenuLstPosts.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (menu.getViewType()) {
                    case 0:
                        switch (index) {
                            case 0:
                                PostManager.getInstance().removePost(posts.get(position));
                                PostManager.getInstance().onPostsUpdated(NewPostsFragment.this, true);
                                Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                posts.get(position).setId(null);
                                posts.get(position).setWorkflowStatus(Post.PostWorkflowStatus.IN_PROGRESS);

                                Post tPost = posts.get(position);

                                PostManager.getInstance().onPostsUpdated(NewPostsFragment.this, true);

                                uploadPort = new UploadPort(getActivity(), NewPostsFragment.this, tPost);
                                uploadPort.execute(APIUrls.getPostUrl());
                                break;
                        }

                        break;
                    case 1:
                        switch (index) {
                            case 0:
                                PostManager.getInstance().removePost(posts.get(position));
                                PostManager.getInstance().onPostsUpdated(NewPostsFragment.this, true);
                                Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_LONG).show();
                                break;
                        }

                        break;
                    case 2:
                        switch (index) {
                            case 0:
                                Toast.makeText(getContext(), getString(R.string.cancel_menu), Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                PostManager.getInstance().removePost(posts.get(position));
                                PostManager.getInstance().onPostsUpdated(NewPostsFragment.this, true);
                                Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_LONG).show();
                                break;
                        }

                        break;
                }

                // false : close the menu; true : not close the menu
                return false;
            }
        });

        swipeRefreshLayoutPosts.setColorSchemeColors(mainActivity.getResources().getColor(R.color.viking));
        swipeRefreshLayoutPosts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PostManager.getInstance().onPostsUpdated(NewPostsFragment.this, true);
                    }
                }, 3000);
            }
        });

        updateEmptyMessage();
        setHasOptionsMenu(true);
        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mainActivity.setDrawerEnable(true);
        mainActivity.setTitle(title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        updateEmptyMessage();
        adapter.setPosts(posts);
        swipeRefreshLayoutPosts.setRefreshing(false);
    }

    private void updateEmptyMessage() {
        if (posts == null || posts.size() == 0) {
            txtEmpty.setText(emptyMessage);
        }
    }

    @Override
    protected String getAnalyticName() {
        return "Reporter.PostsContainerViewController";
    }

    @Override
    protected HashMap<String, String> getAnalyticParameters() {
        HashMap<String, String> result = new HashMap<>();
        result.put("tab", title);
        return result;
    }

    private void setSwipeMenuItem(SwipeMenu swipeMenu, int menuWidth, int colorBackground, int icon, int title, int titleColor) {
        SwipeMenuItem swipeMenuItem = new SwipeMenuItem(getContext());
        swipeMenuItem.setBackground(colorBackground);
        swipeMenuItem.setWidth(menuWidth);
        swipeMenuItem.setIcon(icon);

        swipeMenuItem.setTitle(title);
        swipeMenuItem.setTitleSize(12);
        swipeMenuItem.setTitleColor(getContext().getResources().getColor(titleColor));

        swipeMenu.addMenuItem(swipeMenuItem);
    }

    private int getIndexByProperty(List<Post> posts, Post post) {
        for (int i = 0; i < posts.size(); i++) {
            if (post != null && posts.get(i).getCreateDate().equals(post.getCreateDate())) {
                return i;
            }
        }
        return -1;
    }

    public void setStatus(int index, final String status) {
        View v = swipeMenuLstPosts.getChildAt(index - swipeMenuLstPosts.getFirstVisiblePosition());

        if (v == null) {
            adapter.setPosts(ReporterApplication.getInstance().getPosts());
            swipeMenuLstPosts.setAdapter(adapter);
            v = swipeMenuLstPosts.getAdapter().getView(index, null, swipeMenuLstPosts);
        }

        final View finalV = v;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) finalV.findViewById(R.id.txtview_status)).setText(status + "%");
            }
        });

    }

}