package com.retsi.dabijhouder;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.OpdrachtViewHolder> {

    private ArrayList<OpdrachtItem> mOpdrachtItems;
    private ItemClickListener mClickListener;
    private LongItemClickListener mLongItemClickListener;
    private DatabaseHelper myDb;
    private Context context;

    public interface ItemClickListener {
        void onItemClick(View v, int position);
    }

    public interface LongItemClickListener {
        void onItemLongClick(View v, int position);
    }

    public RecyclerAdapter(ArrayList<OpdrachtItem> opdrachtItems, DatabaseHelper myDb, Context context) {
        mOpdrachtItems = opdrachtItems;
        this.myDb = myDb;
        this.context = context;
    }

    public OpdrachtItem getItem(int position) {
        return mOpdrachtItems.get(position);
    }

    public void RemoveItem(int position) {
        mOpdrachtItems.remove(position);
        notifyItemRemoved(position);
    }

    public void InsertItem(OpdrachtItem item, int position) {
        mOpdrachtItems.add(position, item);
        notifyDataSetChanged();
    }

    public void UpdateItems(ArrayList<OpdrachtItem> items) {
        mOpdrachtItems.clear();
        mOpdrachtItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OpdrachtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.opdracht_item, parent,
                false);
        OpdrachtViewHolder ovh = new OpdrachtViewHolder(v, mClickListener, mLongItemClickListener);
        return ovh;
    }

    @Override
    public void onBindViewHolder(@NonNull OpdrachtViewHolder holder, int position) {
        OpdrachtItem currentItem = mOpdrachtItems.get(position);

        holder.typeOpdracht.setText(currentItem.getTypeOpdracht());
        holder.vakNaam.setText(currentItem.getVakNaam());
        holder.titel.setText(currentItem.getTitel());
        holder.datum.setText(currentItem.getDatum());
        holder.beschrijving.setText(currentItem.getBeschrijving());
        holder.tv_vak_kleur.setBackgroundColor(Color.parseColor(myDb.getVakKleur(currentItem.getVakNaam())));
        if (currentItem.getBelangerijk() == 1){
            holder.img_star.setVisibility(View.VISIBLE);
        } else holder.img_star.setVisibility(View.INVISIBLE);

        if (mOpdrachtItems.get(position).isExpanded()){
            TransitionManager.beginDelayedTransition(holder.parent);
            holder.expandedRelLayout.setVisibility(View.VISIBLE);
        } else{
            TransitionManager.beginDelayedTransition(holder.parent);
            holder.expandedRelLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mOpdrachtItems.size();
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    void setLongClickListener(LongItemClickListener longItemClickListener) {
        this.mLongItemClickListener = longItemClickListener;
    }

    public class OpdrachtViewHolder extends RecyclerView.ViewHolder{
        private TextView typeOpdracht, vakNaam, titel, datum, beschrijving, tv_vak_kleur;
        private RelativeLayout expandedRelLayout;
        private CardView parent;
        private ImageButton check_button;
        private ImageView img_star;

        public OpdrachtViewHolder(@NonNull View itemView, ItemClickListener listener,
                                  LongItemClickListener longListener) {
            super(itemView);
            typeOpdracht = itemView.findViewById(R.id.typeOpdracht);
            vakNaam = itemView.findViewById(R.id.opdrachtVak);
            titel = itemView.findViewById(R.id.opdrachtTitel);
            datum = itemView.findViewById(R.id.opdrachtDatum);
            beschrijving = itemView.findViewById(R.id.opdrachtBeschrijving);
            parent = itemView.findViewById(R.id.opdrachtItem);
            expandedRelLayout = itemView.findViewById(R.id.expandedRelLayout);
            tv_vak_kleur = itemView.findViewById(R.id.tv_opdracht_item_kleur);
            check_button = itemView.findViewById(R.id.img_btn_check_opdracht);
            img_star = itemView.findViewById(R.id.opdrachtImgStar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(view, position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (longListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            longListener.onItemLongClick(view, position);
                            return true;
                        }
                    }
                    return false;
                }
            });

            check_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(view, position);
                        }
                    }
                }
            });


        }
    }

}
