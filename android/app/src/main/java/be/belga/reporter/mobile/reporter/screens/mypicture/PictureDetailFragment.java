package be.belga.reporter.mobile.reporter.screens.mypicture;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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
import be.belga.reporter.mobile.reporter.model.Metadata;
import be.belga.reporter.mobile.reporter.model.Post;
import be.belga.reporter.mobile.reporter.screens.main.MainActivity;
import be.belga.reporter.mobile.reporter.screens.myshort.CustomSpinnerAdapter;
import be.belga.reporter.utils.FileUtil;
import be.belga.reporter.utils.MetadataUtil;
import be.belga.reporter.utils.RealPathUtil;
import belga.be.belgareporter.BuildConfig;
import belga.be.belgareporter.R;

public class PictureDetailFragment extends ReporterFragment implements MainActivity.OnBackPressed, OnClickListener {
    private static final String TAG = PictureDetailFragment.class.getSimpleName();

    private static final String PARAM_POST = "Post";
    private static final String PARAM_INDEX = "Index";

    private MainActivity mainActivity;
    private PictureFragment pictureFragment;
    private Menu menu;

    private BottomSheetDialog dialog;

    private Post post;
    private Metadata cloneMetadata;
    private int index;

    private ImageView imgFileUpload;
    private RelativeLayout detailLayout;
    private LinearLayout inputLayout;

    private EditText edTopic;
    private EditText edTitle;
    private EditText edCaption;

    private EditText edKeywords;
    private EditText edLabel;
    private EditText edCity;
    private EditText edCountry;
    private EditText edEditorial;
    private EditText edInfo;
    private EditText edCredit;
    private EditText edSource;
    private EditText edComment;
    private EditText edAuthors;

    private ViewGroup containerPackages;
    private ImageButton btnAddPackages;

    private ViewGroup containerAuthors;
    private ImageButton btnAddAuthors;

    private Spinner spnPackages1;
    private Spinner spnPackages2;
    private Spinner spnUrgency;
    private Spinner spnDistribution;
    private Spinner spnLanguage;
    private Spinner spnIPTC;
    private Spinner spnAuthors;
    private Spinner spnStatus;

    private TextView tvPackage;
    private TextView tvUrgency;
    private TextView tvDistribution;
    private TextView tvLanguage;
    private TextView tvKeyword;
    private TextView tvIPTC;
    private TextView tvAuthor;
    private TextView tvLabel;
    private TextView tvStatus;
    private TextView tvCity;
    private TextView tvCountry;
    private TextView tvEditorial;
    private TextView tvInfo;
    private TextView tvCredit;
    private TextView tvSource;
    private TextView tvComment;
    private TextView tvEdit;

    private List<String> packagesLst;
    private List<String> authorsLst;

    private String imageFilePath;

    public static PictureDetailFragment getInstance(Post post) {
        PictureDetailFragment instance = new PictureDetailFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_POST, new Gson().toJson(post));
        args.putInt(PARAM_INDEX, 0);
        instance.setArguments(args);

        return instance;
    }

    public static PictureDetailFragment getInstance(Post post, int index) {
        PictureDetailFragment instance = new PictureDetailFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_POST, new Gson().toJson(post));
        args.putInt(PARAM_INDEX, index);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        pictureFragment = (PictureFragment) getActivity().getSupportFragmentManager().findFragmentByTag("PictureFragment");
        packagesLst = new ArrayList<>();
        authorsLst = new ArrayList<>();
        cloneMetadata = Metadata.parseFromJSON(ReporterApplication.getInstance().getPersistentPreference().getString(ReporterApplication.KEY_ClONE_METADATA, null));
        if (getArguments() != null) {
            index = getArguments().getInt(PARAM_INDEX);
            post = Post.parseFromJSON(getArguments().getString(PARAM_POST));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View result = inflater.inflate(R.layout.fragment_picture_detail, container, false);

        imgFileUpload = result.findViewById(R.id.img_file_upload);
        edTopic = result.findViewById(R.id.picture_topic);
        edTitle = result.findViewById(R.id.picture_title);
        edCaption = result.findViewById(R.id.picture_caption);

        tvPackage = result.findViewById(R.id.tv_package);
        tvUrgency = result.findViewById(R.id.tv_urgency);
        tvDistribution = result.findViewById(R.id.tv_distribution);
        tvLanguage = result.findViewById(R.id.tv_language);
        tvKeyword = result.findViewById(R.id.tv_keyword);
        tvIPTC = result.findViewById(R.id.tv_iptc);
        tvAuthor = result.findViewById(R.id.tv_author);
        tvLabel = result.findViewById(R.id.tv_label);
        tvStatus = result.findViewById(R.id.tv_status);
        tvCity = result.findViewById(R.id.tv_city);
        tvCountry = result.findViewById(R.id.tv_country);
        tvEditorial = result.findViewById(R.id.tv_editorial);
        tvInfo = result.findViewById(R.id.tv_info);
        tvCredit = result.findViewById(R.id.tv_credit);
        tvSource = result.findViewById(R.id.tv_source);
        tvComment = result.findViewById(R.id.tv_comment);

        edKeywords = result.findViewById(R.id.metadata_keywords);
        edLabel = result.findViewById(R.id.metadata_label);
        edCity = result.findViewById(R.id.metadata_city);
        edCountry = result.findViewById(R.id.metadata_country);
        edEditorial = result.findViewById(R.id.metadata_editorial);
        edInfo = result.findViewById(R.id.metadata_info);
        edCredit = result.findViewById(R.id.metadata_credit);
        edSource = result.findViewById(R.id.metadata_source);
        edComment = result.findViewById(R.id.metadata_comment);
        edAuthors = result.findViewById(R.id.metadata_author_text);
        edAuthors.setText(ReporterApplication.getInstance().getUser().getUsername());

        tvEdit = result.findViewById(R.id.btn_edit);
        tvEdit.setOnClickListener(this);
        detailLayout = result.findViewById(R.id.layout_detail);
        inputLayout = result.findViewById(R.id.layout_input);

        spnPackages1 = result.findViewById(R.id.metadata_package_1);
        spnPackages2 = result.findViewById(R.id.metadata_package_2);
        containerPackages = result.findViewById(R.id.container_packages);
        btnAddPackages = result.findViewById(R.id.btn_add_packages);
        btnAddPackages.setOnClickListener(this);
        spnUrgency = result.findViewById(R.id.metadata_urgency);
        spnDistribution = result.findViewById(R.id.metadata_distribution);
        spnLanguage = result.findViewById(R.id.metadata_language);
        spnIPTC = result.findViewById(R.id.metadata_iptc);
        spnAuthors = result.findViewById(R.id.metadata_author_spinner);
        containerAuthors = result.findViewById(R.id.container_authors);
        btnAddAuthors = result.findViewById(R.id.btn_add_authors);
        btnAddAuthors.setOnClickListener(this);
        spnStatus = result.findViewById(R.id.metadata_status);

        if (post.getFileUpload() != null) {
            imgFileUpload.setImageURI(Uri.parse(post.getFileUpload().getGeneratedUrl()));
        }

        spnPackages1.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.metadata_packages_1)));
        spnPackages2.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.metadata_packages_2)));
        spnUrgency.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.metadata_urgency)));
        spnDistribution.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.metadata_distribution)));
        spnLanguage.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.metadata_language)));
        spnIPTC.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.metadata_iptc)));
        spnAuthors.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.metadata_authors)));
        spnStatus.setAdapter(new CustomSpinnerAdapter(getContext(), getResources().getStringArray(R.array.metadata_status)));

        loadData();

        setHasOptionsMenu(true);

        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.menu_nav_picture_detail, menu);
        this.menu = menu;

        mainActivity.setTitle(getString(R.string.picture_detail));
        mainActivity.setDrawerEnable(false);

        menu.findItem(R.id.clear_menu).setIcon(mainActivity.setFontAwesomeMenu(18, R.string.clear_icon));
        menu.findItem(R.id.change_picture_menu).setIcon(mainActivity.setFontAwesomeMenu(18, R.string.change_picture_icon));
        menu.findItem(R.id.copy_menu).setIcon(mainActivity.setFontAwesomeMenu(18, R.string.copy_icon));
        menu.findItem(R.id.copy_menu).setVisible(cloneMetadata != null ? false : true);
        menu.findItem(R.id.paste_menu).setIcon(mainActivity.setFontAwesomeMenu(18, R.string.paste_icon));
        menu.findItem(R.id.paste_menu).setVisible(menu.findItem(R.id.copy_menu).isVisible() ? false : true);
        menu.findItem(R.id.send_menu).setIcon(mainActivity.setFontAwesomeMenu(24, R.string.send_icon));
        menu.findItem(R.id.send_menu).setVisible(pictureFragment != null ? false : true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mainActivity.isFragmentOnTop(this)) {
            switch (item.getItemId()) {
                case R.id.clear_menu:
                    clearText();
                    return true;
                case R.id.change_picture_menu:
                    if (requestPermission()) {
                        showDialog();
                    }
                    return true;
                case R.id.copy_menu:
                    cloneMetadata = new Metadata();
                    MetadataUtil.setDataMetadata(cloneMetadata,
                            MetadataUtil.getValueMetadata(containerPackages),
                            spnUrgency.getSelectedItem().toString(),
                            spnDistribution.getSelectedItem().toString(),
                            spnLanguage.getSelectedItem().toString(),
                            edKeywords.getText().toString(),
                            spnIPTC.getSelectedItem().toString(),
                            MetadataUtil.getValueMetadata(containerAuthors),
                            edLabel.getText().toString(),
                            spnStatus.getSelectedItem().toString(),
                            edCity.getText().toString(),
                            edCountry.getText().toString(),
                            edEditorial.getText().toString(),
                            edInfo.getText().toString(),
                            edCredit.getText().toString(),
                            edSource.getText().toString(),
                            edComment.getText().toString()
                    );

                    ReporterApplication.getInstance().createMetadata(cloneMetadata);
                    menu.findItem(R.id.copy_menu).setVisible(false);
                    menu.findItem(R.id.paste_menu).setVisible(true);
                    return true;
                case R.id.paste_menu:
                    cloneMetadata = Metadata.parseFromJSON(ReporterApplication.getInstance().getPersistentPreference().getString(ReporterApplication.KEY_ClONE_METADATA, null));
                    tvPackage.setText(MetadataUtil.getValueMetadata(cloneMetadata.getPackage_()));
                    tvUrgency.setText(cloneMetadata.getUrgency());
                    tvDistribution.setText(cloneMetadata.getDistribition());
                    tvLanguage.setText(cloneMetadata.getLanguage());
                    tvKeyword.setText(cloneMetadata.getKeywords());
                    tvIPTC.setText(cloneMetadata.getIptc());
                    tvAuthor.setText(MetadataUtil.getValueMetadata(cloneMetadata.getAuthor()));
                    tvLabel.setText(cloneMetadata.getLabel());
                    tvStatus.setText(cloneMetadata.getStatus());
                    tvCity.setText(cloneMetadata.getCity());
                    tvCountry.setText(cloneMetadata.getCountry());
                    tvEditorial.setText(cloneMetadata.getEditorial());
                    tvInfo.setText(cloneMetadata.getInfo());
                    tvCredit.setText(cloneMetadata.getCredit());
                    tvSource.setText(cloneMetadata.getSource());
                    tvComment.setText(cloneMetadata.getComment());

                    edKeywords.setText(cloneMetadata.getKeywords());
                    edLabel.setText(cloneMetadata.getLabel());
                    edCity.setText(cloneMetadata.getCity());
                    edCountry.setText(cloneMetadata.getCountry());
                    edEditorial.setText(cloneMetadata.getEditorial());
                    edInfo.setText(cloneMetadata.getInfo());
                    edCredit.setText(cloneMetadata.getCredit());
                    edSource.setText(cloneMetadata.getSource());
                    edComment.setText(cloneMetadata.getComment());

                    MetadataUtil.setValueSpinner(spnUrgency, cloneMetadata.getUrgency());
                    MetadataUtil.setValueSpinner(spnDistribution, cloneMetadata.getDistribition());
                    MetadataUtil.setValueSpinner(spnIPTC, cloneMetadata.getIptc());
                    MetadataUtil.setValueSpinner(spnLanguage, cloneMetadata.getLanguage());
                    MetadataUtil.setValueSpinner(spnStatus, cloneMetadata.getStatus());

                    MetadataUtil.addMetadata(getActivity(), containerPackages, cloneMetadata.getPackage_().split(ReporterApplication.SEMICOLON_CHARACTER), packagesLst);
                    MetadataUtil.addMetadata(getActivity(), containerAuthors, cloneMetadata.getAuthor().split(ReporterApplication.SEMICOLON_CHARACTER), authorsLst);

                    ReporterApplication.getInstance().clearCloneMetadata();

                    menu.findItem(R.id.copy_menu).setVisible(true);
                    menu.findItem(R.id.paste_menu).setVisible(false);
                    return true;
                case android.R.id.home:
                    savePost();
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
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

    private void savePost() {
        if (post.getId() == null) {
            post.setId(UUID.randomUUID().toString());
        }

        post.setTopic(edTopic.getText().toString());
        post.setTitle(edTitle.getText().toString());
        post.setCaption(edCaption.getText().toString());

        MetadataUtil.setDataMetadata(post,
                MetadataUtil.getValueMetadata(containerPackages),
                spnUrgency.getSelectedItem().toString(),
                spnDistribution.getSelectedItem().toString(),
                spnLanguage.getSelectedItem().toString(),
                edKeywords.getText().toString(),
                spnIPTC.getSelectedItem().toString(),
                MetadataUtil.getValueMetadata(containerAuthors),
                edLabel.getText().toString(),
                spnStatus.getSelectedItem().toString(),
                edCity.getText().toString(),
                edCountry.getText().toString(),
                edEditorial.getText().toString(),
                edInfo.getText().toString(),
                edCredit.getText().toString(),
                edSource.getText().toString(),
                edComment.getText().toString()
        );

        if (pictureFragment != null) {
            pictureFragment.updateFileMetadata(post);
        } else {
            ReporterApplication.getInstance().updatePost(index, post);
            PostManager.getInstance().onPostsUpdated(this, false);
            mainActivity.hideSoftKeyboard(getActivity());
        }

        mainActivity.hideSoftKeyboard(getActivity());
    }

    private void loadData() {
        if (post.getId() != null) {
            edTitle.setText(post.getTitle());
            edTopic.setText(post.getTopic());
            edCaption.setText(post.getCaption());

            tvPackage.setText(MetadataUtil.getValueMetadata(post.getMetadata().getPackage_()));
            tvUrgency.setText(post.getMetadata().getUrgency());
            tvDistribution.setText(post.getMetadata().getDistribition());
            tvLanguage.setText(post.getMetadata().getLanguage());
            tvKeyword.setText(post.getMetadata().getKeywords());
            tvIPTC.setText(post.getMetadata().getIptc());
            tvAuthor.setText(MetadataUtil.getValueMetadata(post.getMetadata().getAuthor()));
            tvLabel.setText(post.getMetadata().getLabel());
            tvStatus.setText(post.getMetadata().getStatus());
            tvCity.setText(post.getMetadata().getCity());
            tvCountry.setText(post.getMetadata().getCountry());
            tvEditorial.setText(post.getMetadata().getEditorial());
            tvInfo.setText(post.getMetadata().getInfo());
            tvCredit.setText(post.getMetadata().getCredit());
            tvSource.setText(post.getMetadata().getSource());
            tvComment.setText(post.getMetadata().getComment());

            edKeywords.setText(post.getMetadata().getKeywords());
            edLabel.setText(post.getMetadata().getLabel());
            edCity.setText(post.getMetadata().getCity());
            edCountry.setText(post.getMetadata().getCountry());
            edEditorial.setText(post.getMetadata().getEditorial());
            edInfo.setText(post.getMetadata().getInfo());
            edCredit.setText(post.getMetadata().getCredit());
            edSource.setText(post.getMetadata().getSource());
            edComment.setText(post.getMetadata().getComment());

            MetadataUtil.setValueSpinner(spnUrgency, post.getMetadata().getUrgency());
            MetadataUtil.setValueSpinner(spnDistribution, post.getMetadata().getDistribition());
            MetadataUtil.setValueSpinner(spnIPTC, post.getMetadata().getIptc());
            MetadataUtil.setValueSpinner(spnLanguage, post.getMetadata().getLanguage());
            MetadataUtil.setValueSpinner(spnStatus, post.getMetadata().getStatus());

            if (post.getMetadata().getPackage_() != null) {
                MetadataUtil.addMetadata(getActivity(), containerPackages, post.getMetadata().getPackage_().split(ReporterApplication.SEMICOLON_CHARACTER), packagesLst);
            }

            if (post.getMetadata().getAuthor() != null) {
                MetadataUtil.addMetadata(getActivity(), containerAuthors, post.getMetadata().getAuthor().split(ReporterApplication.SEMICOLON_CHARACTER), authorsLst);
            }
        } else {
            tvPackage.setText(MetadataUtil.getValueMetadata(ReporterApplication.getInstance().getUserMetadata().getPackage_()));
            tvUrgency.setText(ReporterApplication.getInstance().getUserMetadata().getUrgency());
            tvDistribution.setText(ReporterApplication.getInstance().getUserMetadata().getDistribition());
            tvLanguage.setText(ReporterApplication.getInstance().getUserMetadata().getLanguage());
            tvKeyword.setText(ReporterApplication.getInstance().getUserMetadata().getKeywords());
            tvIPTC.setText(ReporterApplication.getInstance().getUserMetadata().getIptc());
            tvAuthor.setText(MetadataUtil.getValueMetadata(ReporterApplication.getInstance().getUserMetadata().getAuthor()));
            tvLabel.setText(ReporterApplication.getInstance().getUserMetadata().getLabel());
            tvStatus.setText(ReporterApplication.getInstance().getUserMetadata().getStatus());
            tvCity.setText(ReporterApplication.getInstance().getUserMetadata().getCity());
            tvCountry.setText(ReporterApplication.getInstance().getUserMetadata().getCountry());
            tvEditorial.setText(ReporterApplication.getInstance().getUserMetadata().getEditorial());
            tvInfo.setText(ReporterApplication.getInstance().getUserMetadata().getInfo());
            tvCredit.setText(ReporterApplication.getInstance().getUserMetadata().getCredit());
            tvSource.setText(ReporterApplication.getInstance().getUserMetadata().getSource());
            tvComment.setText(ReporterApplication.getInstance().getUserMetadata().getComment());

            edKeywords.setText(ReporterApplication.getInstance().getUserMetadata().getKeywords());
            edLabel.setText(ReporterApplication.getInstance().getUserMetadata().getLabel());
            edCity.setText(ReporterApplication.getInstance().getUserMetadata().getCity());
            edCountry.setText(ReporterApplication.getInstance().getUserMetadata().getCountry());
            edEditorial.setText(ReporterApplication.getInstance().getUserMetadata().getEditorial());
            edInfo.setText(ReporterApplication.getInstance().getUserMetadata().getInfo());
            edCredit.setText(ReporterApplication.getInstance().getUserMetadata().getCredit());
            edSource.setText(ReporterApplication.getInstance().getUserMetadata().getSource());
            edComment.setText(ReporterApplication.getInstance().getUserMetadata().getComment());

            MetadataUtil.setValueSpinner(spnUrgency, ReporterApplication.getInstance().getUserMetadata().getUrgency());
            MetadataUtil.setValueSpinner(spnDistribution, ReporterApplication.getInstance().getUserMetadata().getDistribition());
            MetadataUtil.setValueSpinner(spnIPTC, ReporterApplication.getInstance().getUserMetadata().getIptc());
            MetadataUtil.setValueSpinner(spnLanguage, ReporterApplication.getInstance().getUserMetadata().getLanguage());
            MetadataUtil.setValueSpinner(spnStatus, ReporterApplication.getInstance().getUserMetadata().getStatus());

            MetadataUtil.addMetadata(getActivity(), containerPackages, ReporterApplication.getInstance().getUserMetadata().getPackage_().split(ReporterApplication.SEMICOLON_CHARACTER), packagesLst);
            MetadataUtil.addMetadata(getActivity(), containerAuthors, ReporterApplication.getInstance().getUserMetadata().getAuthor().split(ReporterApplication.SEMICOLON_CHARACTER), authorsLst);
        }
    }

    private void clearText() {
        if (inputLayout.getVisibility() != View.GONE) {
            for (int i = 0; i < inputLayout.getChildCount(); i++) {
                if (inputLayout.getChildAt(i) instanceof EditText)
                    ((EditText) inputLayout.getChildAt(i)).getText().clear();
            }
        }

        edTitle.getText().clear();
        edTopic.getText().clear();
        edCaption.getText().clear();
    }

    @Override
    public void onBackPressed() {
        savePost();
        mainActivity.getFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit:
                detailLayout.setVisibility(View.GONE);
                inputLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_add_packages:
                MetadataUtil.addMetadata(getActivity(), containerPackages, spnPackages1.getSelectedItem().toString(), spnPackages2.getSelectedItem().toString(), packagesLst);
                break;
            case R.id.btn_add_authors:
                MetadataUtil.addMetadata(getActivity(), containerAuthors, edAuthors.getText().toString(), spnAuthors.getSelectedItem().toString(), authorsLst);
                break;
        }
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
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
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
                Uri selectedImage = Uri.parse("file:" + imageFilePath);
                realPath = selectedImage.getPath();
                break;
        }
        String mimeType = FileUtil.getMimeType(realPath);
        File imgOrg = new File(realPath);

        post.getFileUpload().setGeneratedName(imgOrg.getName());
        post.getFileUpload().setGeneratedUrl(realPath);
        post.getFileUpload().setMimetype(mimeType);
        post.getFileUpload().setSize(mainActivity.getSizePicture(realPath));

        imgFileUpload.setImageURI(Uri.parse(post.getFileUpload().getGeneratedUrl()));

        if (pictureFragment != null) {
            pictureFragment.updateFile(post.getFileUpload());
        }
    }

}
