package com.retsi.dabijhouder;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VakkenListAdapter extends RecyclerView.Adapter<VakkenListAdapter.VakViewHolder> {

    private final ArrayList<VakItem> mOpdrachtItems;
    private ItemClickListener mClickListener;

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public VakkenListAdapter(ArrayList<VakItem> items) {
        this.mOpdrachtItems = items;
    }

    public void updateData(ArrayList<VakItem> newlist) {
        mOpdrachtItems.clear();
        mOpdrachtItems.addAll(newlist);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VakViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vakken_list_item, parent, false);
        return new VakViewHolder(v, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VakViewHolder holder, int position) {
        VakItem currentItem = mOpdrachtItems.get(position);
        holder.vaknaam.setText(currentItem.getVaknaam());

        holder.tv_color_picker.setBackgroundColor(Color.parseColor(currentItem.getVakColor()));
        holder.tv_color_picker.setBackgroundColor(Color.parseColor(currentItem.getVakColor()));
    }

    @Override
    public int getItemCount() {
        return mOpdrachtItems.size();
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    VakItem getItem(int id) {
        return mOpdrachtItems.get(id);
    }

    public class VakViewHolder extends RecyclerView.ViewHolder{
        private final TextView tv_color_picker, vaknaam, tv_delete_vak;
        private final ImageView img_color_picker, img_delete_vak;
        private CardView parent;


        public VakViewHolder(@NonNull View itemView, ItemClickListener listener) {
            super(itemView);
            parent = itemView.findViewById(R.id.vak_item_card);
            vaknaam = itemView.findViewById(R.id.tv_vak_list_item);
            img_color_picker = itemView.findViewById(R.id.image_color_picker);
            tv_color_picker = itemView.findViewById(R.id.tv_color_picker);
            tv_delete_vak = itemView.findViewById(R.id.tv_delete_vak);
            img_delete_vak = itemView.findViewById(R.id.image_delete_vak);

            View.OnClickListener onClickListener = view -> {
                if (listener != null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        listener.onItemClick(view, position);
                    }
                }
            };

            img_color_picker.setOnClickListener(onClickListener);
            tv_color_picker.setOnClickListener(onClickListener);
            tv_delete_vak.setOnClickListener(onClickListener);
            img_delete_vak.setOnClickListener(onClickListener);
        }
    }

}
