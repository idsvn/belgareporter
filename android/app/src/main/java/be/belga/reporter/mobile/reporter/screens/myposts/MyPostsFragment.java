package be.belga.reporter.mobile.reporter.screens.myposts;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import be.belga.reporter.mobile.reporter.application.ReporterFragment;
import be.belga.reporter.mobile.reporter.manager.PostManager;
import be.belga.reporter.mobile.reporter.screens.main.MainActivity;
import belga.be.belgareporter.R;

/**
 * Created by vinh.bui on 6/5/2018.
 */

public class MyPostsFragment extends ReporterFragment {
    private MainActivity mainActivity;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MyPostsFragmentPagerAdapter adapter;

    public static MyPostsFragment getInstance() {
        MyPostsFragment fragment = new MyPostsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        PostManager.getInstance().onPostsUpdated(this, false);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.fragment_my_posts, container, false);

        viewPager = (ViewPager) result.findViewById(R.id.viewpager_posts);
        tabLayout = (TabLayout) result.findViewById(R.id.tabs_posts);

        viewPager.setAdapter(adapter = new MyPostsFragmentPagerAdapter(getChildFragmentManager()));
        viewPager.setOffscreenPageLimit(5);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (adapter != null) {
                    ReporterFragment fragment = adapter.getFragment(position);
                    if (fragment != null) {
                        for (int i = 0; i < adapter.getCount(); i++) {
                            adapter.getFragment(i).setUserVisibleHint(false);
                        }
                        fragment.setUserVisibleHint(true);
                        PostManager manager = PostManager.getInstance();
                        manager.onPostsUpdated(MyPostsFragment.this, false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);

        setHasOptionsMenu(true);

        return result;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mainActivity.setTitle(R.string.posts);
        mainActivity.setDrawerEnable(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    protected void _onVisible() {
        PostManager manager = PostManager.getInstance();
        manager.onPostsUpdated(MyPostsFragment.this, false);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected String getAnalyticName() {
        return null;
    }

    @Override
    protected HashMap<String, String> getAnalyticParameters() {
        return null;
    }

}
