package samirrolemberg.com.br.maite.tasks;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

import samirrolemberg.com.br.maite.R;
import samirrolemberg.com.br.maite.connection.DatabaseManager;
import samirrolemberg.com.br.maite.daos.DAOAnexo;
import samirrolemberg.com.br.maite.daos.DAOCategoria;
import samirrolemberg.com.br.maite.daos.DAOConteudo;
import samirrolemberg.com.br.maite.daos.DAODescricao;
import samirrolemberg.com.br.maite.daos.DAOFeed;
import samirrolemberg.com.br.maite.daos.DAOImagem;
import samirrolemberg.com.br.maite.daos.DAOPost;
import samirrolemberg.com.br.maite.delegate.SalvarFeedDelegate;
import samirrolemberg.com.br.maite.models.Feed;
import samirrolemberg.com.br.maite.models.Post;

/**
 * Created by Samir on 11/10/2014.
 */
public class SalvarFeedTask extends AsyncTask<String, Integer, Feed> {
    public static final int id = 1550;
    public static final String LOGTAG = "1550";

    private NotificationManager mNotifyManager = null;
    private NotificationCompat.Builder mBuilder = null;
    private Feed feed = null;
    private int estimativa = 0;
    private long idFeed = 0;
    private List<Long> idsPost = null;
    private int atual = 0;

    private DAOFeed daoFeed = null;
    private DAOPost daoPost = null;
    private DAODescricao daoDescricao = null;
    private DAOConteudo daoConteudo = null;
    private DAOAnexo daoAnexo = null;
    private DAOCategoria daoCategoria = null;
    private DAOImagem daoImagem = null;

    private Context context;
    private SalvarFeedDelegate delegate;

    public SalvarFeedTask(Context context, Feed feed, long idFeed, SalvarFeedDelegate delegate) {
        super();
        this.context = context;
        this.feed = feed;
        this.idFeed = idFeed;
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        this.mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Adicionando "+feed.getTitulo())
                .setContentText("Adicionando novos registros.")
                .setTicker("Adicionando novos registros.")
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher);
        estimativa = estimativaDosFor()*2;
    }

    @Override
    protected Feed doInBackground(String... params) {
        addFeed();
        // When the loop is finished, updates the notification
        mBuilder.setContentText("Novo conteúdo adicionado.")
                .setProgress(0,0,false);
        mNotifyManager.notify(id, mBuilder.build());

        return null;
    }

    @Override
    protected void onPostExecute(Feed feed) {
        super.onPostExecute(feed);
        mBuilder.setProgress(0,0,false)
                .setOngoing(false);
        mNotifyManager.notify(id, mBuilder.build());
        delegate.retorno(feed);
    }

    private void atualiza(){
        Feed idf= new Feed.Builder().idFeed(idFeed).build();
        daoFeed.atualizaAcesso(idf, 1);

        atual+=	daoCategoria.atualizaAcesso(idf, 1);
        mBuilder.setProgress(estimativa, atual, false);
        mNotifyManager.notify(id, mBuilder.build());

        daoImagem.atualizaAcesso(idf, 1);
        for (Long idPost : idsPost) {
            Post post = new Post.Builder().idPost(idPost).build();

            atual+=	daoPost.atualizaAcesso(post, 1);

            mBuilder.setProgress(estimativa, atual, false);
            mNotifyManager.notify(id, mBuilder.build());

            atual+=	daoCategoria.atualizaAcesso(post, 1);
            mBuilder.setProgress(estimativa, atual, false);
            mNotifyManager.notify(id, mBuilder.build());

            daoDescricao.atualizaAcesso(post, 1);

            atual+=	daoConteudo.atualizaAcesso(post, 1);
            mBuilder.setProgress(estimativa, atual, false);
            mNotifyManager.notify(id, mBuilder.build());

            atual+=	daoAnexo.atualizaAcesso(post, 1);
            mBuilder.setProgress(estimativa, atual, false);
            mNotifyManager.notify(id, mBuilder.build());
        }
    }
    private int estimativaDosFor(){
        int i = 0;//pq pode adicionar apenas o feed sem nada (!)
        if (feed.getCategorias()!=null) {
            i += feed.getCategorias().size();
        }
        if (feed.getPosts()!=null) {
            i += feed.getPosts().size();
            for (Post post : feed.getPosts()) {
                if (post.getAnexos()!=null) {
                    i += post.getAnexos().size();
                }
                if (post.getCategorias()!=null) {
                    i += post.getCategorias().size();
                }
                if (post.getConteudos()!=null) {
                    i += post.getConteudos().size();
                }
            }
        }
        return i;
    }
    private void addFeed(){
        daoFeed = new DAOFeed(context);
        //long idFeed = daoFeed.inserir(feed);
        //int estimativa = estimativaDosFor();
        if (idFeed!=-1) {
            daoPost = new DAOPost(context);
            daoDescricao = new DAODescricao(context);
            daoConteudo = new DAOConteudo(context);
            daoAnexo = new DAOAnexo(context);
            daoCategoria = new DAOCategoria(context);
            daoImagem = new DAOImagem(context);

            if (feed.getCategorias()!=null) {
                //pega a categoria do feed
                for (int i = 0; i < feed.getCategorias().size(); i++) {
                    //objeto de Feed ainda nÃ£o tem id. Cria um objeto apenas com o id retornado do insert
                    daoCategoria.inserir(feed.getCategorias().get(i), (new Feed.Builder().idFeed(idFeed).build()));
                    atual ++;
                    mBuilder.setProgress(estimativa, atual, false);
                    mNotifyManager.notify(id, mBuilder.build());

                }
            }
            if (feed.getImagem()!=null) {
                //pega a imagem do feed
                daoImagem.inserir(feed.getImagem(), idFeed);
            }
            if (feed.getPosts()!=null) {
                idsPost = new ArrayList<Long>();
                for (int i = 0; i < feed.getPosts().size(); i++) {
                    atual ++;
                    mBuilder.setProgress(estimativa, atual, false);
                    mNotifyManager.notify(id, mBuilder.build());

                    long idPost = daoPost.inserir(feed.getPosts().get(i), idFeed);
                    idsPost.add(idPost);
                    //pega as categorias dos posts
                    if (feed.getPosts().get(i).getCategorias()!=null) {
                        //pega a categoria do feed
                        for (int j = 0; j < feed.getPosts().get(i).getCategorias().size(); j++) {
                            //objeto de Post ainda nÃ£o tem id. Cria um objeto apenas com o id retornado do insert
                            daoCategoria.inserir(feed.getPosts().get(i).getCategorias().get(j), (new Post.Builder().idPost(idPost).build()));
                            atual ++;
                            mBuilder.setProgress(estimativa, atual, false);
                            mNotifyManager.notify(id, mBuilder.build());

                        }

                    }
                    //pega os anexos do post
                    if (feed.getPosts().get(i).getAnexos()!=null) {
                        for (int j = 0; j < feed.getPosts().get(i).getAnexos().size(); j++) {
                            daoAnexo.inserir(feed.getPosts().get(i).getAnexos().get(j), idPost);
                            atual ++;
                            mBuilder.setProgress(estimativa, atual, false);
                            mNotifyManager.notify(id, mBuilder.build());
                        }
                    }
                    //para cada post tem uma ou mais descriÃ§Ãµes
                    if (feed.getPosts().get(i).getDescricao()!=null) {
                        daoDescricao.inserir(feed.getPosts().get(i).getDescricao(), idPost);
                    }
                    //para cada post tem uma ou mais conteudos
                    if (feed.getPosts().get(i).getConteudos()!=null) {
                        for (int j = 0; j < feed.getPosts().get(i).getConteudos().size(); j++) {
                            daoConteudo.inserir(feed.getPosts().get(i).getConteudos().get(j), idPost);
                            atual ++;
                            mBuilder.setProgress(estimativa, atual, false);
                            mNotifyManager.notify(id, mBuilder.build());
                        }
                    }

                }
            }
            atualiza();//atualiza flag de acesso
            DatabaseManager.getInstance().closeDatabase();
            DatabaseManager.getInstance().closeDatabase();
            DatabaseManager.getInstance().closeDatabase();
            DatabaseManager.getInstance().closeDatabase();
            DatabaseManager.getInstance().closeDatabase();
            DatabaseManager.getInstance().closeDatabase();
            DatabaseManager.getInstance().closeDatabase();
        }
        //TODO: COLOCAR UMA MUDANÃ‡A DE FLAG NO FEED PARA SER ACESSÃ�VEL.
        //daoFeed.DatabaseManager.getInstance().closeDatabase();
        //TODO: JOGAR O PROCESSO DE ADIÃ‡ÃƒO EM BACKGROUND NUMA NOTIFICAÃ‡ÃƒO.
    }

}
