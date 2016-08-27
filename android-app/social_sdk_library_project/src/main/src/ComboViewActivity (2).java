/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.browser.bookmark;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.view.ViewGroup;
import android.widget.TextView;


import com.android.browser.BrowserPreferencesPage;
import com.android.browser.Controller;
import com.android.browser.R;
import com.android.browser.UI.ComboViews;
import com.android.browser.bookmark.BrowserBookmarksAccountPage.onReloadFragmentListener;
import com.android.browser.bookmark.BrowserBookmarksAdapter.FragmentEditListener;


import java.util.ArrayList;
import java.util.List;

public class ComboViewActivity extends Activity implements CombinedBookmarksCallbacks,
        OnClickListener, onReloadFragmentListener {

    private static final String STATE_SELECTED_TAB = "tab";
    public static final String EXTRA_COMBO_ARGS = "combo_args";
    public static final String EXTRA_INITIAL_VIEW = "initial_view";

    public static final String EXTRA_OPEN_SNAPSHOT = "snapshot_id";
    public static final String EXTRA_OPEN_ALL = "open_all";
    public static final String EXTRA_CURRENT_URL = "url";

    public static final String FRAGMENT_BOOKMARK_TAG = "bookmark";
    public static final String FRAGMENT_HISTORY_TAG = "history";
    public static final String FRAGMENT_LOCAL_BOOKMARK_TAG = "local_bookmark";
    public static final String FRAGMENT_ACCOUNT_BOOKMARK_TAG = "account_bookmark";

    public static final String FRAGMENT_RELOAD_ID = "reload_id";

    public static final int FRAGMENT_HISTORY = 0;
    public static final int FRAGMENT_LOCAL_BOOKMARK = 1;
    public static final int FRAGMENT_ACCOUNT_BOOKMARK = 2;

    private static final int ANIM_IN = 200;
    private static final int ANIM_OUT = 200;

    private String mCurrentFragmentTag;


    private TextView mActionNewEvent;
    private TextView mActionDown;
    private TextView mBackBtn;
    private View mBackActionView;
    private View mDoneActionView;

    private String mAccountName;
    private String mFolderName;
    private String mParentFolderName;
    private OnChangeFolderListener mListener;
    private Bundle mBundle;

    private List<String> backNameList = new ArrayList<String>();
    // save bookmark folderID
    private long mParentID;
    // need reload folder id
    private long mReloadID;
    // current folder id
    private long mCurrentID;

    private int mReloadPosition;
    private boolean mFolderState = false;
    // select animation
    private boolean isChange = false;
    // mark bookmark whether edit state.
    private boolean mEditState = false;

    // private onItemCloceListener mItemCloseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        Bundle extras = getIntent().getExtras();
        mBundle = extras.getBundle(EXTRA_COMBO_ARGS);
        String svStr = extras.getString(EXTRA_INITIAL_VIEW, null);
        ComboViews startingView = svStr != null
                ? ComboViews.valueOf(svStr)
                : ComboViews.Bookmarks;

        setContentView(R.layout.simple_frame_layout);
        initActionBar();

        switch (startingView) {
            case Bookmarks:
                mActionNewEvent.setText(R.string.tab_bookmarks);
                startFragment(FRAGMENT_LOCAL_BOOKMARK, mBundle, true);
                break;
            case History:
                mActionNewEvent.setText(R.string.tab_history);
                startFragment(FRAGMENT_HISTORY, mBundle, true);
                break;
            default:
                break;
        }
    }

    private void initActionBar() {
        LayoutInflater inflater = (LayoutInflater) getActionBar()
                .getThemedContext().getSystemService(this.LAYOUT_INFLATER_SERVICE);

        getActionBar().setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM);

        View actionBar = LayoutInflater.from(this).inflate(
                R.layout.browser_settings_actionbar, null);

        getActionBar().setCustomView(actionBar,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

        mActionNewEvent = (TextView) actionBar.findViewById(R.id.action_new_event_text);
        mBackActionView = actionBar.findViewById(R.id.action_cancel);
        mBackActionView.setVisibility(View.INVISIBLE);
        mBackActionView.setOnClickListener(this);
        mBackBtn = (TextView) actionBar.findViewById(R.id.action_back_textview);
        mBackBtn.setVisibility(View.VISIBLE);
        mDoneActionView = actionBar.findViewById(R.id.action_done);
        mDoneActionView.setOnClickListener(this);
        mActionDown = (TextView) actionBar.findViewById(R.id.edit_event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_TAB,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        setResult(RESULT_OK, i);
        finish();
        overridePendingTransition(R.anim.activity_slide_do_nothing,
                R.anim.slide_down_out);
    }

    @Override
    public void openInNewTab(String... urls) {
        Intent i = new Intent();
        i.putExtra(EXTRA_OPEN_ALL, urls);
        setResult(RESULT_OK, i);
        finish();
        overridePendingTransition(R.anim.activity_slide_do_nothing,
                R.anim.slide_down_out);

    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public void exitAnim() {
        overridePendingTransition(
                R.anim.pop_up_in,
                R.anim.activity_close_enter_in_call);

    }

    @Override
    public void openSnapshot(long id) {
        Intent i = new Intent();
        i.putExtra(EXTRA_OPEN_SNAPSHOT, id);
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.combined, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.preferences_menu_id) {
            String url = getIntent().getStringExtra(EXTRA_CURRENT_URL);
            Intent intent = new Intent(this, BrowserPreferencesPage.class);
            intent.putExtra(BrowserPreferencesPage.CURRENT_PAGE, url);
            startActivityForResult(intent, Controller.PREFERENCES_PAGE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setFolderChanegListener(OnChangeFolderListener listener) {
        mListener = listener;
    }

    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost. It relies on a
     * trick. Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show. This is not sufficient for switching
     * between pages. So instead we make the content part of the tab host 0dp
     * high (it is not shown) and the TabsAdapter supplies its own dummy view to
     * show as the tab content. It listens to changes in tabs, and takes care of
     * switch to the correct page in the ViewPager whenever the selected tab
     * changes.
     */
    public static class TabsAdapter extends FragmentPagerAdapter
            implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
            }
        }

        public TabsAdapter(Activity activity, ViewPager pager) {
            super(activity.getFragmentManager());
            mContext = activity;
            mActionBar = activity.getActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            tab.setTag(info);
            tab.setTabListener(this);
            mTabs.add(info);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onTabSelected(android.app.ActionBar.Tab tab,
                FragmentTransaction ft) {
            Object tag = tab.getTag();
            for (int i = 0; i < mTabs.size(); i++) {
                if (mTabs.get(i) == tag) {
                    mViewPager.setCurrentItem(i);
                }
            }
        }

        @Override
        public void onTabUnselected(android.app.ActionBar.Tab tab,
                FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(android.app.ActionBar.Tab tab,
                FragmentTransaction ft) {
        }
    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    private void startFragment(int type, Bundle bundle, boolean isFirstTime) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        switch (type) {
            case 0:
                mCurrentFragmentTag = FRAGMENT_HISTORY_TAG;
                BrowserHistoryPage history = new BrowserHistoryPage();
                history.setArguments(bundle);
                if (!isFirstTime) {
                    ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);
                }
                ft.replace(R.id.main_frame, history, FRAGMENT_HISTORY_TAG);
                break;
            case 1:
                mCurrentFragmentTag = FRAGMENT_LOCAL_BOOKMARK_TAG;
                BrowserBookmarksAccountPage accountPage1 = new BrowserBookmarksAccountPage();
                Bundle bundle1 = new Bundle();
                bundle1.putString(BrowserBookmarksAccountPage.ACCOUNT_NAME, null);
                bundle1.putLong(FRAGMENT_RELOAD_ID, mReloadID);
                accountPage1.setArguments(bundle1);
                if (!isFirstTime) {
                    if (isChange) {
                        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);
                    } else {
                        ft.setCustomAnimations(R.anim.in_from_left, R.anim.out_to_right);
                    }
                }
                ft.replace(R.id.main_frame, accountPage1, FRAGMENT_LOCAL_BOOKMARK_TAG);
                break;
            default:
                break;
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    public String getFolderName() {
        return mFolderName;
    }

    @Override
    public long getFolderID() {
        return mCurrentID;
    }

    @Override
    public void setCurrentID(long parent) {
        mParentID = parent;
        mCurrentID = parent;
    }

    @Override
    public void onClick(View view) {
        if (mDoneActionView == view) {
            finish();
            overridePendingTransition(R.anim.activity_slide_do_nothing,
                    R.anim.slide_down_out);
        } else if (mBackActionView == view) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment fregment = getFragmentManager().findFragmentByTag(FRAGMENT_HISTORY_TAG);
            ft.hide(fregment);
            onBackPressed();
            mDoneActionView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onReloadFragment(int type, long id, String title) {
        isChange = true;
        mReloadID = id;
        backNameList.add(title);
        mFolderName = title;
        mActionNewEvent.setText(title);
        mBackActionView.setVisibility(View.VISIBLE);
        startFragment(type, mBundle, false);
    }

    @Override
    public void setParentID(long parent) {
        mReloadID = parent;
        mFolderState = false;
    }

    @Override
    public long getParentID() {
        return mParentID;
    }

    @Override
    public void changeActionBar(boolean isEditState) {
        mEditState = isEditState;
        if (isEditState) {
            hideButton(mBackActionView);
            hideButton(mDoneActionView);
        } else {
            if (mDoneActionView != null && mDoneActionView.getVisibility() == View.VISIBLE) {
                return;
            }
            showButton(mBackActionView);
            showButton(mDoneActionView);
        }
    }

    private void showButton(View v) {
        if (mReloadID == 0) {
            return;
        }
        final View view = v;
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0);
        view.animate().setDuration(150).setInterpolator(new DecelerateInterpolator())
                .setListener(null).alpha(1f);
    }

    private void hideButton(View v) {
        if (mReloadID == 0) {
            return;
        }
        final View view = v;
        view.animate().setDuration(150).setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.INVISIBLE);
                    }
                })
                .alpha(0);
    }

    @Override
    public void setAccountName(String name) {
        mAccountName = name;
    }

    private void backFolderName() {
        if (backNameList.size() == 1) {
            backNameList.clear();
            if (mAccountName == null) {
                mActionNewEvent.setText
                        (R.string.tab_bookmarks);
            } else {
                mActionNewEvent.setText
                        (mAccountName);
            }

        } else {
            int count = backNameList.size() - 2;
            if (count >= 0) {
                mParentFolderName = backNameList.get(count);
                mActionNewEvent.setText(mParentFolderName);
                backNameList.remove(count + 1);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestFullScreen();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        requestFullScreen();
    }

    public void requestFullScreen() {
        Window win = getWindow();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            win.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onBackPressed() {
        if (getItemhasOpen()) {
            return;
        }
        if (mEditState) {
            BookmarkEditListView.isOnTouch = false;
            mListener.cancelEditState();
            return;
        }
        if (mListener == null) {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_slide_do_nothing,
                    R.anim.slide_down_out);
        }
        if (mListener != null && mListener.onChangeFolderAdapter()) {
            if (!mListener.isReload() || mReloadID == 0) {
                isChange = true;
                finish();
                overridePendingTransition(R.anim.activity_slide_do_nothing,
                        R.anim.slide_down_out);
            } else { // some foler tree node.
                isChange = false;
                if (mListener.isReload() && mReloadID != 0) {
                    backFolderName();
                    if (mReloadID == 1) {
                        mBackActionView.setVisibility(View.INVISIBLE);
                    } else {
                        mBackActionView.setVisibility(View.VISIBLE);
                    }
                    mFolderName = mActionNewEvent.getText().toString();
                    setCurrentID(mReloadID);
                    startFragment(mListener.getCurrentType(), mBundle, false);
                }
            }
            mListener = null;
        }
    }

    private FragmentManager mFragmentManager;

    private boolean getItemhasOpen() {
        mFragmentManager = getFragmentManager();
        final Fragment fragment = mFragmentManager.findFragmentByTag(mCurrentFragmentTag);
        if (isValidFragment(fragment)) {
            if (mCurrentFragmentTag == FRAGMENT_HISTORY_TAG) {
                return ((BrowserHistoryPage) fragment).hasItemOpen();
            } else {
                return ((BrowserBookmarksAccountPage) fragment).hasItemOpen();
            }
        }
        return false;
    }

    private static boolean isValidFragment(Fragment in) {
        return !(in == null || in.getActivity() == null || in.getView() == null);
    }
}
