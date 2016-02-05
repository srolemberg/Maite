package samirrolemberg.com.br.maite.holders;

import android.graphics.Typeface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import samirrolemberg.com.br.maite.R;
import samirrolemberg.com.br.maite.fonts.Fontes;

/**
 * Created by Samir on 11/10/2014.
 */
public class ViewHolder {

    private FrameLayout frame;
    private TextView titulo;
    private TextView autor;
    private TextView data;
    private ImageView fundo;
    //private SwipeRefreshLayout swipe;

    public ViewHolder(View view){
        frame   = (FrameLayout)         view.findViewById(R.id.card_frame);
        fundo   = (ImageView)           view.findViewById(R.id.card_imagem_frame);
        titulo  = (TextView)            view.findViewById(R.id.card_frame_titulo);
        titulo.setTypeface(Typeface.createFromAsset(view.getContext().getAssets(), Fontes.Tipo.BOLD.toString()));
        autor   = (TextView)            view.findViewById(R.id.card_frame_autor);
        autor.setTypeface(Typeface.createFromAsset(view.getContext().getAssets(), Fontes.Tipo.REGULAR.toString()));
        data    = (TextView)            view.findViewById(R.id.card_frame_data);
        data.setTypeface(Typeface.createFromAsset(view.getContext().getAssets(), Fontes.Tipo.REGULAR.toString()));
    }


    public FrameLayout getFrame() {
        return frame;
    }

    public TextView getTitulo() {
        return titulo;
    }

    public TextView getAutor() {
        return autor;
    }

    public TextView getData() {
        return data;
    }

    public ImageView getFundo() {
        return fundo;
    }

   //public SwipeRefreshLayout getSwipe() {
  //      return swipe;
    //}

}
