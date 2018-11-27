package be.belga.reporter.mobile.reporter.screens.mypicture;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
import be.belga.reporter.mobile.reporter.network.APIUrls;
import be.belga.reporter.mobile.reporter.screens.main.MainActivity;
import be.belga.reporter.mobile.reporter.screens.myposts.AllPostsFragment;
import be.belga.reporter.mobile.reporter.service.UploadFile;
import be.belga.reporter.mobile.reporter.service.UploadPort;
import be.belga.reporter.utils.FileUtil;
import be.belga.reporter.utils.RealPathUtil;
import belga.be.belgareporter.BuildConfig;
import belga.be.belgareporter.R;

public class PictureFragment extends ReporterFragment implements MainActivity.OnBackPressed {
    private static final String TAG = PictureFragment.class.getSimpleName();

    private static PictureFragment instance;

    private MainActivity mainActivity;

    private UploadPort uploadPort;
    private UploadFile uploadFile;

    private Menu menu;
    private BottomSheetDialog dialog;

    private List<FileUpload> files;
    private List<Post> posts;

    private RecyclerView recyclerViewPicture;
    private PictureRecyclerViewAdapter pictureRecyclerViewAdapter;
    private GridView gridViewPicture;
    private PictureGridViewAdapter pictureGridViewAdapter;

    private String imageFilePath;

    private boolean isGridView = false;

    public static PictureFragment getInstance() {
        if (instance == null) {
            instance = new PictureFragment();
        }

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        files = new ArrayList<>();
        posts = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View result = inflater.inflate(R.layout.fragment_picture, container, false);

        gridViewPicture = (GridView) result.findViewById(R.id.gridview_picture);
        recyclerViewPicture = (RecyclerView) result.findViewById(R.id.recycler_view_picture);

        setDataGridView();

        setHasOptionsMenu(true);

        return result;
    }

    private void setDataGridView() {
        gridViewPicture.setAdapter(pictureGridViewAdapter = new PictureGridViewAdapter(getActivity(), files));

        gridViewPicture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == files.size()) {
                    if (requestPermission()) {
                        showDialog();
                    }
                } else {
                    mainActivity.openPictureDetailFragment(posts.get(position));
                }
            }
        });

//        gridViewPicture.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position != files.size()) {
//                    pictureGridViewAdapter.deleteItem(true, position);
//                }
//
//                return true;
//            }
//        });

    }

    private void setDataCycleView() {
        recyclerViewPicture.setHasFixedSize(true);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 100);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (setSizePicture(files).size() != 0) {
                    return setSizePicture(files).get(position / 2).get(position % 2);
                }

                return 0;
            }
        });

        recyclerViewPicture.setLayoutManager(gridLayoutManager);
        recyclerViewPicture.setAdapter(pictureRecyclerViewAdapter = new PictureRecyclerViewAdapter(mainActivity, posts));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.menu_nav_picture, menu);
        this.menu = menu;

        mainActivity.setTitle(getString(R.string.picture));
        mainActivity.setDrawerEnable(false);

        menu.findItem(R.id.send_menu).setIcon(mainActivity.setFontAwesomeMenu(24, R.string.send_icon));
        menu.findItem(R.id.sort_menu).setIcon(mainActivity.setFontAwesomeMenu(18, R.string.short_asymmetric_icon));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mainActivity.isFragmentOnTop(this)) {
            switch (item.getItemId()) {
                case R.id.sort_menu:
                    changeDisplayPicture();
                    return true;
                case R.id.send_menu:
                    prepareSendData();
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
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

    void changeDisplayPicture() {
        MenuItem menuSort = menu.findItem(R.id.sort_menu);
        if (isGridView) {
            //Display GridView
            menuSort.setIcon(mainActivity.setFontAwesomeMenu(18, R.string.short_asymmetric_icon));
            menuSort.setTitle(getString(R.string.asymmetric_display));

            recyclerViewPicture.clearFocus();
            setDataGridView();

            gridViewPicture.setVisibility(View.VISIBLE);
            recyclerViewPicture.setVisibility(View.GONE);

            isGridView = false;
        } else {
            //Display RecyclerView
            menuSort.setIcon(mainActivity.setFontAwesomeMenu(18, R.string.short_symmetry_icon));
            menuSort.setTitle(getString(R.string.symmetry_display));

            gridViewPicture.clearFocus();
            setDataCycleView();

            recyclerViewPicture.setVisibility(View.VISIBLE);
            gridViewPicture.setVisibility(View.GONE);

            isGridView = true;
        }
    }

    private List<List<Integer>> setSizePicture(List<FileUpload> files) {
        List<List<Integer>> listViewSizePercentage = new ArrayList<>();
        if (files.size() % 2 == 0) {
            for (int i = 0; i < files.size(); i += 2) {
                listViewSizePercentage.add(getPercentageSizePicture(files.get(i).getGeneratedUrl(), files.get(i + 1).getGeneratedUrl()));
            }

            List<Integer> listSize = new ArrayList<>();
            listSize.add(50);
            listSize.add(0);
            listViewSizePercentage.add(listSize);
        } else {
            for (int i = 0; i < files.size(); i++) {
                if (i % 2 == 1 && i != files.size() - 1) {
                    continue;
                }
                if (i == files.size() - 1) {
                    List<Integer> listSize = new ArrayList<>();
                    listSize.add(50);
                    listSize.add(50);

                    listViewSizePercentage.add(listSize);
                } else {
                    listViewSizePercentage.add(getPercentageSizePicture(files.get(i).getGeneratedUrl(), files.get(i + 1).getGeneratedUrl()));
                }
            }
        }

        return listViewSizePercentage;
    }

    private List<Integer> getPercentageSizePicture(String imageUrl, String nextImageUrl) {
        List<Integer> listSize = new ArrayList<>();

        File imgFile = new File(imageUrl);
        File nextImgFile = new File(nextImageUrl);

        BitmapFactory.Options dimensions = new BitmapFactory.Options();
        BitmapFactory.Options nextDimensions = new BitmapFactory.Options();
        dimensions.inJustDecodeBounds = true;
        nextDimensions.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), dimensions);
        Bitmap nextBitmap = BitmapFactory.decodeFile(nextImgFile.getAbsolutePath(), nextDimensions);

        if (dimensions.outWidth > nextDimensions.outWidth) {
            listSize.add(65);
            listSize.add(35);
        } else if (dimensions.outWidth < nextDimensions.outWidth) {
            listSize.add(35);
            listSize.add(65);
        } else {
            listSize.add(50);
            listSize.add(50);
        }

        return listSize;
    }

    private File createImageFile() {
        // Create an image file name
        File image = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = mainActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            imageFilePath = image.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return image;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), ReporterApplication.CHANGE_IMAGE_REQUEST_CODE);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", createImageFile());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, ReporterApplication.REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void showDialog() {
        dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(R.layout.dialog_add_picture);
        dialog.show();

        LinearLayout openGallery = (LinearLayout) dialog.findViewById(R.id.open_gallery);
        LinearLayout openCamera = (LinearLayout) dialog.findViewById(R.id.open_camera);

        openGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
    }

    public boolean requestPermission() {
        int permissionReadExternalStorage = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteExternalStorage = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCamera = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (permissionWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
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
                showDialog();
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
        dialog.hide();

        String realPath = "";

        switch (requestCode) {
            case ReporterApplication.CHANGE_IMAGE_REQUEST_CODE:
                realPath = RealPathUtil.getRealPath(getActivity(), data.getData());
                break;
            case ReporterApplication.REQUEST_TAKE_PHOTO:
                Uri selectedImage = Uri.parse(imageFilePath);
                realPath = selectedImage.getPath();
                break;
        }

        Post post = new Post();
        FileUpload myFile = new FileUpload();

        String mimeType = FileUtil.getMimeType(realPath);
        File imgOrg = new File(realPath);

        myFile.setId(UUID.randomUUID().toString());
        myFile.setGeneratedName(imgOrg.getName());
        myFile.setGeneratedUrl(imgOrg.getAbsolutePath());
        myFile.setMimetype(mimeType);
        myFile.setSize(mainActivity.getSizePicture(realPath));

        files.add(myFile);

        post.setWorkflowStatus(Post.PostWorkflowStatus.NEW);
        post.setType(Post.PostType.PICTURE.getStatus());
        post.setCreateDate(new Date().getTime());
        post.setFileUpload(myFile);

        posts.add(post);

        if (!isGridView) {
            pictureGridViewAdapter.setPicture(files);
        } else {
            pictureRecyclerViewAdapter.setPosts(posts);
        }
    }

    private void addNewPost() {
        ReporterApplication.getInstance().addPost(posts);
        PostManager.getInstance().onPostsUpdated(this, false);
        mainActivity.hideSoftKeyboard(getActivity());
    }

    private List<Post> prepareSendData() {
        AllPostsFragment fragment = (AllPostsFragment) getActivity().getSupportFragmentManager().findFragmentByTag("MyPostsFragment").getChildFragmentManager().getFragments().get(0);
        for (Post post : posts) {
            post.setMetadata(ReporterApplication.getInstance().getUserMetadata());
            post.getMetadata().setId(null);
            post.setWorkflowStatus(Post.PostWorkflowStatus.IN_PROGRESS);
            post.setId(null);

            ReporterApplication.getInstance().addPost(post);

            uploadPort = new UploadPort(getActivity(), fragment, post);
            uploadPort.execute(APIUrls.getPostUrl());
//            resumeUpload(post);
        }

        PostManager.getInstance().onPostsUpdated(this, false);

        return posts;
    }

//    private void resumeUpload(Post post) {
////        try {
////            String[] pathArr = null;
////            TusUpload upload = new TusAndroidUpload(FileUtil.getImageContentUri(getActivity(), new File(post.getFileUpload().getGeneratedUrl()), post.getType().getStatus()), getActivity());
////            uploadFile = new UploadFile(getActivity(), FileUtil.getTusClient(getActivity()), upload, post.getFileUpload(), post);
////            uploadFile.execute(new Void[0]);
////        } catch (Exception e) {
////            Log.e(getActivity().getLocalClassName(), e.getMessage(), e);
////        }
////    }

    @Override
    public void onBackPressed() {
        addNewPost();
        mainActivity.getSupportFragmentManager().popBackStackImmediate();
    }

    public void updateFileMetadata(Post post) {
        for (int i = 0; i < posts.size(); i++) {
            if (post.getFileUpload().getId().equals(posts.get(i).getFileUpload().getId())) {
                posts.set(i, post);
            }
        }
    }

    public void updateFile(FileUpload fileUpload) {
        for (int i = 0; i < files.size(); i++) {
            if (fileUpload.getId().equals(files.get(i).getId())) {
                files.set(i, fileUpload);
            }
        }
    }
}
