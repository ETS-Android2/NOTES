package com.chanpreet.notes;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.ViewHolder> {
    private final Context context;
    private OnItemClickListener listener;

    protected NoteAdapter(Context context) {
        super(DIFF);
        this.context = context;
    }

    public static final DiffUtil.ItemCallback<Note> DIFF = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getID().equals(newItem.getID());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getID().equals(newItem.getID())
                    && oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.getDescription().equals(newItem.getDescription())
                    && oldItem.getColor() == newItem.getColor();
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.note_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titleTextView.setText(getItem(position).getTitle());
        holder.descriptionTextView.setText(getItem(position).getDescription());
        holder.cardLayout.setBackgroundTintList(ColorStateList.valueOf(getItem(position).getColor()));
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.zoom_in));
    }

    public Note getNoteAt(int position) {
        return getItem(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        LinearLayout cardLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTV);
            descriptionTextView = itemView.findViewById(R.id.descriptionTV);
            cardLayout = itemView.findViewById(R.id.cardLayout);
            itemView.setOnClickListener(view -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.OnClick(getItem(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void OnClick(Note note);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
