package be.belga.reporter.mobile.reporter.screens.metadata;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import be.belga.reporter.mobile.reporter.application.ReporterApplication;
import be.belga.reporter.mobile.reporter.application.ReporterFragment;
import be.belga.reporter.mobile.reporter.model.Metadata;
import be.belga.reporter.mobile.reporter.screens.main.MainActivity;
import be.belga.reporter.mobile.reporter.screens.myshort.CustomSpinnerAdapter;
import be.belga.reporter.utils.MetadataUtil;
import belga.be.belgareporter.R;

public class MyMetadataFragment extends ReporterFragment implements MainActivity.OnBackPressed, View.OnClickListener {
    private static final String TAG = MyMetadataFragment.class.getSimpleName();

    private MainActivity mainActivity;
    private Menu menu;

    private Metadata mMetadata;
    private Metadata cloneMetadata;

    private RelativeLayout detailLayout;
    private LinearLayout inputLayout;

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

    public static MyMetadataFragment getInstance() {
        MyMetadataFragment fragment = new MyMetadataFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        packagesLst = new ArrayList<>();
        authorsLst = new ArrayList<>();
        mMetadata = new Metadata();
        cloneMetadata = Metadata.parseFromJSON(ReporterApplication.getInstance().getPersistentPreference().getString(ReporterApplication.KEY_ClONE_METADATA, null));
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_manage_default_metadata, container, false);

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

        mainActivity.setTitle(getString(R.string.my_metadata));
        mainActivity.setDrawerEnable(false);

        menu.findItem(R.id.clear_menu).setIcon(mainActivity.setFontAwesomeMenu(18, R.string.clear_icon));
        menu.findItem(R.id.send_menu).setVisible(false);
        menu.findItem(R.id.copy_menu).setIcon(mainActivity.setFontAwesomeMenu(18, R.string.copy_icon));
        menu.findItem(R.id.copy_menu).setVisible(cloneMetadata != null ? false : true);
        menu.findItem(R.id.paste_menu).setIcon(mainActivity.setFontAwesomeMenu(18, R.string.paste_icon));
        menu.findItem(R.id.paste_menu).setVisible(menu.findItem(R.id.copy_menu).isVisible() ? false : true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mainActivity.isFragmentOnTop(this)) {
            switch (item.getItemId()) {
                case R.id.clear_menu:
                    clearText();
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
                    updateMetadata();
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

    private void loadData() {
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

    private void updateMetadata() {
        MetadataUtil.setDataMetadata(mMetadata,
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
        mainActivity.hideSoftKeyboard(getActivity());
        ReporterApplication.getInstance().setUserMetadata(new Gson().toJson(mMetadata));
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    private void clearText() {
        if (inputLayout.getVisibility() != View.GONE) {
            for (int i = 0; i < inputLayout.getChildCount(); i++) {
                if (inputLayout.getChildAt(i) instanceof EditText)
                    ((EditText) inputLayout.getChildAt(i)).getText().clear();
            }
        }
    }

    @Override
    public void onBackPressed() {
        updateMetadata();
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
