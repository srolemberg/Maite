package samirrolemberg.com.br.maite.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;

import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;

import samirrolemberg.com.br.maite.PrincipalActivity;
import samirrolemberg.com.br.maite.R;
import samirrolemberg.com.br.maite.adapters.GridViewAdapter;
import samirrolemberg.com.br.maite.adapters.NoticiasGridViewAdapter;
import samirrolemberg.com.br.maite.connection.DatabaseManager;
import samirrolemberg.com.br.maite.daos.DAOFeed;
import samirrolemberg.com.br.maite.daos.DAOPost;
import samirrolemberg.com.br.maite.delegate.AtualizarFeedDelegate;
import samirrolemberg.com.br.maite.delegate.CarregarFeedDelegate;
import samirrolemberg.com.br.maite.delegate.SalvarFeedDelegate;
import samirrolemberg.com.br.maite.models.Feed;
import samirrolemberg.com.br.maite.models.Post;
import samirrolemberg.com.br.maite.tasks.AtualizarFeedTask;
import samirrolemberg.com.br.maite.tasks.CarregarFeedTask;
import samirrolemberg.com.br.maite.tasks.SalvarFeedTask;
import samirrolemberg.com.br.maite.tools.Configuracoes;

/**
 * Created by Samir on 06/10/2014.
 */
/**
 * A placeholder fragment containing a simple view.
 */
public class GridViewFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "GridViewArgument";

    private List<String> lista;
    private ArrayAdapter adapter = null;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GridViewFragment newInstance(int sectionNumber) {
        GridViewFragment fragment = new GridViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public GridViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.i("DEST","ONCREATE");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i("DEST","onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState!=null){
            if (savedInstanceState.containsKey("GridViewAdapter")){
                adapter = (GridViewAdapter) savedInstanceState.get("GridViewAdapter");
                Log.i("DEST","GRID");
            }else{
                adapter = (NoticiasGridViewAdapter) savedInstanceState.get("NoticiasGridViewAdapter");
                Log.i("DEST","NOTICIA");
            }
        }else Log.i("DEST","null");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState = new Bundle();
        Log.i("DEST","SAVE");
        if (adapter instanceof GridViewAdapter){
            outState.putSerializable("GridViewAdapter",(GridViewAdapter)adapter);
            Log.i("DEST", "SAVE gridd");
        }else{
            outState.putSerializable("NoticiasGridViewAdapter",(NoticiasGridViewAdapter)adapter);
            Log.i("DEST", "SAVE noticia");
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.grid_card_fragment, container, false);
        final SwipeRefreshLayout swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout_card_grid);
        final GridView gridView = (GridView) swipe.findViewById(R.id.gridviewcardstring);

        final long idFeed = Configuracoes.getLong(getActivity(), Configuracoes.Flags.CRIA_DB);

        if (adapter==null){
            ScaleInAnimationAdapter animation;

            if (idFeed>0){
                adapter = loadContent(getActivity(), idFeed);
            }else{
                dados();
                adapter = new GridViewAdapter(getActivity(), lista);
            }



            animation = new ScaleInAnimationAdapter(adapter);
            configSwipe(swipe, adapter);

            adapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    final long idFeed = Configuracoes.getLong(getActivity(), Configuracoes.Flags.CRIA_DB);

                    NoticiasGridViewAdapter array = loadContent(getActivity(), idFeed);
                    ScaleInAnimationAdapter anima = new ScaleInAnimationAdapter(array);
                    configSwipe(swipe, array);
                    anima.setAbsListView(gridView);
                    gridView.setAdapter(anima);
                    gridView.deferNotifyDataSetChanged();
                }
            });

            animation.setAbsListView(gridView);
            gridView.setAdapter(animation);

        }else{
            gridView.setAdapter(adapter);
        }
        //setRetainInstance(true);
        return rootView;
    }
    private NoticiasGridViewAdapter loadContent(Context context, long idFeed){
        DAOPost daoPost = new DAOPost(context);
        Feed feed = new Feed.Builder().idFeed(idFeed).build();
        List<Post> posts = daoPost.listarTudo(feed);

        NoticiasGridViewAdapter adapter = new NoticiasGridViewAdapter(getActivity(), posts);
        DatabaseManager.getInstance().closeDatabase();

        return adapter;
    }

    private void configSwipe(final SwipeRefreshLayout swipe, final ArrayAdapter adapter){
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getActivity(),"Atualizando", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe.setRefreshing(true);//true = indeterminado (ignora os segundos)
                        final long idFeed = Configuracoes.getLong(getActivity(), Configuracoes.Flags.CRIA_DB);
                        if (idFeed!=0){//feed existe, atualiza
                            DAOFeed daoFeed = new DAOFeed(getActivity());
                            Feed feed = daoFeed.buscar(idFeed);
                            DatabaseManager.getInstance().closeDatabase();
                            AtualizarFeedTask feedTask = new AtualizarFeedTask(getActivity(), feed, new AtualizarFeedDelegate() {
                                @Override
                                public void retorno(Feed retorno) {
                                    adapter.notifyDataSetChanged();
                                    swipe.setRefreshing(false);//true = indeterminado (ignora os segundos)
                                }
                            });
                            String[] arg ={getActivity().getString(R.string.url_feed_noticias)};
                            feedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arg);
                        }else{//feed não existe, cria
                            CarregarFeedTask task = new CarregarFeedTask(getActivity(), new CarregarFeedDelegate() {
                                @Override
                                public void retorno(Feed feed) {
                                    DAOFeed daoFeed = new DAOFeed(getActivity());
                                    if (feed!=null){
                                        if (idFeed==0){//se não existe
                                            //cria a db do feed
                                            long id = daoFeed.inserir(feed);
                                            Configuracoes.setLong(getActivity(), Configuracoes.Flags.CRIA_DB, id);
                                            adapter.setNotifyOnChange(true);
                                            SalvarFeedTask salvarFeedTask = new SalvarFeedTask(getActivity(), feed, id, new SalvarFeedDelegate() {
                                                @Override
                                                public void retorno(Feed feed) {
                                                    adapter.notifyDataSetChanged();
                                                    swipe.setRefreshing(false);//true = indeterminado (ignora os segundos)
                                                    Toast.makeText(getActivity(),"Atualizado", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            salvarFeedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                                        }
                                    }
                                    DatabaseManager.getInstance().closeDatabase();
                                }
                            });
                            String[] arg ={getActivity().getString(R.string.url_feed_noticias)};
                            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arg);
                        }


                    }
                }, 1 * 1000);//tempo de espera para o termino da atualização (1sec [1*1000ms])
            }
        });
        swipe.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((PrincipalActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void dados(){
        lista = new ArrayList<String>();
        lista.add(null);
    }
}
