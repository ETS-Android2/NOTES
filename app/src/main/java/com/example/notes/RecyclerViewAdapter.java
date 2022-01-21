package com.example.notes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    private ArrayList<Note> listOfNotes;
    private final String[] listOfColors = {"#F24C00", "#2D3047", "#048A81", "#57CC99", "#22577A", "#587792", "#FF9F1C", "#E71D36", "#2EC4B6"};


    public RecyclerViewAdapter(Context context, ArrayList<Note> listOfNotes) {
        this.context = context;
        this.listOfNotes = listOfNotes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.note_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titleTextView.setText(listOfNotes.get(position).getTitle());
        holder.descriptionTextView.setText(listOfNotes.get(position).getDescription());
        holder.noteCardView.setCardBackgroundColor(Color.parseColor(listOfColors[position % (listOfColors.length)]));
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_up));
    }

    @Override
    public int getItemCount() {
        return listOfNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleTextView;
        TextView descriptionTextView;
        CardView noteCardView;


        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            titleTextView = itemView.findViewById(R.id.titleTV);
            descriptionTextView = itemView.findViewById(R.id.descriptionTV);
            noteCardView = itemView.findViewById(R.id.noteCardView);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, NoteEditorActivity.class);
            Note note = listOfNotes.get(getAdapterPosition());
            intent.putExtra(Params.KEY_ID, note.getID());
            intent.putExtra(Params.KEY_TITLE, note.getTitle());
            intent.putExtra(Params.KEY_DESCRIPTION, note.getDescription());
            context.startActivity(intent);
        }
    }
}
