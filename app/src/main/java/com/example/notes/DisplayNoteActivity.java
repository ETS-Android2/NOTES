package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DisplayNoteActivity extends AppCompatActivity {

    ArrayList<Note> listOfNotes = new ArrayList<>();
    RecyclerView recyclerView;
    FloatingActionButton createFAB;
    RecyclerViewAdapter recyclerViewAdapter;
    TextView userNameTextView;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logOut) {
            LogOut();
            SharedPreferences sharedPreferences = getSharedPreferences("APP", MODE_PRIVATE);
            sharedPreferences.edit().putString("Email", "").apply();
            sharedPreferences.edit().putString("Password", "").apply();
        } else if (item.getItemId() == R.id.deleteAllNotes) {
            deleteAllRecords();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_note);

        recyclerView = findViewById(R.id.recyclerView);
        createFAB = findViewById(R.id.createFAB);
        createFAB.setOnClickListener(view -> createButtonCLicked());
        userNameTextView = findViewById(R.id.userNameTextView);
        FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("Information").child("Name").getValue(String.class);
                userNameTextView.setText("Hello, " + name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        initialiseRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAllRecords();
    }

    private void initialiseRecyclerView() {
        recyclerViewAdapter = new RecyclerViewAdapter(this, listOfNotes);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(recyclerViewAdapter);
    }


    private void createButtonCLicked() {
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra(Params.KEY_ID, "");
        intent.putExtra(Params.KEY_TITLE, "");
        intent.putExtra(Params.KEY_DESCRIPTION, "");
        startActivity(intent);
    }


    public void addRecord(String title, String description) {
        String ID = Long.toString(System.currentTimeMillis());
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef.child("Notes").child(ID).child(Params.KEY_TITLE).setValue(title);
        myRef.child("Notes").child(ID).child(Params.KEY_DESCRIPTION).setValue(description);
    }

    public void fetchAllRecords() {
        listOfNotes.clear();
        recyclerViewAdapter.notifyDataSetChanged();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef.child("Notes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String ID = dataSnapshot.getKey();
                    String title = dataSnapshot.child(Params.KEY_TITLE).getValue(String.class);
                    String description = dataSnapshot.child(Params.KEY_DESCRIPTION).getValue(String.class);
                    Note note = new Note(ID, title, description);
                    listOfNotes.add(note);
                }
                recyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Please check your Internet Connection!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void updateRecord(String ID, String title, String description) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef.child("Notes").child(ID).child(Params.KEY_TITLE).setValue(title);
        myRef.child("Notes").child(ID).child(Params.KEY_DESCRIPTION).setValue(description);
    }

    public void deleteRecord(String ID) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        myRef.child("Notes").child(ID).removeValue();
    }

    public void deleteAllRecords() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.red_warning_icon)
                .setTitle("Delete All Notes?")
                .setMessage("Following action cannot be undone!")
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notes").removeValue();
                        fetchAllRecords();
                    }
                })
                .setPositiveButton("No", null)
                .show();
    }

    private void LogOut() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.yellow_warning_icon)
                .setMessage("Are you sure you want to sign out?")
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setPositiveButton("No", null)
                .show();

    }
}