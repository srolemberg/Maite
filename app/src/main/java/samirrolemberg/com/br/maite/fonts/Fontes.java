package samirrolemberg.com.br.maite.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.TextView;

import java.util.Hashtable;

/**
 * Created by Samir on 08/10/2014.
 */
public class Fontes {
    private static final String TAG = "Typefaces";

    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    //private static String assetPath = "dinprobold.ttf";


    public enum Tipo{
        BOLD("dinprobold.ttf"),
        REGULAR("dinproregular.ttf"),
        THIN1("pfdindisplayprothin0.ttf");

        private final String string;

        private Tipo(String string){
            this.string = string;
        }
        public boolean equalsName(String otherName){
            return (otherName == null)? false:this.string.equals(otherName);
        }

        public String toString(){
            return string;
        }
    }

    private static Typeface get(Context c, Fontes.Tipo assetPath) {
        synchronized (cache) {
            if (!cache.containsKey(assetPath.toString())) {
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(), assetPath.toString());
                    cache.put(assetPath.toString(), t);
                } catch (Exception e) {
                    Log.e(TAG, "Could not get typeface '" + assetPath+ "' because " + e.getMessage());
                    return null;
                }
            }
            return cache.get(assetPath);
        }
    }

    public static TextView checkTypeFace(Context somecontext, TextView someTextView, Fontes.Tipo assetPath){
        if(someTextView.getTypeface() !=null && !someTextView.getTypeface().equals(Fontes.get(somecontext, assetPath))){
            someTextView.setTypeface(Fontes.get(somecontext, assetPath));
            Log.d("FONTES", assetPath+"__FEITO");
        }
        return someTextView;
    }
}