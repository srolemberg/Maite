package samirrolemberg.com.br.maite.adapters;


import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

import samirrolemberg.com.br.maite.R;
import samirrolemberg.com.br.maite.holders.ViewHolder;
import samirrolemberg.com.br.maite.fonts.Fontes;

/**
 * Created by Samir on 06/10/2014.
 */
public class GridViewAdapter extends ArrayAdapter<String> implements Serializable{
    private List<String> lista;
    private Context context;

    private ViewHolder holder;

    public GridViewAdapter(Context context, List<String> lista) {
        super(context, R.layout.cards);
        this.lista = lista;
        this.context = context;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public String getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.cards, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        acaoFrame(position, holder);

        String text = "Puxe para Atualizar";
        holder.getTitulo().setText(text);
        holder.getAutor().setText("Samir Rolemberg");
        holder.getData().setText("11/10/2014");
        return convertView;
    }


    private void acaoFrame(final int position, final ViewHolder view){
        final FrameLayout frame = view.getFrame();

        frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), lista.get(position), Toast.LENGTH_SHORT).show();
                YoYo.with(Techniques.Bounce).duration(1*500).playOn(frame);
            }
        });
    }
}
