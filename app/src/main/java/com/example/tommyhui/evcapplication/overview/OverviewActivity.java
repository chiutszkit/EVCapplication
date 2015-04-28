package com.example.tommyhui.evcapplication.overview;

import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tommyhui.evcapplication.R;
import com.example.tommyhui.evcapplication.adapter.OverviewSearchAutoCompleteAdapter;
import com.example.tommyhui.evcapplication.utils.SearchUtil;
import com.example.tommyhui.evcapplication.utils.UIUtils;

import java.util.ArrayList;
import java.util.Locale;

public class OverviewActivity extends ActionBarActivity {

    private MenuItem searchItem;
    private SearchView searchView;
    private MenuItem result;

    //a handler is to handle process in another thread
    private Handler handler;

    //Search Mode
    private boolean isSearchMode;

    //test String list (replace your own list)
    private ArrayList<String> testlist = new ArrayList<>();

    //connect your list with the overview search adapter for filtering and display the result list layout
    private OverviewSearchAutoCompleteAdapter adapter;

    //a view used to obscure user the see the background and focus on the search bar and its result
    private View dimBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);

        isSearchMode = false;

        /*Use Customized Action Bar*/
        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.actionbar);

        /*Set Action Bar's Title*/
        //TextView title = (TextView)findViewById(R.id.action_bar_title);
        //title.setText("Overview");

        /*Set Action Bar's Icon*/
        //ImageView myImgView = (ImageView)findViewById(R.id.action_bar_icon);
        //myImgView.setImageResource(R.drawable.overview_icon);

        dimBackground = findViewById(R.id.overview_dim_background);
//        mListView = (ListView) findViewById(R.id.list_view);
//        mListView.setAdapter(new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1,
//                mStrings));
//        mListView.setTextFilterEnabled(true);
//        setupSearchView();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overview_options_menu, menu);

        return true;


        /*// Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overview_options_menu, menu);

//        // Get the SearchView and set the overview_searchable configuration
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        *//*Expand Search Bar*//*
        searchItem = menu.findItem(R.id.overview_action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getResources().getString(R.string.overview_search_view_title));

        result = menu.findItem(R.id.overview_status_text);
        result.setVisible(false);

//        Log.v("test-logging", "R.id.overview_action_search : " +  menu.findItem(R.id.overview_action_search));


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            *//*When texts are changed*//*
            public boolean onQueryTextChange(String query) {
                setSuggestionForSearch(query);
                return true;
            }
            @Override
            *//*When texts are submit*//*
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                *//*Clear the Query and Display the Result*//*
                searchView.setQuery("", false);
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
                result.setTitle(query);
                result.setVisible(true);

                Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        return true;*/
    }

    private void restoreActionBar(ActionBar actionBar){
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            //since your icon size is too large, so I have not set the original icon to it
            //actionBar.setIcon(R.drawable.overview_icon);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowCustomEnabled(false);
            ColorDrawable actionBarBg = new ColorDrawable(getResources().getColor(R.color.colorPrimary));
            actionBar.setBackgroundDrawable(actionBarBg);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        ActionBar actionBar = getSupportActionBar();

        if (isSearchMode) {
            actionBarModeSearch(actionBar, menu);
        } else {
            cancelActionBarModeSearch(actionBar);
            restoreActionBar(actionBar);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    //for detection of clicks in option item
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.action_search_trigger){
            isSearchMode = true;

            //call onCreateOptionMenu and onPrepareOptionMenu to inflate search view
            this.supportInvalidateOptionsMenu();
            return true;
        }

        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    //elements of the custom search view
    private AutoCompleteTextView searchField;
    private ImageButton backButton;
    private ImageButton clearButton;

    //init SearchView
    private View createCustomSearchView() {

        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = mInflater.inflate(R.layout.ab_overview_search_customview, null, false); //ab stands for 'ActionBar'

        //you have to replace it with your own data list (e.g. traffic data list)
        testlist = getListOfCountries();

        adapter = new OverviewSearchAutoCompleteAdapter(OverviewActivity.this, testlist);

        //autocomplete textView is a textView but can enable listener on the result items
        searchField = (AutoCompleteTextView) customView.findViewById(R.id.overview_search_search_field);
        searchField.setAdapter(adapter);
        searchField.setDropDownBackgroundDrawable(getResources().getDrawable(R.drawable.abc_item_background_overview_search));
        searchField.setDropDownHeight(getMaximumDropdownHeight());

        final View divider = (View) customView.findViewById(R.id.overview_search_divider);

        //back button is present for user to go back
        backButton = (ImageButton) customView.findViewById(R.id.overview_search_back_btn);
        ViewTreeObserver vto = backButton.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                backButton.getViewTreeObserver().removeOnPreDrawListener(this);
                int width = backButton.getMeasuredWidth();
                searchField.setDropDownHorizontalOffset(getMeasuredWidth(width));
                searchField.setDropDownVerticalOffset(divider.getMeasuredHeight());
                adapter.setLeftPaddingWidth(getMeasuredPaddingWidth(width));
                return true;
            }
        });


        clearButton = (ImageButton) customView.findViewById(R.id.overview_search_clear_btn);
        searchField.setHint(R.string.search_result_title);

        //after clicking an item
        searchField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //searchField.setText("");
                isSearchMode = false;
                supportInvalidateOptionsMenu();

                //implement your action here

                Toast.makeText(OverviewActivity.this, adapter.getmList().get(position), Toast.LENGTH_SHORT).show();
            }
        });

        //after clicking a back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isSearchMode = false;
                supportInvalidateOptionsMenu();
            }
        });

        //after clicking a clear button
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchField.setText("");
            }
        });

        //filtering job here
        searchField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                //TODO: method improvement for getting dropdown's length is required
                if (filter(s.toString()) && s.length() > 0) {
                    Log.i("test-logging", "shows pop up, dim background");

                    dimBackground.setVisibility(View.VISIBLE);

                } else {
                    Log.i("test-logging", "no popup");

                    dimBackground.setVisibility(View.INVISIBLE);
                }

            }
        });

        return customView;
    }

    private void actionBarModeSearch(ActionBar actionBar, Menu menu) {

        // hide menu items
        MenuItem searchItem = menu.findItem(R.id.action_search_trigger);
        MenuItem addressItem = menu.findItem(R.id.overview_status_text);
        searchItem.setVisible(false);
        addressItem.setVisible(false);

        // config action bar
        Drawable actionBarBg = getResources().getDrawable(R.drawable.search_bar_bg);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setBackgroundDrawable(actionBarBg);
        ColorDrawable searchActionBarBg = new ColorDrawable(getResources().getColor(android.R.color.white));
        actionBar.setBackgroundDrawable(searchActionBarBg);

        // setup custom view
        View customView = actionBar.getCustomView();
        if (customView == null) {
            customView = createCustomSearchView();
        }

        //set the search view in the actionbar using setCustomView
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        actionBar.setCustomView(customView, params);

        //by default, it will leave some left padding in the action bar after inflating custom view
        //this is the method to eliminate it
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0,0);

        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                searchField.requestFocus();
                showKeyboard(searchField);
            }
        }, 100);

        searchField.setText("");

    }

    //get the width of the gap between the leftmost part of the search field and the icon with margins
    private int getMeasuredWidth(int backButtonWidth){

        int buttonWidth = backButtonWidth;

        int buttonPadding = backButton.getPaddingLeft()+backButton.getPaddingRight();

        LinearLayout.LayoutParams searchFieldParams = (LinearLayout.LayoutParams) searchField.getLayoutParams();
        int searchFieldMarginLeft = searchFieldParams.leftMargin;

        int searchFieldPaddingLeft = searchField.getPaddingLeft();

        return -(buttonWidth + buttonPadding + searchFieldMarginLeft + searchFieldPaddingLeft);
        //return avatarWidth;
    }

    //get the width from the left of the screen to the text listing in the dropdown list
    private int getMeasuredPaddingWidth(int backButtonWidth){
        LinearLayout.LayoutParams searchFieldParams = (LinearLayout.LayoutParams) searchField.getLayoutParams();
        int searchFieldMarginLeft = searchFieldParams.leftMargin;

        return (backButtonWidth+searchFieldMarginLeft);
    }


    private void cancelActionBarModeSearch(ActionBar actionBar) {

        if (searchField != null) {
            searchField.setText("");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //hideKeyboard(getCurrentFocus());
                }
            });
        }
    }

    protected void hideKeyboard(final View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    protected void showKeyboard(final View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
    }

    protected void setSuggestionForSearch(String query) {
        // TODO Auto-generated method stub
        ArrayList<String> suggestionArray;

        suggestionArray = getListOfCountries();

        searchView.setSuggestionsAdapter(SearchUtil
                .getCursorAdapter(OverviewActivity.this, suggestionArray,
                        query));

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @Override
            public boolean onSuggestionClick(int position) {

                showSearchResult(SearchUtil
                        .getItemTag((MatrixCursor) searchView
                                .getSuggestionsAdapter().getItem(position)));
                return false;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }
        });

    }

    private ArrayList<String> getListOfCountries() {
        // TODO Auto-generated method stub

        ArrayList<String> items = new ArrayList<String>();

        String[] isoCountries = Locale.getISOCountries();
        for (String country : isoCountries) {
            Locale locale = new Locale("en", country);
            //          String iso = locale.getISO3Country();
            //          String code = locale.getCountry();
            String name = locale.getDisplayCountry();

            if (!"".equals(name)) {
                items.add(name);
            }
        }

        return items;

    }
    protected void showSearchResult(String itemTag) {
        // TODO Auto-generated method stub

            /*Clear the Query and Display the Result*/
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.onActionViewCollapsed();
        result.setTitle(itemTag);
        result.setVisible(true);

        Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();
    }

//        // Associate overview_searchable configuration with the SearchView
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.overview_action_search).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));


    //get the maximum dropdown height
    private int getMaximumDropdownHeight(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels; //height in px

        float halfHeight = screenHeight/2;
        //float dropDownItemHeight = UIUtils.dipToPixels(getActivity(), groupSearchAutoCompleteAdapter.getGroupSearchItemHeight());
        float dropDownItemHeight = UIUtils.dipToPixels(OverviewActivity.this, 60);
        int finalHeight = 0;
        while(finalHeight <= halfHeight){
            finalHeight = (int) (finalHeight + dropDownItemHeight);
        }

        if((finalHeight-halfHeight)>(dropDownItemHeight/2)){
            return (int) (finalHeight - dropDownItemHeight);
        } else {
            return finalHeight;
        }
    }

    //search view filtering function
    private boolean filter(String s){

        Log.i("test-logging","s.toString() : " + s);

        //you have to reference to the list for returning the suited results
        for(String item : testlist){
            if(item.toLowerCase().contains(s.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.overview_action_search) {
            onSearchRequested();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}