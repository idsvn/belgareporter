package be.belga.reporter.mobile.reporter.screens.mymedia;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import be.belga.reporter.mobile.reporter.application.ReporterApplication;
import be.belga.reporter.mobile.reporter.application.ReporterFragment;
import be.belga.reporter.mobile.reporter.manager.PostManager;
import be.belga.reporter.mobile.reporter.model.FileUpload;
import be.belga.reporter.mobile.reporter.model.Post;
import be.belga.reporter.mobile.reporter.screens.main.MainActivity;
import be.belga.reporter.mobile.reporter.screens.mypicture.PictureGridViewAdapter;
import be.belga.reporter.utils.FileUtil;
import be.belga.reporter.utils.RealPathUtil;
import belga.be.belgareporter.R;

public class MediaFragment extends ReporterFragment implements MainActivity.OnBackPressed {
    private static final String TAG = MediaFragment.class.getSimpleName();

    private static final String PARAM_TITLE = "Title";

    private MainActivity mainActivity;

    private List<FileUpload> files;
    private List<Post> posts;

    private GridView gridViewVideo;
    private PictureGridViewAdapter pictureGridViewAdapter;

    private int title;

    public static MediaFragment getInstance(int title) {
        MediaFragment instance = new MediaFragment();
        Bundle args = new Bundle();
        args.putInt(PARAM_TITLE, title);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        files = new ArrayList<>();
        posts = new ArrayList<>();
        if (getArguments() != null) {
            title = getArguments().getInt(PARAM_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View result = inflater.inflate(R.layout.frament_media, container, false);

        gridViewVideo = (GridView) result.findViewById(R.id.gridview_video);

        setDataGridView();

        setHasOptionsMenu(true);

        return result;
    }

    private void setDataGridView() {
        gridViewVideo.setAdapter(pictureGridViewAdapter = new PictureGridViewAdapter(getActivity(), files));

        gridViewVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == files.size()) {
                    if (requestPermission()) {
                        switch (title) {
                            case R.string.video:
                                chooseVideo();
                                break;
                            case R.string.audio:
                                chooseAudio();
                                break;
                        }
                    }
                } else {
                    switch (title) {
                        case R.string.video:
                            mainActivity.openMediaDetailFragment(posts.get(position), R.string.video_detail);
                            break;
                        case R.string.audio:
                            mainActivity.openMediaDetailFragment(posts.get(position), R.string.audio_detail);
                            break;
                    }
                }
            }
        });

//        gridViewVideo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position != files.size()) {
//                    Toast.makeText(getContext(), "Audio was removed", Toast.LENGTH_LONG).show();
//                }
//                return true;
//            }
//        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.menu_nav_picture, menu);

        mainActivity.setTitle(getString(title));
        mainActivity.setDrawerEnable(false);

        menu.findItem(R.id.send_menu).setIcon(mainActivity.setFontAwesomeMenu(24, R.string.send_icon));
        menu.findItem(R.id.sort_menu).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mainActivity.isFragmentOnTop(this)) {
            switch (item.getItemId()) {
                case R.id.send_menu:
                    return true;

                case android.R.id.home:
                    addNewPost();
                    mainActivity.getSupportFragmentManager().popBackStackImmediate();
                    return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String getAnalyticName() {
        return null;
    }

    @Override
    protected HashMap<String, String> getAnalyticParameters() {
        return null;
    }

    private void chooseVideo() {
//        Intent intent = new Intent();
//        intent.setType("video/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//        startActivityForResult(Intent.createChooser(intent, "Select Video"), ReporterApplication.REQUEST_TAKE_GALLERY_VIDEO);
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(intent, ReporterApplication.REQUEST_TAKE_GALLERY_VIDEO);
    }

    private void chooseAudio() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), ReporterApplication.REQUEST_TAKE_GALLERY_VIDEO);
    }

    private boolean requestPermission() {
        int permissionReadExternalStorage = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionRecordAudio = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (permissionRecordAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), ReporterApplication.CHANGE_IMAGE_REQUEST_CODE);
            return false;
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.wtf(TAG, "onRequestPermissionsResult(");
        if (requestCode == ReporterApplication.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Log.wtf(TAG, "Permission denied");
                Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        String realPath = "";

        switch (requestCode) {
            case ReporterApplication.REQUEST_TAKE_GALLERY_VIDEO:
                realPath = RealPathUtil.getRealPath(getActivity(), data.getData());
                break;
        }

        Post post = new Post();
        FileUpload myFile = new FileUpload();

        try {
            String mimeType = FileUtil.getMimeType(realPath);

            File imgOrg = new File(realPath);

<<<<<<< HEAD
            myFile.setId(UUID.randomUUID().toString());
            myFile.setGeneratedName(imgOrg.getName());
            myFile.setGeneratedUrl(realPath);
            myFile.setMimetype(mimeType);
            myFile.setSize(mainActivity.getSizeVideo(realPath));
=======
        myFile.setStrId(UUID.randomUUID().toString());
        myFile.setGeneratedName(imgOrg.getName());
        myFile.setGeneratedUrl(realPath);
        myFile.setMimetype(mimeType);
        myFile.setSize(mainActivity.getSizeVideo(realPath));
>>>>>>> a92387f8b1bb5cbfd5a61ea3f8f45ca713308af9

            files.add(myFile);
        } catch (Exception e) {
            Toast.makeText(this.getContext(), "This format video not support", Toast.LENGTH_SHORT).show();
        }

        post.setWorkflowStatus(Post.PostWorkflowStatus.NEW);
        if (title == R.string.video) {
            post.setType(Post.PostType.VIDEO.getStatus());
        } else {
            post.setType(Post.PostType.AUDIO.getStatus());
        }

        post.setCreateDate(new Date().getTime());
        post.setFileUpload(myFile);

        if (post.getMetadata().getId() == null) {
            post.setMetadata(ReporterApplication.getInstance().getUserMetadata());
        }

        posts.add(post);

        pictureGridViewAdapter.setPicture(files);
    }

    private void addNewPost() {
        ReporterApplication.getInstance().addPost(posts);
        PostManager.getInstance().onPostsUpdated(this, false);
        mainActivity.hideSoftKeyboard(getActivity());
    }

    @Override
    public void onBackPressed() {
        addNewPost();
        mainActivity.getSupportFragmentManager().popBackStackImmediate();
    }

    public void updateFileMetadata(Post post) {
        for (int i = 0; i < posts.size(); i++) {
            if (post.getFileUpload().getStrId().equals(posts.get(i).getFileUpload().getStrId())) {
                posts.set(i, post);
            }
        }
    }

    public void updateFile(FileUpload fileUpload) {
        for (int i = 0; i < files.size(); i++) {
            if (fileUpload.getStrId().equals(files.get(i).getStrId())) {
                files.set(i, fileUpload);
            }
        }
    }
}
