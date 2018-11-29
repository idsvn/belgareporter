package be.belga.reporter.mobile.reporter.screens.myposts;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import be.belga.reporter.mobile.reporter.application.ReporterFragment;
import be.belga.reporter.mobile.reporter.manager.PostManager;
import be.belga.reporter.mobile.reporter.model.Post;
import belga.be.belgareporter.R;

/**
 * Created by vinh.bui on 6/5/2018.
 */

public class MyPostsFragmentPagerAdapter extends FragmentStatePagerAdapter implements PostManager.OnPostsUpdatedListener {

    protected String tabTitles[] = new String[]{"ALL", "NEW", "IN PROGRESS", "PUBLISHED", "FAILED"};
    protected int pageCount = tabTitles.length;

    private ReporterFragment[] listFragments = new ReporterFragment[pageCount];

    public MyPostsFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        PostManager.getInstance().onPostsUpdatedListener(this);
    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Override
    public ReporterFragment getItem(int position) {
        List<Post> posts;
        int emptyMessageResId;
        ReporterFragment result = null;

        switch (position) {
            case 0:
                posts = PostManager.getInstance().getAllPosts();
                emptyMessageResId = R.string.empty_post;
                result = listFragments[position] = AllPostsFragment.getInstance(posts, emptyMessageResId);
                break;
            case 1:
                posts = PostManager.getInstance().getNewPost();
                emptyMessageResId = R.string.empty_new_post;
                result = listFragments[position] = NewPostsFragment.getInstance(posts, emptyMessageResId);
                break;
            case 2:
                posts = PostManager.getInstance().getInProgressPost();
                emptyMessageResId = R.string.empty_in_progress_post;
                result = listFragments[position] = InProgressPostsFragment.getInstance(posts, emptyMessageResId);
                break;
            case 3:
                posts = PostManager.getInstance().getPublishedPost();
                emptyMessageResId = R.string.empty_published_post;
                result = listFragments[position] = PublishedPostsFragment.getInstance(posts, emptyMessageResId);
                break;
            case 4:
                posts = PostManager.getInstance().getFailedPost();
                emptyMessageResId = R.string.empty_failed_post;
                result = listFragments[position] = FailedPostsFragment.getInstance(posts, emptyMessageResId);
                break;
        }

        return result;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public void onPostsUpdated() {
        if (listFragments[0] != null)
            ((AllPostsFragment) listFragments[0]).setPosts(PostManager.getInstance().getAllPosts());
        if (listFragments[1] != null)
            ((NewPostsFragment) listFragments[1]).setPosts(PostManager.getInstance().getNewPost());
        if (listFragments[2] != null)
            ((InProgressPostsFragment) listFragments[2]).setPosts(PostManager.getInstance().getInProgressPost());
        if (listFragments[3] != null)
            ((PublishedPostsFragment) listFragments[3]).setPosts(PostManager.getInstance().getPublishedPost());
        if (listFragments[4] != null)
            ((FailedPostsFragment) listFragments[4]).setPosts(PostManager.getInstance().getFailedPost());
    }

    public ReporterFragment getFragment(int i) {
        if (i > listFragments.length) return null;
        return (ReporterFragment) listFragments[i];
    }

}
