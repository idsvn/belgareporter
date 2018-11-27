package be.belga.reporter.mobile.reporter.screens.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import be.belga.reporter.mobile.reporter.application.ReporterApplication;
import be.belga.reporter.mobile.reporter.application.ReporterFragment;
import be.belga.reporter.mobile.reporter.model.Post;
import be.belga.reporter.mobile.reporter.screens.login.StartActivity;
import be.belga.reporter.mobile.reporter.screens.metadata.MyMetadataFragment;
import be.belga.reporter.mobile.reporter.screens.mymedia.MediaDetailFragment;
import be.belga.reporter.mobile.reporter.screens.mymedia.MediaFragment;
import be.belga.reporter.mobile.reporter.screens.mypicture.PictureDetailFragment;
import be.belga.reporter.mobile.reporter.screens.mypicture.PictureFragment;
import be.belga.reporter.mobile.reporter.screens.myprofile.MyProfileFragment;
import be.belga.reporter.mobile.reporter.screens.myshort.ShortDetailFragment;
import be.belga.reporter.mobile.reporter.screens.setting.NetworkFragment;
import be.belga.reporter.mobile.views.IconDrawable;
import be.belga.reporter.mobile.views.IconTextView;
import be.belga.reporter.utils.FileUtil;
import belga.be.belgareporter.R;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ListView drawerListview;
    private IconTextView btnChangeMenu;
    private TextView textviewUserName;

    private ActionBarDrawerToggle drawerToggle;
    private ManageMenuListViewAdapter adapterManage = null;
    private NavigationMenuListViewAdapter adapterNavigation = null;

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ReporterApplication.getInstance().clearCloneMetadata();

        requestPermission();
        FileUtil.getTusURLStore(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListview = (ListView) findViewById(R.id.listview_drawer);
        btnChangeMenu = (IconTextView) findViewById(R.id.btn_change_menu);
        textviewUserName = (TextView) findViewById(R.id.textview_user_name);

        adapterNavigation = new NavigationMenuListViewAdapter(MainActivity.this);
        adapterManage = new ManageMenuListViewAdapter(MainActivity.this);

        drawerListview.setAdapter(adapterNavigation);

        btnChangeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnChangeMenu.getRotation() == 180) {
                    btnChangeMenu.setRotation(0);
                    drawerListview.setAdapter(adapterManage);
                } else {
                    btnChangeMenu.setRotation(180);
                    drawerListview.setAdapter(adapterNavigation);
                }
            }
        });

        textviewUserName.setText(ReporterApplication.getInstance().getUser().getFirstName() + " " + ReporterApplication.getInstance().getUser().getLastName());

        drawerListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clearBackStack();
                if (drawerListview.getAdapter().equals(adapterNavigation)) {
                    switch (position) {
                        case 1:
                            openShortDetailFragment(R.string.alert_text);
                            break;
                        case 2:
                            openShortDetailFragment(R.string.short_text);
                            break;
                        case 3:
                            openPictureFragment();
                            break;
                        case 4:
                            openMediaFragment(R.string.video);
                            break;
                        case 5:
                            openMediaFragment(R.string.audio);
                            break;
                    }
                } else {
                    switch (position) {
                        case 0:
                            openMyMetadataFragment();
                            break;
                        case 1:
                            openSettingDialogFragment();
                            break;
                        case 2:
                            openMyProfileFragment();
                            break;
                    }
                }

                drawerLayout.closeDrawers();
            }
        });

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_menu, R.string.close_menu) {
            @Override
            public void onDrawerClosed(View drawerView) {
                invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });

        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getWindow().setStatusBarColor(this.getResources().getColor(R.color.viking));
        setDrawerEnable(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    public boolean isFragmentOnTop(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);
        String str = backStackEntry.getName();
        return fragmentManager.findFragmentByTag(str) == fragment;
    }

    public void setDrawerEnable(boolean enable) {
        if (enable) {
            drawerToggle.setDrawerIndicatorEnabled(true);
            drawerLayout.setEnabled(true);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            drawerToggle.setDrawerIndicatorEnabled(false);
            drawerLayout.setEnabled(false);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public void onBackPressed() {
        ReporterFragment reporterFragment = ((ReporterFragment) getCurrentFragment());
        if (reporterFragment == null || reporterFragment.onFinish()) {
            getFragmentBackPressed();
            super.onBackPressed();
        }
    }

    private void getFragmentBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment f : fragments) {
            if (f != null) {
                if (f instanceof PictureFragment) {
                    ((PictureFragment) f).onBackPressed();
                } else if (f instanceof ShortDetailFragment) {
                    ((ShortDetailFragment) f).onBackPressed();
                } else if (f instanceof PictureDetailFragment) {
                    ((PictureDetailFragment) f).onBackPressed();
                }
            }

        }
    }

    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() < 0) return null;
        return fragmentManager.findFragmentById(R.id.fragment_container);
    }

    protected void clearBackStack() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public void signout() {
        ReporterApplication.getInstance().signout();
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }

    public void openShortDetailFragment(int title) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fm.popBackStack("ShortDetailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ShortDetailFragment shortDetailFrament = ShortDetailFragment.getInstance(title);
        ft.add(R.id.fragment_container, shortDetailFrament, "ShortDetailFragment");
        ft.addToBackStack("ShortDetailFragment");
        ft.commit();
    }

    public void openShortDetailFragment(int title, Post post, int index) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fm.popBackStack("ShortDetailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ShortDetailFragment shortDetailFrament = ShortDetailFragment.getInstance(title, post, index);
        ft.add(R.id.fragment_container, shortDetailFrament, "ShortDetailFragment");
        ft.addToBackStack("ShortDetailFragment");
        ft.commit();
    }

    public void openPictureFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fm.popBackStack("PictureFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        PictureFragment pictureFrament = PictureFragment.getInstance();
        ft.add(R.id.fragment_container, pictureFrament, "PictureFragment");
        ft.addToBackStack("PictureFragment");
        ft.commit();
    }

    public void openPictureDetailFragment(Post post, int index) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fm.popBackStack("PictureDetailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        PictureDetailFragment pictureDetailFragment = PictureDetailFragment.getInstance(post, index);
        ft.add(R.id.fragment_container, pictureDetailFragment, "PictureDetailFragment");
        ft.addToBackStack("PictureDetailFragment");
        ft.commit();
    }

    public void openPictureDetailFragment(Post post) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fm.popBackStack("PictureDetailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        PictureDetailFragment pictureDetailFragment = PictureDetailFragment.getInstance(post);
        ft.add(R.id.fragment_container, pictureDetailFragment, "PictureDetailFragment");
        ft.addToBackStack("PictureDetailFragment");
        ft.commit();
    }

    public void openMediaFragment(int title) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fm.popBackStack("MediaFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        MediaFragment mediaFragment = MediaFragment.getInstance(title);
        ft.add(R.id.fragment_container, mediaFragment, "MediaFragment");
        ft.addToBackStack("MediaFragment");
        ft.commit();
    }

    public void openMediaDetailFragment(Post post, int title) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fm.popBackStack("MediaDetailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        MediaDetailFragment videoDetailFrament = MediaDetailFragment.getInstance(post, title);
        ft.add(R.id.fragment_container, videoDetailFrament, "MediaDetailFragment");
        ft.addToBackStack("MediaDetailFragment");
        ft.commit();
    }

    public void openMediaDetailFragment(Post post, int index, int title) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fm.popBackStack("MediaDetailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        MediaDetailFragment videoDetailFrament = MediaDetailFragment.getInstance(post, index, title);
        ft.add(R.id.fragment_container, videoDetailFrament, "MediaDetailFragment");
        ft.addToBackStack("MediaDetailFragment");
        ft.commit();
    }

    public void openMyMetadataFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fm.popBackStack("MyMetadataFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        MyMetadataFragment myMetadataFragment = MyMetadataFragment.getInstance();
        ft.add(R.id.fragment_container, myMetadataFragment, "MyMetadataFragment");
        ft.addToBackStack("MyMetadataFragment");
        ft.commit();
    }

    public void openMyProfileFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fm.popBackStack("MyProfileFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        MyProfileFragment myProfileFragment = MyProfileFragment.getInstance();
        ft.add(R.id.fragment_container, myProfileFragment, "MyProfileFragment");
        ft.addToBackStack("MyProfileFragment");
        ft.commit();
    }

    public void openSettingDialogFragment() {
        FragmentManager fm = getSupportFragmentManager();
        NetworkFragment settingDialogFragment = NetworkFragment.getInstance();
        settingDialogFragment.show(fm, "NetworkFragment");
    }

    public Point getSizeWindowDisplay() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public void hideSoftKeyboard(Activity activity) {
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public IconDrawable setFontAwesomeMenu(int size, int iconId) {
        IconDrawable iconDrawable = new IconDrawable(this);
        iconDrawable.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
        iconDrawable.setTypeface(IconDrawable.getTypeface(this));
        iconDrawable.setText(getResources().getText(iconId));
        iconDrawable.setTextColor(getResources().getColor(android.R.color.white));

        return iconDrawable;
    }

    public long getSizePicture(String url) {
        File imgFile = new File(url);
        Bitmap bitmapOrg = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        Bitmap bitmap = bitmapOrg;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        return imageInByte.length;
    }

    //----------Edited by Tai 23/11/2018----------//
    public long getSizePicture(String url,String typeFile) {
        File imgFile = new File(url);

        //-------------Tai-------------//
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // Get bitmap dimensions before reading...
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgFile.getAbsolutePath(), opts);
        opts.inJustDecodeBounds = false; // This time it's for real!
        int sampleSize = 1;
        opts.inSampleSize = sampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), opts);
        //-------------Tai-------------//
//        Bitmap bitmapOrg = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//        Bitmap bitmap = bitmapOrg;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.valueOf(typeFile.substring(6).toUpperCase()),93, stream);
        byte[] imageInByte = stream.toByteArray();
        return imageInByte.length;
    }
    //----------------Edited by Tai-------------//

    public long getSizeVideo(String url) {
        File videoFile = new File(url);
        return videoFile.length();
    }

    public boolean requestPermission() {
        int permissionReadExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionWriteExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionRecordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

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

        if (permissionRecordAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), ReporterApplication.CHANGE_IMAGE_REQUEST_CODE);
            return false;
        }

        return true;
    }

    public void uploadName(String firstName, String lastName){
        textviewUserName.setText(firstName + " " + lastName);
    }

    public interface OnBackPressed {
        void onBackPressed();
    }

}
