package samirrolemberg.com.br.maite.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import samirrolemberg.com.br.maite.PrincipalActivity;
import samirrolemberg.com.br.maite.R;

/**
 * Created by Samir on 12/10/2014.
 */
public class SobreSiteFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "SobreSiteArgument";


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SobreSiteFragment newInstance(int sectionNumber) {
        SobreSiteFragment fragment = new SobreSiteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SobreSiteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.grid_card_fragment, container, false);
        setRetainInstance(true);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((PrincipalActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

}
