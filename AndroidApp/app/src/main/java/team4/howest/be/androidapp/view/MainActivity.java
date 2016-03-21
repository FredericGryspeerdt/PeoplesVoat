package team4.howest.be.androidapp.view;


import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import team4.howest.be.androidapp.Navigation.KillFragmentEvent;
import team4.howest.be.androidapp.Navigation.NavigationEvent;
import team4.howest.be.androidapp.R;
import team4.howest.be.androidapp.VoatApplication;
import team4.howest.be.androidapp.model.DefaultSubverseResponse;
import team4.howest.be.androidapp.auth.AuthenticationActivity;
import team4.howest.be.androidapp.auth.Authenticator;
import team4.howest.be.androidapp.database.Contract;
import team4.howest.be.androidapp.model.User;
import team4.howest.be.androidapp.model.UserSubscription;
import team4.howest.be.androidapp.model.UserSubscriptionsResponse;
import team4.howest.be.androidapp.service.DrawerHelper;
import team4.howest.be.androidapp.service.RandomSubverseHelper;
import team4.howest.be.androidapp.service.VoatApiService;
import team4.howest.be.androidapp.service.VoatClient;
import team4.howest.be.androidapp.service.VoatLegacyApiService;
import team4.howest.be.androidapp.service.VoatLegacyClient;
import team4.howest.be.androidapp.viewmodel.Sortable;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        FragmentManager.OnBackStackChangedListener {

    private static final String SELECTED_SUBVERSE = "SELECTED_SUBVERSE";
    public static final String DEFAULT_SUBVERSES = "DEFAULT_SUBVERSES";
    private static final String SELECTED_SORT = "SELECTED_SORT";
    private static final String SORTABLE = "SORTABLE";


    public ArrayAdapter<String> adapter; //deze wordt gebruikt om de random suverse toe te voegen aan de spinner
    public Spinner spinner;
    private Toolbar toolbar;
    private CoordinatorLayout coordinatorLayout;

    public ArrayList<String> listSubverses; //om spinner te vullen
    String selectedSubverse;
    String selectedSortOrder = "Hot";

    private DrawerLayout drawerLayout;
    //private String userName;
    private User user;
    private String accessToken;
    public boolean isConnected = false;

    private Sortable sortable;
    public boolean watchingSavedSubmissions;

    private Boolean isFirstStart; //nodig voor search
    private Boolean isLoggedIn = false;
    private Tracker mTracker;
    private SharedPreferences sharedPrefs;
    private Account account;

    private MainActivity mainActivity = this;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        VoatApplication  application = (VoatApplication) getApplication();
        mTracker = application.getDefaultTracker();

        isFirstStart = true;

        setContentView(R.layout.activity_main); //drawer opent over toolbar
        //setContentView(R.layout.activity_main_alternative); //drawer opent niet over toolbaar (blijkbaar geen Material design)

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        initUser();
        initDrawer();
        initCustomActionbar();

        if (savedInstanceState != null) { //wordt opgeroepen bij bv. orientation change

            listSubverses = savedInstanceState.getStringArrayList(DEFAULT_SUBVERSES);
            initializeSpinnerAdapter();


            selectedSortOrder = savedInstanceState.getString(SELECTED_SORT);

            spinner.setTag(savedInstanceState.getInt(SELECTED_SUBVERSE));
            spinner.setSelection(savedInstanceState.getInt(SELECTED_SUBVERSE));
        }

        if (listSubverses == null) {
            loadSubverses(); //eerste keer
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);
    }

    public void onEvent(KillFragmentEvent killFragmentEvent ){
        getSupportFragmentManager().popBackStack();
    }

    public void onEvent(NavigationEvent navigationEvent) {
        if (navigationEvent.fragment instanceof Sortable) {
            sortable = (Sortable) navigationEvent.fragment;
        }
        else{
            sortable = null;
        }

        if(navigationEvent.fragment.getClass() == SavedSubmissionsFragment.class)
            watchingSavedSubmissions = true;
        else
            watchingSavedSubmissions = false;

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content, navigationEvent.fragment, selectedSubverse);

        if(!isFirstStart)
        {
            transaction.addToBackStack(navigationEvent.toString());
        }
        else
        {
            getSupportFragmentManager().popBackStackImmediate();//getSupportFragmentManager().getBackStackEntryAt(0).getName(),FragmentManager.POP_BACK_STACK_INCLUSIVE);
            isFirstStart = false;
        }

        // Commit the transaction
        transaction.commit();

        sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        boolean prefTrackMe = sharedPrefs.getBoolean("prefTrackMe", true);
        if(prefTrackMe) {
            mTracker.setScreenName("Image~" + navigationEvent.fragment.toString());
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    //region SPINNER

    //user is niet ingelogd -> default subverses ophalen
    private void loadSubverses() {

        //checken of gebruiker is ingelogd
        if(isLoggedIn){ //ingelogd --> subscriptions voor deze gebruiker ophalen
            VoatClient voatClient = new VoatClient();
            VoatApiService voatApiService = voatClient.getApiService();
            voatApiService.getUserSubscriptions(account.name, new Callback<UserSubscriptionsResponse>() {
                @Override
                public void success(UserSubscriptionsResponse userSubscriptionsResponse, Response response) {
                    if (userSubscriptionsResponse.getSuccess()){

                        listSubverses = new ArrayList<String>();
                        ArrayList<UserSubscription> listUserSubscriptions = userSubscriptionsResponse.getData();

                        for(UserSubscription subscription : listUserSubscriptions){
                            if (subscription.getTypeName().equals("Subverse")){

                                listSubverses.add(subscription.getName());
                            }
                        }
                        initializeSpinnerAdapter();

                    } else {
                        String errorType = userSubscriptionsResponse.getError().getType();
                        String errorMessage= userSubscriptionsResponse.getError().getMessage();

                        Toast.makeText(getApplicationContext(), errorType + ": " + errorMessage,Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

        } else { //guest --> default subverses ophalen

            VoatLegacyClient voatLegacyClient = new VoatLegacyClient();

            VoatLegacyApiService voatLegacyApiService = voatLegacyClient.getApiService();

            voatLegacyApiService.getDefaultSubverses(new Callback<DefaultSubverseResponse>() {
                @Override
                public void success(DefaultSubverseResponse defaultSubverseResponse, Response response) {
                    listSubverses = new ArrayList<String>();
                    listSubverses.addAll(defaultSubverseResponse.getDefaultSubverses());
                    initializeSpinnerAdapter();
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }

    }

    //als alle subverses geladen zijn -> steek ze in de spinner
    private void initializeSpinnerAdapter() {
        // Create an ArrayAdapter using the string array and a default spinner drawerLayout
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listSubverses);
        // Specify the drawerLayout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    //verwerken click op spinner item
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        //geselecteerde subverse ophalen
        selectedSubverse = parent.getItemAtPosition(pos).toString();

        if (spinner.getTag() != null) {  //--> tag is al eens ingesteld
            if ((int) spinner.getTag() != pos) {
                //Toast.makeText(this,selectedSubverse,Toast.LENGTH_SHORT).show();
                startSubverseFragment(selectedSubverse, selectedSortOrder);
            }

        } else { //---> tag is nog niet ingesteld
            //Toast.makeText(this,selectedSubverse,Toast.LENGTH_SHORT).show();
            startSubverseFragment(selectedSubverse, selectedSortOrder);
        }

        spinner.setTag(pos);
    }

    //endregion

    //kijken of er al dan niet een account bestaat
    private void initUser() {
        AccountManager accountManager = AccountManager.get(MainActivity.this);
        Account[] accounts = accountManager.getAccountsByType(Contract.ACCOUNT_TYPE);
        if (accounts.length > 0) {
            account = accountManager.getAccountsByType(Contract.ACCOUNT_TYPE)[0];

            //userName = account.name;
            accessToken = accountManager.peekAuthToken(account, "access_token");

            ((VoatApplication) getApplicationContext()).setBearer(accessToken);
            isLoggedIn = true;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(ContentResolver.getPeriodicSyncs(account, "team4.howest.be.androidapp.provider").size()==0)
                        ContentResolver.addPeriodicSync(account, "team4.howest.be.androidapp.provider", Bundle.EMPTY, 18000); //sync als er geen sync bestaat
                }
            }, 5000);
        }
    }

    //region NAVIGATION DRAWER

    //Navigation Drawer maken
    private void initDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer_main);

        //if (account.name != null)
        if(account != null)
            navigationView.inflateMenu(R.menu.drawer_menu_authenticated);//ingelogde-user menu
        else
            navigationView.inflateMenu(R.menu.drawer_menu);//niet ingelogde-user menu

        setupDrawerContent(navigationView);

        //toon user-info in de Drawer-Header als user ingelogd is
        //if (account.name != null)
        if(account != null)
            new DrawerHelper(this, getSupportFragmentManager()).showDrawerHeaderUserInfo(account.name);
    }

    //Navigation Drawer opvullen
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        menuItem.setChecked(false);
                        return true;
                    }
                });

        //if (account.name != null)//delete account button
        if(account != null)
        {
            MenuItem deleteAccountItem = navigationView.getMenu().findItem(R.id.nav_delete_account);
            ImageView deleteAccount = (ImageView) deleteAccountItem.getActionView().findViewById(R.id.drawer_delete_account);
            deleteAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create(); //Read Update
                    alertDialog.setTitle("Delete account: " + account.name);
                    alertDialog.setMessage("Are you sure you want to delete this account?");

                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //niets doen
                        }
                    });

                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) { //delete account
                            Authenticator authenticator = new Authenticator(MainActivity.this);

                            if (authenticator.deleteAccount()) {
                                Toast.makeText(MainActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();

                                //bearer op null zetten
                                accessToken = null;
                                ((VoatApplication) getApplicationContext()).setBearer(accessToken);
                                isLoggedIn = false;

                                //deze activity opnieuw opstarten (UI veranderd)
                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Oops, couldn't delete account", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    alertDialog.show();
                }
            });
        }
    }

    //Fragments tonen naarmate een menu-item
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = null;
        Class fragmentClass;

        switch (menuItem.getItemId()) {
            case R.id.nav_log_in:
                Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
                startActivity(intent);
                return; //return i.p.v. break (methode verlaten, geen fragment oproepen)
            case R.id.nav_my_subverses:
                fragmentClass = MySubversesFragment.class;
                break;
            case R.id.nav_manage_subverses:
                ManageSubversesFragment manageSubversesFragment = new ManageSubversesFragment();
                Bundle args = new Bundle();
                args.putString(manageSubversesFragment.USER, account.name);
                manageSubversesFragment.setArguments(args);

                EventBus.getDefault().post(new NavigationEvent(manageSubversesFragment));
                drawerLayout.closeDrawers();
                return;
            case R.id.nav_profile:
                fragment = UserFragment.newInstance(user);
                EventBus.getDefault().post(new NavigationEvent(fragment));
                drawerLayout.closeDrawers();
                return;
            case R.id.nav_saved_items:
                fragmentClass = SavedSubmissionsFragment.class;
                break;
            case R.id.nav_delete_account:
                Toast.makeText(MainActivity.this, "Press the icon to delete", Toast.LENGTH_SHORT).show();
                return; //return i.p.v. break (methode verlaten, geen fragment oproepen)
            case R.id.nav_search_submissions:
                fragmentClass = SearchSubmissionFragment.class;
                break;
            case R.id.nav_random_subverse:
                RandomSubverseHelper.getRandomSubverse(MainActivity.this);
                return;
            case R.id.nav_settings:
                SettingsFragment settingsFragment = new SettingsFragment();
                EventBus.getDefault().post(new NavigationEvent(settingsFragment));
                drawerLayout.closeDrawers();
                return;
            case R.id.nav_help:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://peoplesvoat.askbot.com/"));
                startActivity(browserIntent);
                drawerLayout.closeDrawers();
                DrawerHelper.showInfo(MainActivity.this);
                return;
            case R.id.nav_review_this_app:
                DrawerHelper.showReviewThisApp(mainActivity, MainActivity.this);
                return;
            case R.id.nav_browse_subverses:
                fragmentClass = BrowseSubversesFragment.class;
                break;
            default:
                fragmentClass = BlankFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        EventBus.getDefault().post(new NavigationEvent(fragment));
        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();
    }
    //endregion

    //region CUSTOM ACTIONBAR
    private void initCustomActionbar() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        spinner = (Spinner) findViewById(R.id.subverses_spinner);

        toolbar = (Toolbar) findViewById(R.id.toolbar); // Set a Toolbar to replace the ActionBar.

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null); //de titel van de app ('androidapp') wordt niet meer weergegeven in toolbar

            // Set the padding to match the Status Bar height
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
            toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColorDark));
        }

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);

        // Set the drawerLayout toggle as the DrawerListener
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //Sync the state
        actionBarDrawerToggle.syncState();
    }
    //endregion

    //Navigation Drawer openen en sluiten via de optie-knop
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle tool bar / action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_sort:
                showSortPopupMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //region SORT (TOOLBAR)
    private void showSortPopupMenu() {
        if (sortable != null) {
            View v = findViewById(R.id.action_sort);

            final PopupMenu popupMenu = new PopupMenu(this, v);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.popup_menu_sort, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.sort_hot:
                            selectedSortOrder = "Hot";
                            //Toast.makeText(this,"HOT",Toast.LENGTH_SHORT).show();
                            startSubverseFragment(selectedSubverse, "Hot");
                            //sortable.setSortOrder(selectedSortOrder);
                            return true;

                        case R.id.sort_new:
                            selectedSortOrder = "New";
                            //Toast.makeText(this,"NEW",Toast.LENGTH_SHORT).show();
                            startSubverseFragment(selectedSubverse, "New");
                            //sortable.setSortOrder(selectedSortOrder);
                            return true;

                        case R.id.sort_top:
                            selectedSortOrder = "Top";
                            //Toast.makeText(this,"TOP",Toast.LENGTH_SHORT).show();
                            startSubverseFragment(selectedSubverse, "Top");
                            //sortable.setSortOrder(selectedSortOrder);
                            return true;

                        default:
                            return false;
                    }
                }
            });

            //zorgt er voor dat de gekozen sort wijze gechecked staat in het popup menu
            MenuItem menuItem = null;
            switch (selectedSortOrder) {
                case "Hot":
                    menuItem = popupMenu.getMenu().findItem(R.id.sort_hot);
                    break;
                case "New":
                    menuItem = popupMenu.getMenu().findItem(R.id.sort_new);
                    break;
                case "Top":
                    menuItem = popupMenu.getMenu().findItem(R.id.sort_top);
                    break;
            }
            if (menuItem != null) {
                menuItem.setChecked(true);
            }
            popupMenu.show();
        }
    }
    //endregion

    //Nodig voor hamburger-menu-icon (in ActionBar)
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    //toolbar drawerLayout inladen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_appbar, menu);

        toolbar.setTitle(null);
        spinner.setVisibility(View.VISIBLE);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.action_search).setVisible(false);

        if(isFirstStart) {
            loadSubverses();
        }
        return true;
    }

    private void startSubverseFragment(String selectedSubverse, String sort) {
        // Create fragment and give it an argument for the selected subverse
        SubverseFragment subverseFragment = new SubverseFragment();
        Bundle args = new Bundle();
        args.putString(subverseFragment.SELECTED_SUBVERSE, selectedSubverse);
        args.putString(SubverseFragment.SELECTED_SORT, sort);
        subverseFragment.setArguments(args);

        EventBus.getDefault().post(new NavigationEvent(subverseFragment));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    //region NAVIGATION HANDLING
    @Override
    public void onBackStackChanged() {
        //Toast.makeText(this, "Backstackchanged", Toast.LENGTH_LONG).show();

        //wnn een gebruik op de back knop drukt moet de UI een update krijgen
        //Toolbar: spinner moet juiste subverse aanduiden

    }
    //endregion

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_SUBVERSE, spinner.getSelectedItemPosition());
        outState.putStringArrayList(DEFAULT_SUBVERSES, (ArrayList<String>) listSubverses);
        outState.putString(SELECTED_SORT, selectedSortOrder);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);

        int posSelectedSubverse = savedInstanceState.getInt(SELECTED_SUBVERSE);
        spinner.setSelection(posSelectedSubverse);
    }

    @Override
    public void onBackPressed()
    {
        FragmentManager fm = getSupportFragmentManager();

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this);

        boolean prefClosePrompt = sharedPreferences.getBoolean("prefShowClosePrompt", true);

        if(fm.getBackStackEntryCount()==0 && prefClosePrompt)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit");
            builder.setMessage("Are You Sure?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else
        {
            super.onBackPressed();
        }
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(final Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if(currentNetworkInfo.isConnected()){
                //Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                isConnected = true;
            }else{
                //Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_LONG).show();
                Snackbar.make(coordinatorLayout, "No connection", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mainActivity.recreate();
                            }
                        })
                        .setActionTextColor(Color.RED)
                        .show();
                isConnected = false;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mConnReceiver);
    }
}