package samirrolemberg.com.br.maite.adapters;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.List;

import samirrolemberg.com.br.maite.PrincipalActivity;
import samirrolemberg.com.br.maite.R;
import samirrolemberg.com.br.maite.connection.DatabaseManager;
import samirrolemberg.com.br.maite.daos.DAOAnexo;
import samirrolemberg.com.br.maite.daos.DAOConteudo;
import samirrolemberg.com.br.maite.fragments.WebViewNoticiaFragment;
import samirrolemberg.com.br.maite.holders.NoticiasViewHolder;
import samirrolemberg.com.br.maite.holders.ViewHolder;
import samirrolemberg.com.br.maite.models.Anexo;
import samirrolemberg.com.br.maite.models.Conteudo;
import samirrolemberg.com.br.maite.models.Descricao;
import samirrolemberg.com.br.maite.models.Feed;
import samirrolemberg.com.br.maite.models.Post;
import samirrolemberg.com.br.maite.tools.U;

/**
 * Created by Samir on 11/10/2014.
 */
public class NoticiasGridViewAdapter extends ArrayAdapter<Post> implements Serializable{
    private List<Post> lista;
    private Context context;

    private NoticiasViewHolder holder;

    private final static String LOGTAG = "NOTICIATAG";

    public NoticiasGridViewAdapter(Context context, List<Post> lista) {
        super(context, R.layout.cards);
        this.lista = lista;
        this.context = context;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Post getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cards, null);
            holder = new NoticiasViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (NoticiasViewHolder) convertView.getTag();
        }

        holder.getTitulo().setText(lista.get(position).getTitulo());
        holder.getAutor().setText(lista.get(position).getAutor());
        holder.getData().setText(U.date_mask(lista.get(position).getData_publicacao(), getContext()));
        //aqui obter o anexo
        final DAOAnexo daoAnexo = new DAOAnexo(getContext());
        final DAOConteudo daoConteudo = new DAOConteudo(getContext());

        List<Anexo> anexos = daoAnexo.listarTudo(getItem(position));

        if (anexos.size()>0){//se tem anexo.
            Picasso.with(getContext())
                    .load(anexos.get(0).getUrl())
                    .into(holder.getFundo());
        }else{
            //verifica se há imagem no conteúdo
            Post post = lista.get(position);
            String img = "";
            if (post!=null){
                Log.i(LOGTAG,"POST NÃO NULO");
                Conteudo conteudo = daoConteudo.buscar(lista.get(position));
                if (conteudo!=null){
                    Log.i(LOGTAG,"CONTENT NÃO NULO");
                        Document doc = Jsoup.parse(conteudo.getValor());
                        Elements content = doc.getElementsByTag("img");
                        Log.i(LOGTAG,"GET ELEMENT");
                        if (content!=null){
                            Log.i(LOGTAG,"ELEMENT NAO NULO: content: "+content.toString());
                            if (content.size()>0){
                                img = content.get(0).attributes().get("src").toString();
                                Log.i(LOGTAG,"GET SRC "+img);
                                Anexo anexo = new Anexo.Builder()
                                        .tamanho(0)
                                        .tipo("image/"+img.substring(img.length()-3,img.length()))
                                        .url(img)
                                        .acesso(1)
                                        .build();
                                daoAnexo.inserir(anexo, lista.get(position).getIdPost());
                                Log.i(LOGTAG, "insert anexo");
                            }else{
                                Log.i(LOGTAG, "NO CONTENT");
                            }
                        }
                }else
                if (post.getDescricao()!=null){
                    if (!img.isEmpty()){//se temalgo, já foi adiconado
                        if (post.getDescricao()!=null){
                            Descricao descricao = post.getDescricao();
                            Document doc = Jsoup.parse(descricao.getValor());
                            Elements content = doc.getElementsByTag("img");
                            if (content!=null){
                                img = content.get(0).attributes().get("src").toString();
                                Anexo anexo = new Anexo.Builder()
                                        .post(lista.get(position))
                                        .tamanho(0)
                                        .tipo("image/"+img.substring(img.length()-3,img.length()))
                                        .url(img)
                                        .acesso(1)
                                        .build();
                                daoAnexo.inserir(anexo, lista.get(position).getIdPost());
                            }
                        }
                    }
                }
            }
            if (img.isEmpty()){
                holder.getFundo().setBackgroundColor(getContext().getResources().getColor(android.R.color.darker_gray));
                Picasso.with(getContext())
                        .load(R.drawable.overloadr)
                        .into(holder.getFundo());
            }else{
                Picasso.with(getContext())
                        .load(img)
                        .into(holder.getFundo());
            }
        }
        DatabaseManager.getInstance().closeDatabase();
        DatabaseManager.getInstance().closeDatabase();
        acaoFrame(position, holder.getFrame());
        return convertView;
    }

    private void acaoFrame(final int position, final View frame){
        frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Bounce).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //FragmentTransaction transaction = PrincipalActivity.fragmentManager.beginTransaction();
                        //transaction.replace(R.id.container, WebViewNoticiaFragment.newInstance(55)).commit();
                        Toast.makeText(getContext(),lista.get(position).getTitulo(),Toast.LENGTH_LONG).show();
                        PrincipalActivity.change(PrincipalActivity.FRAGMENT_FLAG.WEB_VIEW_NOTICIA);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).duration(1 * 500).playOn(frame);
            }
        });
    }
}
