package samirrolemberg.com.br.maite;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import samirrolemberg.com.br.maite.fragments.GridViewFragment;
import samirrolemberg.com.br.maite.fragments.SobreAppFragment;
import samirrolemberg.com.br.maite.fragments.SobreSiteFragment;
import samirrolemberg.com.br.maite.fragments.WebViewNoticiaFragment;
import samirrolemberg.com.br.maite.menu.NavigationDrawerFragment;


public class PrincipalActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private GridViewFragment gridViewFragment;
    private SobreSiteFragment sobreSiteFragment;
    private SobreAppFragment sobreAppFragment;
    //private WebViewNoticiaFragment webViewNoticiaFragment;

    static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        fragmentManager = getFragmentManager();

    }

    public static enum FRAGMENT_FLAG{
        GRID_VIEW_FRAGMENT,
        SOBRE_SITE,
        SOBRE_APP,
        WEB_VIEW_NOTICIA;
    }

    public static void change(FRAGMENT_FLAG flag){

        if (flag.equals(FRAGMENT_FLAG.WEB_VIEW_NOTICIA)){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            //transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

            Fragment fragment = WebViewNoticiaFragment.newInstance(7);
            transaction
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();

        switch (position){
            case 0:{
                fragmentManager.beginTransaction()
                        .replace(R.id.container, gridViewFragment.newInstance(position + 1))
                        .commit();
            }
            break;
            case 1:{
                fragmentManager.beginTransaction()
                        .replace(R.id.container, sobreSiteFragment.newInstance(position + 1))
                        .commit();
            }
            break;
            case 2:{
                fragmentManager.beginTransaction()
                        .replace(R.id.container, sobreAppFragment.newInstance(position + 1))
                        .commit();
            }
            break;
            default:{

            }
            break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_noticias);
                break;
            case 2:
                mTitle = getString(R.string.title_sobre_site);
                break;
            case 3:
                mTitle = getString(R.string.title_sobre_app);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.principal, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.grid_card_fragment, container, false);
//            return rootView;
//        }
//
//        @Override
//        public void onAttach(Activity activity) {
//            super.onAttach(activity);
//            ((PrincipalActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
//        }
//    }

}
