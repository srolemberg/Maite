package samirrolemberg.com.br.maite.tasks;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import samirrolemberg.com.br.maite.R;
import samirrolemberg.com.br.maite.connection.DatabaseManager;
import samirrolemberg.com.br.maite.daos.DAOAnexo;
import samirrolemberg.com.br.maite.daos.DAOCategoria;
import samirrolemberg.com.br.maite.daos.DAOConteudo;
import samirrolemberg.com.br.maite.daos.DAODescricao;
import samirrolemberg.com.br.maite.daos.DAOFeed;
import samirrolemberg.com.br.maite.daos.DAOImagem;
import samirrolemberg.com.br.maite.daos.DAOPost;
import samirrolemberg.com.br.maite.delegate.AtualizarFeedDelegate;
import samirrolemberg.com.br.maite.models.Anexo;
import samirrolemberg.com.br.maite.models.Categoria;
import samirrolemberg.com.br.maite.models.Conteudo;
import samirrolemberg.com.br.maite.models.Descricao;
import samirrolemberg.com.br.maite.models.ExceptionMessage;
import samirrolemberg.com.br.maite.models.Feed;
import samirrolemberg.com.br.maite.models.Imagem;
import samirrolemberg.com.br.maite.models.Post;
import samirrolemberg.com.br.maite.models.SimpleFeed;


public class AtualizarFeedTask extends AsyncTask<String, Integer, Feed> {

    private final Context context;

    private SyndFeed syndFeed;
    private ExceptionMessage e;
    private int id = 0;

    private Feed feed;

    private int estimativa = 0;
    private int atual = 0;

    private NotificationManager mNotifyManager = null;
    private NotificationCompat.Builder mBuilder = null;

    private DAOFeed daoFeed = null;
    private DAOAnexo daoAnexo = null;
    private DAOCategoria daoCategoria = null;
    private DAOConteudo daoConteudo = null;
    private DAODescricao daoDescricao = null;
    private DAOImagem daoImagem = null;
    private DAOPost daoPost = null;

    private AtualizarFeedDelegate delegate;

    public AtualizarFeedTask(Context context, Feed feed, AtualizarFeedDelegate delegate){
        this.context = context;
        this.id = 30;//colocar parametero
        this.feed = feed;
        this.delegate = delegate;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Atualizando "+feed.getTitulo())
                .setContentText("Sincronizando o Feed.")
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher);
        //estimativa = estimativaDosFor()*2;
    }
    @Override
    protected Feed doInBackground(String... arg) {
        try {
            mBuilder.setProgress(0, 0, true);
            mNotifyManager.notify(id, mBuilder.build());
            URL feedUrl = new URL(arg[0]);
            SyndFeedInput input = new SyndFeedInput();
            this.syndFeed = input.build(new InputStreamReader(feedUrl.openStream()));
            if (feed != null) {
                mBuilder.setProgress(0, 0, false);
                mBuilder.setContentText("Verificando Entradas Antigas.");
                mNotifyManager.notify(id, mBuilder.build());
                return SimpleFeed.consumir(syndFeed, arg[0]);
            }
        } catch (MalformedURLException e) {//problema ao acessar url, verifique a digitação! protocolo nao encontrado.
            Log.e("ADDFEED", "MalformedURLException");
            Log.e("ADDFEED", e.getMessage(),e);
            Log.e("ADDFEED", e.getLocalizedMessage(),e);
            this.e = new ExceptionMessage(e);
        } catch (IllegalArgumentException e) {
            Log.e("ADDFEED", "IllegalArgumentException");
            Log.e("ADDFEED", e.getMessage(),e);
            Log.e("ADDFEED", e.getLocalizedMessage(),e);
            this.e = new ExceptionMessage(e);
        } catch (FeedException e) {//XMl invpalido - nenhum elemento encontrado
            Log.e("ADDFEED", "FeedException");
            Log.e("ADDFEED", e.getMessage(),e);
            Log.e("ADDFEED", e.getLocalizedMessage(),e);
            this.e = new ExceptionMessage(e);
        } catch (IOException e) {//arquivo n�o encontrado
            Log.e("ADDFEED", "IOException");
            Log.e("ADDFEED", e.getMessage(),e);
            Log.e("ADDFEED", e.getLocalizedMessage(),e);
            this.e = new ExceptionMessage(e);
        } catch (Exception e) {
            Log.e("ADDFEED", "Exception");
            Log.e("ADDFEED", e.getMessage(),e);
            Log.e("ADDFEED", e.getLocalizedMessage(),e);
            this.e = new ExceptionMessage(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(final Feed result) {
        super.onPostExecute(result);
        if (result!=null) {
            daoFeed = new DAOFeed(context);
            daoAnexo = new DAOAnexo(context);
            daoCategoria = new DAOCategoria(context);
            daoConteudo = new DAOConteudo(context);
            daoDescricao = new DAODescricao(context);
            daoImagem = new DAOImagem(context);
            daoPost = new DAOPost(context);

            estimativa = estimativaDosFor(result);

            //verifica se o build do feed é igual ou recente
            if (result.getData_publicacao()==null) {
                //quando a data de publicação vier nula, um teste e feito para tornar o feed sempre atualizavel.
                doIT(result);
            }else if (feed.getData_publicacao()==null || feed.getData_publicacao().before(result.getData_publicacao())) {
                doIT(result);
            }else{
                //não é necessário atualizar
                Toast.makeText(getContext(), "Não é necessário atualizar.", Toast.LENGTH_SHORT).show();
            }
            DatabaseManager.getInstance().closeDatabase();
            DatabaseManager.getInstance().closeDatabase();
            DatabaseManager.getInstance().closeDatabase();
            DatabaseManager.getInstance().closeDatabase();
            DatabaseManager.getInstance().closeDatabase();
            DatabaseManager.getInstance().closeDatabase();
            DatabaseManager.getInstance().closeDatabase();
        }else{
            Toast.makeText(getContext(), "Não encontrado: "+e.getThrowable().getMessage(), Toast.LENGTH_SHORT).show();
        }
        mBuilder.setProgress(0, 0, false);
        mBuilder.setOngoing(false);
        mBuilder.setContentText("Feed Atualizado.");
        mNotifyManager.notify(id, mBuilder.build());

        delegate.retorno(result);
    }
    private void doIT(Feed result){
        //quando a data de publicação vier nula, um teste e feito para tornar o feed sempre atualizavel.
        //TODO: VERIFICAR COMO REMOVER ALGUNS DADOS QUE DEIXARAM DE VIR (COMO CATEGORIAS)
        //se a data do ultimo build está antes da nova data
        //atualiza os dados do feed
        daoFeed.atualiza(result, feed.getIdFeed());//não atualiza o objeto feed(Aux) que já está aqui | quando a tarefa terminar elel carregará no devido lugar
        //LinkedList<Long> acessoListaFeed = new LinkedList<Long>();//lista de ids de post para remover da lista
        if (result.getCategorias()!=null) {//se tem categorias
            for (Categoria categoria : result.getCategorias()) {
                long idCategoria = daoCategoria.existe(categoria, feed);//verifica se a categoria do feed existe na base
                if (idCategoria>0) {//se a categoria existe atualiza pois pode ter url
                    daoCategoria.atualiza(categoria, idCategoria);
                }else{//se não existe insere a categira nova
                    daoCategoria.inserir(categoria, feed);
                    daoCategoria.atualizaAcesso(feed, 1);//aqui o acesso pode ser direto. não influenciana experiencia
                }
                atual++;
                mBuilder.setProgress(estimativa, atual, false);
                mNotifyManager.notify(id, mBuilder.build());
            }
        }
        if (result.getImagem()!=null) {
            Imagem imagem = result.getImagem();
            long idImagem = daoImagem.existe(imagem, feed);//verifica de a imagem existe
            if (idImagem>0) {
                daoImagem.atualiza(result.getImagem(), feed.getIdFeed());
            }else{
                daoImagem.inserir(imagem, feed.getIdFeed());
                daoImagem.atualizaAcesso(feed, 1);
            }
        }
        if (result.getPosts()!=null) {//se tem post no novo feed
            //pega as novas entradas e verifica se há alguém com data de atualização
            //se há, verifica se o link existe na base.
            //se existe pega aquele post e atualiza TUDO dele
            LinkedList<Long> acessoLista = new LinkedList<Long>();//lista de ids de post para remover da lista
            for (int i = 0; i < result.getPosts().size(); i++) {
                atual++;
                mBuilder.setProgress(estimativa, atual, false);
                mNotifyManager.notify(id, mBuilder.build());

                Post post = result.getPosts().get(i);
                Post idPost = new Post.Builder().idPost(daoPost.existe(post)).build();
                if (idPost.getIdPost()>0) {//se o post existe atualiza
                    //idsp.add(i);//remove este no futuro;
                    daoPost.atualiza(post, idPost.getIdPost());//atualiza os novos dados do post
                    if (post.getAnexos()!=null) {//se tem anexo no novo post a ser atualizado
                        for (Anexo anexo : post.getAnexos()) {//para cada novo anexo verifica se ele existe na base
                            long idAnexo = daoAnexo.existe(anexo, idPost.getIdPost());
                            if (idAnexo>0) {//se existe este anexo atualiza ele
                                daoAnexo.atualiza(anexo, idAnexo);
                            }else{//veio mais um anexo na atualização. insere
                                daoAnexo.inserir(anexo, idPost.getIdPost());
                                acessoLista.add(idPost.getIdPost());
                            }
                            atual++;
                            mBuilder.setProgress(estimativa, atual, false);
                            mNotifyManager.notify(id, mBuilder.build());
                        }
                    }
                    if (post.getCategorias()!=null) {//se tem categorias
                        //adiciona novas categorias ao post se ela não existir antes.
                        for (Categoria categoria : post.getCategorias()) {
                            long idCategoria = daoCategoria.existe(categoria, idPost);
                            if (idCategoria>0) {//atualiza pois algumas categorias podem ter URL. Mas o nome ainda ser igual.
                                daoCategoria.atualiza(categoria, idCategoria);
                            }else{
                                daoCategoria.inserir(categoria, idPost);
                                acessoLista.add(idPost.getIdPost());
                            }
                            atual++;
                            mBuilder.setProgress(estimativa, atual, false);
                            mNotifyManager.notify(id, mBuilder.build());
                        }

                    }
                    if (post.getConteudos()!=null) {//set tem conteúdo
                        //adiciona novo contudoao postse ele nao existir
                        for (Conteudo conteudo : post.getConteudos()) {
                            //o conteudo é baseado em valor.
                            //se o feed retornar a menor alteração o conteúdo do post se existir será atualizado
                            //isso talvez contorne o caso do feed talvez não mandar uma data de atualização no post
                            //TODO: VERIFICAR ISSO POIS EXISTE O TESTE DE DATA E ELE NUNCA ENTRARIA AQUI
                            //TALVEZ A DATA NAO FAÇA SENTIDO SER VERIFICADA
                            long idConteudo = daoConteudo.existe(conteudo, idPost);
                            if (idConteudo>0) {
                                daoConteudo.atualiza(conteudo, idConteudo);
                            }else{
                                daoConteudo.inserir(conteudo, idPost.getIdPost());
                                acessoLista.add(idPost.getIdPost());
                            }
                            atual++;
                            mBuilder.setProgress(estimativa, atual, false);
                            mNotifyManager.notify(id, mBuilder.build());
                        }
                    }
                    if (post.getDescricao()!=null) {//se tem descrição
                        Descricao descricao = post.getDescricao();//armazena descrição para acesso fácil
                        long idDescricao = daoDescricao.existe(post.getDescricao(), idPost.getIdPost());//verifica existe comparando o valor
                        if (idDescricao>0) {//se existe
                            daoDescricao.atualiza(descricao, idDescricao);
                        }else{
                            daoDescricao.inserir(descricao, idPost.getIdPost());
                            acessoLista.add(idPost.getIdPost());
                        }
                    }
                }else{//se o post nao existe insere novo
                    //insere post e retorna o id
                    idPost = new Post.Builder().idPost(daoPost.inserir(post, feed.getIdFeed())).build();
                    if (post.getAnexos()!=null) {//se tem anexo no novo post a ser atualizado
                        for (Anexo anexo : post.getAnexos()) {//para cada novo anexo verifica se ele existe na base
                            daoAnexo.inserir(anexo, idPost.getIdPost());
                            acessoLista.add(idPost.getIdPost());

                            atual++;
                            mBuilder.setProgress(estimativa, atual, false);
                            mNotifyManager.notify(id, mBuilder.build());
                        }
                    }
                    if (post.getCategorias()!=null) {//se tem categorias
                        //adiciona novas categorias ao post se ela não existir antes.
                        for (Categoria categoria : post.getCategorias()) {
                            daoCategoria.inserir(categoria, idPost);
                            acessoLista.add(idPost.getIdPost());

                            atual++;
                            mBuilder.setProgress(estimativa, atual, false);
                            mNotifyManager.notify(id, mBuilder.build());
                        }

                    }
                    if (post.getConteudos()!=null) {//set tem conteúdo
                        //adiciona novo contudoao postse ele nao existir
                        for (Conteudo conteudo : post.getConteudos()) {
                            daoConteudo.inserir(conteudo, idPost.getIdPost());
                            acessoLista.add(idPost.getIdPost());

                            atual++;
                            mBuilder.setProgress(estimativa, atual, false);
                            mNotifyManager.notify(id, mBuilder.build());
                        }
                    }
                    if (post.getDescricao()!=null) {//se tem descrição
                        Descricao descricao = post.getDescricao();//armazena descrição para acesso fácil
                        daoDescricao.inserir(descricao, idPost.getIdPost());
                        acessoLista.add(idPost.getIdPost());

                        atual++;
                        mBuilder.setProgress(estimativa, atual, false);
                        mNotifyManager.notify(id, mBuilder.build());
                    }
                }
            }
            mBuilder.setProgress(0, 0, true);
            mBuilder.setContentText("Atualizando novas entradas.");
            mNotifyManager.notify(id, mBuilder.build());
            for (Long idDAO : acessoLista) {//dados do post que entraram como 0
                Post post = new Post.Builder().idPost(idDAO).build();
                daoAnexo.atualizaAcesso(post, 1);
                daoCategoria.atualizaAcesso(post, 1);
                daoConteudo.atualizaAcesso(post, 1);
                daoDescricao.atualizaAcesso(post, 1);
                daoPost.atualizaAcesso(post, 1);
            }
        }
        Toast.makeText(getContext(), feed.getTitulo()+" foi atualizado com sucesso.", Toast.LENGTH_SHORT).show();
    }
    protected Context getContext(){
        return this.context;
    }

    private int estimativaDosFor(Feed feed){
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

}
