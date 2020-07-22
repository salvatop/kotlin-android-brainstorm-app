package app.salvatop.brainstorm.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

import app.salvatop.brainstorm.R;
import app.salvatop.brainstorm.RegisterActivity;


public class SettingsFragment extends Fragment {

    private static final String TAG = "Fragment";
    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();

        /////TODO testing code
        //setupProfile(firebaseAuth,"gigi la trottola","https://firebasestorage.googleapis.com/v0/b/brainstorm-f3b48.appspot.com/o/eiffel-tower.jpg?alt=media&token=1c3332ec-6a44-4ef0-83bf-40a12fff86a0");
        ////TODO end of testing code

        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    public void setupProfile(FirebaseAuth mAuth, String displayName, String photoUrl) {
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .setPhotoUri(Uri.parse(photoUrl))
                .build();

        assert user != null;
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                        //username = user.getDisplayName(); // TODO promise and future
                    }
                });
    }

    public void removeUser(FirebaseAuth auth){
        FirebaseUser user =  auth.getCurrentUser();
        assert user != null;
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                            Objects.requireNonNull(getContext()).startActivity(new Intent(getContext(), RegisterActivity.class));
                            Objects.requireNonNull(getActivity()).finish();
                        } else {
                            Toast.makeText(getContext(), "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}