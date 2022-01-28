package com.chanpreet.notes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NoteDatabase {
    private static NoteDatabase Instance;
    public FirebaseAuth firebaseAuth;
    public FirebaseDatabase firebaseDatabase;
    private NoteAdapter adapter;

    public static synchronized NoteDatabase getInstance() {
        if (Instance == null) {
            Instance = new NoteDatabase();
            Instance.firebaseAuth = FirebaseAuth.getInstance();
            Instance.firebaseDatabase = FirebaseDatabase.getInstance();
        }
        return Instance;
    }

    //Set adapter to recycler view.
    public void setNoteRVAdapter(NoteAdapter adapter) {
        getInstance().adapter = adapter;
    }

    //Insert or Update a note .
    public void insertOrUpdate(Context context, @NonNull Note note) {

        DatabaseReference databaseReference = getInstance().getDatabaseReference();
        if (databaseReference != null) {
            String ID = note.getID();
            if (ID == null || ID.isEmpty()) {
                note.setID(String.valueOf(System.currentTimeMillis()));
            }
            ID = note.getID();
            databaseReference.child(Params.DB_NOTES).child(ID).child(Params.KEY_ID).setValue(note.getID());
            databaseReference.child(Params.DB_NOTES).child(ID).child(Params.KEY_TITLE).setValue(note.getTitle());
            databaseReference.child(Params.DB_NOTES).child(ID).child(Params.KEY_DESCRIPTION).setValue(note.getDescription());
            databaseReference.child(Params.DB_NOTES).child(ID).child(Params.KEY_COLOR).setValue(note.getColor());
            getInstance().fetchNotes(context);
        } else {
            getInstance().noInternetConnectionError(context);
        }
    }

    //deleting a note
    public void delete(Context context, @NonNull Note note) {
        DatabaseReference databaseReference = getDatabaseReference();
        if (databaseReference != null) {
            getInstance().getDatabaseReference().child(Params.DB_NOTES).child(note.getID()).removeValue();
            getInstance().fetchNotes(context);
            getInstance().playSound(context, R.raw.sfx_pop);
        } else {
            getInstance().noInternetConnectionError(context);
        }
    }

    //deleting all records
    public void deleteAll(Context context) {
        DatabaseReference databaseReference = getDatabaseReference();
        if (databaseReference != null) {
            databaseReference.child(Params.DB_NOTES).removeValue();
            getInstance().fetchNotes(context);
            getInstance().playSound(context, R.raw.sfx_pop);
        } else {
            getInstance().noInternetConnectionError(context);
        }
    }

    //Fetching all notes.
    public void fetchNotes(Context context) {
        DatabaseReference databaseReference = getDatabaseReference();
        if (databaseReference != null) {
            databaseReference.child(Params.DB_NOTES).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Note> listOfNotes = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String ID = dataSnapshot.getKey();
                        String title = dataSnapshot.child(Params.KEY_TITLE).getValue(String.class);
                        String description = dataSnapshot.child(Params.KEY_DESCRIPTION).getValue(String.class);
                        int color = dataSnapshot.child(Params.KEY_COLOR).getValue(Integer.class);
                        Note note = new Note(ID, title, description, color);
                        listOfNotes.add(note);
                    }
                    //update recycler view
                    if (adapter != null) {
                        adapter.submitList(listOfNotes);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    getInstance().noInternetConnectionError(context);
                }
            });
        } else {
            getInstance().noInternetConnectionError(context);
        }
    }

    //sign out
    public void signOut() {
        getInstance().firebaseAuth.signOut();
    }

    //Get database reference to the user UID
    public DatabaseReference getDatabaseReference() {
        FirebaseUser user = Instance.firebaseAuth.getCurrentUser();
        if (user != null) {
            return Instance.firebaseDatabase.getReference(user.getUid());
        } else {
            return null;
        }
    }

    //Sign In user using email and password
    public void signIn(Context context, String email, String password, boolean rememberPassword) {
        Dialog dialog = createProgressDialog(context, context.getString(R.string.dialog_signing_in));
        dialog.show();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Params.SP_NAME, Context.MODE_PRIVATE);

        getInstance().firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(task -> {
                    if (Objects.requireNonNull(task.getUser()).isEmailVerified()) {
                        if (rememberPassword) {
                            sharedPreferences.edit().putString(Params.SP_EMAIL, email).apply();
                            sharedPreferences.edit().putString(Params.SP_PASSWORD, password).apply();
                        }
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    } else {
                        Toast.makeText(context, context.getString(R.string.unverified_email_error), Toast.LENGTH_LONG).show();
                        Snackbar.make(context, ((SignInActivity) context).getBinding().parentLayout, context.getString(R.string.send_verification_link), Snackbar.LENGTH_LONG)
                                .setAction(R.string.yes, view -> getInstance().sendEmailVerificationLink(context, task.getUser()))
                                .show();
                    }
                })
                .addOnFailureListener(e -> {
                    sharedPreferences.edit().clear().apply();
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(context, context.getString(R.string.invalid_user_credentials_error), Toast.LENGTH_LONG).show();
                    } else if (e instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(context, context.getString(R.string.non_registered_email_address_error), Toast.LENGTH_LONG).show();
                    } else {
                        getInstance().noInternetConnectionError(context);
                    }
                })
                .addOnCompleteListener(task -> dialog.hide());
    }

    //auto sign in
    public void autoSignIn(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Params.SP_NAME, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(Params.SP_EMAIL, "").trim();
        String password = sharedPreferences.getString(Params.SP_PASSWORD, "").trim();
        if (!email.isEmpty() && !password.isEmpty()) {
            signIn(context, email, password, true);
        }
    }

    //Create Account on FirebaseAuthentication
    public void createAccount(Context context, String name, String email, String password) {
        Dialog dialog = createProgressDialog(context, context.getString(R.string.dialog_creating_account));
        dialog.show();

        getInstance().firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    //Storing user information on Firebase
                    String uid = Objects.requireNonNull(authResult.getUser()).getUid();
                    getInstance().firebaseDatabase.getReference(uid).child(Params.DB_INFORMATION).child(Params.DB_NAME).setValue(name);
                    getInstance().firebaseDatabase.getReference(uid).child(Params.DB_INFORMATION).child(Params.DB_EMAIL).setValue(email);

                    //Default Note
                    Note note = new Note(context.getString(R.string.welcome_note_title), context.getString(R.string.welcome_note_description), Color.BLACK);
                    getInstance().insertOrUpdate(context, note);

                    //Acknowledging account created
                    Toast.makeText(context, context.getString(R.string.account_created_success), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, SignInActivity.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(context, context.getString(R.string.email_exists_error), Toast.LENGTH_LONG).show();
                    } else {
                        getInstance().noInternetConnectionError(context);
                    }
                })
                .addOnCompleteListener(task -> dialog.hide());
    }

    //Send email verification link
    public void sendEmailVerificationLink(Context context, @NonNull FirebaseUser user) {
        Dialog dialog = createProgressDialog(context, context.getString(R.string.dialog_creating_link));
        dialog.show();

        user.sendEmailVerification()
                .addOnSuccessListener(task -> Toast.makeText(context, context.getString(R.string.email_verification_success), Toast.LENGTH_LONG).show())
                .addOnFailureListener(e -> noInternetConnectionError(context))
                .addOnCompleteListener(task -> dialog.hide());
    }

    public void requestResetPasswordLink(Context context, String email) {
        Dialog dialog = createProgressDialog(context, context.getString(R.string.dialog_please_wait));
        dialog.show();

        getInstance().firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, context.getString(R.string.password_reset_success), Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    if (e instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(context, context.getString(R.string.non_registered_email_address_error), Toast.LENGTH_LONG).show();
                    } else {
                        getInstance().noInternetConnectionError(context);
                    }
                })
                .addOnCompleteListener(task -> dialog.hide());
    }

    @NonNull
    private Dialog createProgressDialog(@NonNull Context context, String message) {
        Dialog dialog = new Dialog(context);
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.custom_progress_bar, null);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        ((TextView) view.findViewById(R.id.messageTV)).setText(message);
        return dialog;
    }

    private void noInternetConnectionError(Context context) {
        Toast.makeText(context, context.getString(R.string.no_internet_error), Toast.LENGTH_LONG).show();
    }

    private void playSound(Context context, int id) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, id);
        mediaPlayer.start();
    }

    public void getEmailAndName(Context context, TextView email_TV, TextView name_TV) {
        getInstance().getDatabaseReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child(Params.DB_INFORMATION).child(Params.DB_NAME).getValue(String.class);
                String email = snapshot.child(Params.DB_INFORMATION).child(Params.DB_EMAIL).getValue(String.class);
                name_TV.setText(name);
                email_TV.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                noInternetConnectionError(context);
            }
        });
    }

    public void deleteAccount(Context context) {
        getInstance().getDatabaseReference().child(Params.DB_INFORMATION).removeValue();
        getInstance().getDatabaseReference().child(Params.DB_NOTES).removeValue();
        Objects.requireNonNull(getInstance().firebaseAuth.getCurrentUser()).delete();
        Toast.makeText(context, context.getString(R.string.account_deleted_success), Toast.LENGTH_LONG).show();
        Activity activity = ((Activity) context);
        context.startActivity(new Intent(activity, SplashScreenActivity.class));
        activity.finish();
    }
}
