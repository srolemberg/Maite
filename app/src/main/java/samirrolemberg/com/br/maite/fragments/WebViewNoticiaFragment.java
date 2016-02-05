package samirrolemberg.com.br.maite.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import samirrolemberg.com.br.maite.PrincipalActivity;
import samirrolemberg.com.br.maite.R;

/**
 * Created by Samir on 12/10/2014.
 */
public class WebViewNoticiaFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "WebViewNoticiaArgument";


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WebViewNoticiaFragment newInstance(int sectionNumber) {
        WebViewNoticiaFragment fragment = new WebViewNoticiaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public WebViewNoticiaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_web_view_noticia, container, false);
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
