package com.chanpreet.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.chanpreet.notes.databinding.FragmentNoteDisplayBinding;
import com.google.android.material.snackbar.Snackbar;

public class NoteDisplayFragment extends Fragment {
    private NoteDatabase database;
    private Note lastDeleted = null;
    private FragmentNoteDisplayBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNoteDisplayBinding.inflate(inflater, container, false);


        //referencing
        NoteAdapter adapter = new NoteAdapter(requireContext());
        database = NoteDatabase.getInstance();
        database.setNoteRVAdapter(adapter);
        database.fetchNotes(requireContext());
        binding.createFAB.setOnClickListener(view -> startActivity(new Intent(requireContext(), NoteEditorActivity.class)));
        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.recyclerView.setAdapter(adapter);

        //Recycler View Item Click Listener
        adapter.setOnClickListener(note -> {
            Intent intent = new Intent(requireContext(), NoteEditorActivity.class);
            intent.putExtra(NoteEditorActivity.EXTRA_NOTE, note);
            startActivity(intent);
        });

        //Recycler View Item Slide Right|Left.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Note noteToBeDeleted = adapter.getNoteAt(viewHolder.getAdapterPosition());
                database.delete(requireContext(), noteToBeDeleted);
                lastDeleted = noteToBeDeleted;
                Snackbar.make(binding.getRoot(), "Undo the delete operation?", Snackbar.LENGTH_LONG)
                        .setAction("Undo", view -> {
                            if (lastDeleted != null) {
                                database.insertOrUpdate(requireContext(), lastDeleted);
                            }
                        })
                        .show();
            }
        }).attachToRecyclerView(binding.recyclerView);
        return binding.getRoot();
    }
}