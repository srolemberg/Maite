package samirrolemberg.com.br.maite.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import samirrolemberg.com.br.maite.delegate.CarregarFeedDelegate;
import samirrolemberg.com.br.maite.models.ExceptionMessage;
import samirrolemberg.com.br.maite.models.Feed;
import samirrolemberg.com.br.maite.models.SimpleFeed;

/**
 * Created by Samir on 11/10/2014.
 */
public class CarregarFeedTask extends AsyncTask<String, Integer, Feed>{

    public final static String LOGTAG = "ADDFEED";
    private Context context;
    private CarregarFeedDelegate delegate;

    private SyndFeed feed;
    private ExceptionMessage e;


    public CarregarFeedTask(Context context, CarregarFeedDelegate delegate){
        super();
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected Feed doInBackground(String... arg) {
        try {
            URL feedUrl = new URL(arg[0]);
            SyndFeedInput input = new SyndFeedInput();
            this.feed = input.build(new InputStreamReader(feedUrl.openStream()));
            Log.d(LOGTAG,"Consumindo");
            if (feed != null) {
                Log.i(LOGTAG,"Retorno");
                return SimpleFeed.consumir(feed, arg[0]);
            }
            Log.i(LOGTAG,"Retorno nulo");
        } catch (MalformedURLException e) {//problema ao acessar url, verifique a digitaï¿½ï¿½o! protocolo nao encontrado.
            Log.e(LOGTAG, "MalformedURLException");
            Log.e(LOGTAG, e.getMessage(),e);
            Log.e(LOGTAG, e.getLocalizedMessage(),e);
            this.e = new ExceptionMessage(e);
        } catch (IllegalArgumentException e) {
            Log.e(LOGTAG, "IllegalArgumentException");
            Log.e(LOGTAG, e.getMessage(),e);
            Log.e(LOGTAG, e.getLocalizedMessage(),e);
            this.e = new ExceptionMessage(e);
        } catch (FeedException e) {//XMl invpalido - nenhum elemento encontrado
            Log.e(LOGTAG, "FeedException");
            Log.e(LOGTAG, e.getMessage(),e);
            Log.e(LOGTAG, e.getLocalizedMessage(),e);
            this.e = new ExceptionMessage(e);
        } catch (IOException e) {//arquivo nï¿½o encontrado
            Log.e(LOGTAG, "IOException");
            Log.e(LOGTAG, e.getMessage(),e);
            Log.e(LOGTAG, e.getLocalizedMessage(),e);
            this.e = new ExceptionMessage(e);
        } catch (Exception e) {
            Log.e(LOGTAG, "Exception");
            Log.e(LOGTAG, e.getMessage(),e);
            Log.e(LOGTAG, e.getLocalizedMessage(),e);
            this.e = new ExceptionMessage(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Feed feed) {
        super.onPostExecute(feed);
        delegate.retorno(feed);
    }

}
