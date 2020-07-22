package app.salvatop.brainstorm.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import app.salvatop.brainstorm.R;
import app.salvatop.brainstorm.RegisterActivity;


public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
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