package samirrolemberg.com.br.maite.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Samir on 11/10/2014.
 */
public class Configuracoes {

    private static String SHARED_TAG = "configuracoes";

    public enum Flags{
        CRIA_DB("cria_db");

        private final String string;

        private Flags(String string){
            this.string = string;
        }
        public boolean equalsName(String otherName){
            return (otherName == null)? false:this.string.equals(otherName);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

    public static void setLong(Context context, Flags flag, Long value){
        SharedPreferences sp = context.getSharedPreferences(SHARED_TAG,context.MODE_PRIVATE);
        sp.edit().putLong(flag.CRIA_DB.toString(),value).commit();
    }

    public static Long getLong(Context context, Flags flag){
        SharedPreferences sp = context.getSharedPreferences(SHARED_TAG, context.MODE_PRIVATE);
        return sp.getLong(flag.CRIA_DB.toString(),0);
    }
}
