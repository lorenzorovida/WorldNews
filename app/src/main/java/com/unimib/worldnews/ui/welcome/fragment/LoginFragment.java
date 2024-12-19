package com.unimib.worldnews.ui.welcome.fragment;

import static com.unimib.worldnews.util.Constants.INVALID_CREDENTIALS_ERROR;
import static com.unimib.worldnews.util.Constants.INVALID_USER_ERROR;
import static com.unimib.worldnews.util.Constants.SHARED_PREFERENCES_CATEGORIES_OF_INTEREST;
import static com.unimib.worldnews.util.Constants.SHARED_PREFERENCES_COUNTRY_OF_INTEREST;
import static com.unimib.worldnews.util.Constants.SHARED_PREFERENCES_FILENAME;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.worldnews.R;
import com.unimib.worldnews.model.Result;
import com.unimib.worldnews.model.User;
import com.unimib.worldnews.repository.user.IUserRepository;
import com.unimib.worldnews.ui.home.HomeActivity;
import com.unimib.worldnews.ui.welcome.viewmodel.UserViewModel;
import com.unimib.worldnews.ui.welcome.viewmodel.UserViewModelFactory;
import com.unimib.worldnews.util.ServiceLocator;
import com.unimib.worldnews.util.SharedPreferencesUtils;

public class LoginFragment extends Fragment {

    private final static String TAG = LoginFragment.class.getSimpleName();

    private TextInputEditText editTextEmail, editTextPassword;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult;
    private UserViewModel userViewModel;

    public LoginFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().
                getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        oneTapClient = Identity.getSignInClient(requireActivity());
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

        startIntentSenderForResult = new ActivityResultContracts.StartIntentSenderForResult();

        activityResultLauncher = registerForActivityResult(startIntentSenderForResult, activityResult -> {
            if (activityResult.getResultCode() == Activity.RESULT_OK) {
                Log.d(TAG, "result.getResultCode() == Activity.RESULT_OK");
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate with Firebase.
                        userViewModel.getGoogleUserMutableLiveData(idToken).observe(getViewLifecycleOwner(), authenticationResult -> {
                            if (authenticationResult.isSuccess()) {
                                User user = ((Result.UserSuccess) authenticationResult).getData();
                                //saveLoginData(user.getEmail(), null, user.getIdToken());
                                Log.i(TAG, "Logged as: " + user.getEmail());
                                userViewModel.setAuthenticationError(false);
                                retrieveUserInformationAndStartActivity(user, getView());
                            } else {
                                userViewModel.setAuthenticationError(true);
                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        getErrorMessage(((Result.Error) authenticationResult).getMessage()),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (ApiException e) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            requireActivity().getString(R.string.error_unexpected),
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void retrieveUserInformationAndStartActivity(User user, View view) {
        //progressIndicator.setVisibility(View.VISIBLE);

        userViewModel.getUserPreferences(user.getIdToken()).observe(
                getViewLifecycleOwner(), userPreferences -> {
                    //The viewmodel updated sharedprefs
                    goToNextPage(view);
                }
        );
    }

    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case INVALID_CREDENTIALS_ERROR:
                return requireActivity().getString(R.string.error_password_login);
            case INVALID_USER_ERROR:
                return requireActivity().getString(R.string.error_email_login);
            default:
                return requireActivity().getString(R.string.error_unexpected);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    private void goToNextPage(View view) {
        SharedPreferencesUtils sharedPreferencesUtil =
                new SharedPreferencesUtils(requireActivity().getApplication());

        if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILENAME,
                SHARED_PREFERENCES_COUNTRY_OF_INTEREST) != null &&
                sharedPreferencesUtil.readStringSetData(SHARED_PREFERENCES_FILENAME,
                        SHARED_PREFERENCES_CATEGORIES_OF_INTEREST) != null) {

            startActivity(new Intent(getContext(), HomeActivity.class));
        } else {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_pickCountryFragment);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (userViewModel.getLoggedUser() != null) {
            goToNextPage(view);
        }

        editTextEmail = view.findViewById(R.id.textInputEmail);
        editTextPassword = view.findViewById(R.id.textInputPassword);

        Button loginButton = view.findViewById(R.id.loginButton);
        Button signupButton = view.findViewById(R.id.buttonNewAccount);
        Button loginGoogleButton = view.findViewById(R.id.loginGoogleButton);

        loginButton.setOnClickListener(v -> {
            if (editTextEmail.getText() != null && isEmailOk(editTextEmail.getText().toString())) {
                if (editTextPassword.getText() != null && isPasswordOk(editTextPassword.getText().toString())) {
                    Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_pickCountryFragment);

                } else {
                    editTextPassword.setError(getString(R.string.error_password_login));
                }
            } else {
                editTextEmail.setError(getString(R.string.error_email_login));
            }
        });

        loginGoogleButton.setOnClickListener(v -> oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        Log.d(TAG, "onSuccess from oneTapClient.beginSignIn(BeginSignInRequest)");
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                        activityResultLauncher.launch(intentSenderRequest);
                    }
                })
                .addOnFailureListener(requireActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());

                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                requireActivity().getString(R.string.error_unexpected),
                                Snackbar.LENGTH_SHORT).show();
                    }
                }));

        signupButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signupFragment);
        });
    }




    private boolean isEmailOk(String email) {
        return true;
        //return EmailValidator.getInstance().isValid(email);
    }

    private boolean isPasswordOk(String password) {
        return true;
        //return password.length() > 7;
    }
}