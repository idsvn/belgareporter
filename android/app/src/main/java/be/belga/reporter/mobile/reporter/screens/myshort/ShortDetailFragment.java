package be.belga.reporter.mobile.reporter.screens.myshort;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

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
import be.belga.reporter.mobile.reporter.network.APIUrls;
import be.belga.reporter.mobile.reporter.screens.main.MainActivity;
import be.belga.reporter.mobile.reporter.screens.myposts.AllPostsFragment;
import be.belga.reporter.mobile.reporter.service.UploadPort;
import be.belga.reporter.utils.MetadataUtil;
import belga.be.belgareporter.R;

public class ShortDetailFragment extends ReporterFragment implements MainActivity.OnBackPressed, OnClickListener {
    private static final String TAG = ShortDetailFragment.class.getSimpleName();

    private static final String PARAM_POST = "Post";
    private static final String PARAM_INDEX = "Index";
    private static final String PARAM_TITLE = "Title";
    private static final String PARAM_ADD_NEW = "AddNew";

    private MainActivity mainActivity;
    private Menu menu;

    private boolean addNew;
    private Post post;
    private Metadata cloneMetadata;
    private int index;
    private int title;

    private RelativeLayout detailLayout;
    private LinearLayout inputLayout;

    private EditText edTopic;
    private EditText edTitle;
    private EditText edLead;
    private EditText edBody;

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

    private UploadPort uploadPort;

    public static ShortDetailFragment getInstance(int title) {
        ShortDetailFragment instance = new ShortDetailFragment();
        Bundle args = new Bundle();
        args.putBoolean(PARAM_ADD_NEW, true);
        args.putInt(PARAM_INDEX, 0);
        args.putInt(PARAM_TITLE, title);
        instance.setArguments(args);

        return instance;
    }

    public static ShortDetailFragment getInstance(int title, Post post, int index) {
        ShortDetailFragment instance = new ShortDetailFragment();

        final Bundle args = new Bundle();
        args.putString(PARAM_POST, new Gson().toJson(post));
        args.putBoolean(PARAM_ADD_NEW, false);
        args.putInt(PARAM_INDEX, index);
        args.putInt(PARAM_TITLE, title);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        packagesLst = new ArrayList<>();
        authorsLst = new ArrayList<>();
        cloneMetadata = Metadata.parseFromJSON(ReporterApplication.getInstance().getPersistentPreference().getString(ReporterApplication.KEY_ClONE_METADATA, null));
        if (getArguments() != null) {
            index = getArguments().getInt(PARAM_INDEX);
            post = Post.parseFromJSON(getArguments().getString(PARAM_POST));
            addNew = getArguments().getBoolean(PARAM_ADD_NEW);
            title = getArguments().getInt(PARAM_TITLE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View result = inflater.inflate(R.layout.fragment_short, container, false);

        //Find id
        edTopic = result.findViewById(R.id.short_topic);
        edTitle = result.findViewById(R.id.short_title);
        edLead = result.findViewById(R.id.short_lead);
        edBody = result.findViewById(R.id.short_body);

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
        inflater.inflate(R.menu.menu_nav_short, menu);
        this.menu = menu;

        mainActivity.setTitle(getString(title));
        mainActivity.setDrawerEnable(false);

        menu.findItem(R.id.clear_menu).setIcon(mainActivity.setFontAwesomeMenu(18, R.string.clear_icon));
        menu.findItem(R.id.send_menu).setIcon(mainActivity.setFontAwesomeMenu(24, R.string.send_icon));
        menu.findItem(R.id.copy_menu).setIcon(mainActivity.setFontAwesomeMenu(18, R.string.copy_icon));
        menu.findItem(R.id.copy_menu).setVisible(cloneMetadata != null ? false : true);
        menu.findItem(R.id.paste_menu).setIcon(mainActivity.setFontAwesomeMenu(18, R.string.paste_icon));
        menu.findItem(R.id.paste_menu).setVisible(menu.findItem(R.id.copy_menu).isVisible() ? false : true);
        if (post != null && post.getWorkflowStatus().equals(Post.PostWorkflowStatus.PUBLISHED)) {
            menu.findItem(R.id.send_menu).setVisible(false);
        } else {
            menu.findItem(R.id.send_menu).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mainActivity.isFragmentOnTop(this)) {
            switch (item.getItemId()) {
                case R.id.clear_menu:
                    clearText();
                    return true;
                case R.id.send_menu:
                    List<Fragment> fragmentList = getActivity().getSupportFragmentManager().findFragmentByTag("MyPostsFragment").getChildFragmentManager().getFragments();
                    AllPostsFragment fragment = null;
                    for (Fragment fr : fragmentList) {
                        if (fr instanceof AllPostsFragment) {
                            fragment = (AllPostsFragment) fr;
                        }
                    }
                    uploadPort = new UploadPort(getActivity(), fragment, prepareSendData());
                    uploadPort.execute(APIUrls.getPostUrl());
                    mainActivity.hideSoftKeyboard(getActivity());
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
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
                    if (addNew) {
                        addNewPost();
                    } else {
                        editPost();
                    }
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

    private void addNewPost() {
        post = new Post();

        post.setStrId(UUID.randomUUID().toString());
        post.setWorkflowStatus(Post.PostWorkflowStatus.NEW);
        if (getString(title).equals(Post.PostType.SHORT.getFullStatus())) {
            post.setType(Post.PostType.SHORT.getStatus());
        } else {
            post.setType(Post.PostType.ALERT.getStatus());
        }
        post.setCreateDate(new Date().getTime());

        post.setTopic(edTopic.getText().toString());
        post.setTitle(edTitle.getText().toString());
        post.setCaption(edLead.getText().toString());
        post.setBody(edBody.getText().toString());

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

        ReporterApplication.getInstance().addPost(post);

        PostManager.getInstance().onPostsUpdated(this, false);
//        PostManager.getInstance().onPostUpdated(this, false, post);
        mainActivity.hideSoftKeyboard(getActivity());
    }

    private void editPost() {
        post.setTopic(edTopic.getText().toString());
        post.setTitle(edTitle.getText().toString());
        post.setCaption(edLead.getText().toString());
        post.setBody(edBody.getText().toString());

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

        ReporterApplication.getInstance().updatePost(index, post);
        PostManager.getInstance().onPostsUpdated(this, false);
//        PostManager.getInstance().onPostUpdated(this, false, post);
        mainActivity.hideSoftKeyboard(getActivity());
    }

    private void loadData() {
        if (addNew == false) {
            edTitle.setText(post.getTitle());
            edTopic.setText(post.getTopic());
            edLead.setText(post.getCaption());
            edBody.setText(post.getBody());

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
        edLead.getText().clear();
        edBody.getText().clear();
    }

    private Post prepareSendData() {
        Post post = new Post();
        post.setWorkflowStatus(Post.PostWorkflowStatus.IN_PROGRESS);
        if (getString(title).equals(Post.PostType.SHORT.getFullStatus())) {
            post.setType(Post.PostType.SHORT.getStatus());
        } else {
            post.setType(Post.PostType.ALERT.getStatus());
        }
        post.setCreateDate(new Date().getTime());

        post.setTitle(edTitle.getText().toString());
        post.setTopic(edTopic.getText().toString());
        post.setCaption(edLead.getText().toString());
        post.setBody(edBody.getText().toString());

        post.getMetadata().setPackage_(MetadataUtil.getValueMetadata(containerPackages));
        post.getMetadata().setUrgency(spnUrgency.getSelectedItem().toString());
        post.getMetadata().setDistribition(spnDistribution.getSelectedItem().toString());
        post.getMetadata().setLanguage(spnLanguage.getSelectedItem().toString());
        post.getMetadata().setKeywords(edKeywords.getText().toString());
        post.getMetadata().setIptc(spnIPTC.getSelectedItem().toString());
        post.getMetadata().setAuthor(MetadataUtil.getValueMetadata(containerAuthors));
        post.getMetadata().setLabel(edLabel.getText().toString());
        post.getMetadata().setStatus(spnStatus.getSelectedItem().toString());
        post.getMetadata().setCity(edCity.getText().toString());
        post.getMetadata().setCountry(edCountry.getText().toString());
        post.getMetadata().setEditorial(edEditorial.getText().toString());
        post.getMetadata().setInfo(edInfo.getText().toString());
        post.getMetadata().setCredit(edCredit.getText().toString());
        post.getMetadata().setSource(edSource.getText().toString());
        post.getMetadata().setComment(edComment.getText().toString());

        if (addNew) {
            ReporterApplication.getInstance().addPost(post);
        } else {
            ReporterApplication.getInstance().updatePost(index, post);
        }

        PostManager.getInstance().onPostsUpdated(this, false);

        return post;
    }

    @Override
    public void onBackPressed() {
        if (addNew) {
            addNewPost();
        } else {
            editPost();
        }
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
}
