package com.unimib.worldnews.ui.welcome.fragment;

import static com.unimib.worldnews.util.Constants.USER_COLLISION_ERROR;
import static com.unimib.worldnews.util.Constants.WEAK_PASSWORD_ERROR;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.worldnews.R;
import com.unimib.worldnews.model.Result;
import com.unimib.worldnews.model.User;
import com.unimib.worldnews.repository.user.IUserRepository;
import com.unimib.worldnews.repository.user.UserRepository;
import com.unimib.worldnews.ui.home.viewmodel.ArticleViewModelFactory;
import com.unimib.worldnews.ui.welcome.viewmodel.UserViewModel;
import com.unimib.worldnews.ui.welcome.viewmodel.UserViewModelFactory;
import com.unimib.worldnews.util.Constants;
import com.unimib.worldnews.util.ServiceLocator;

import org.apache.commons.validator.routines.EmailValidator;

public class SignupFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextInputEditText textInputEmail, textInputPassword;


    public SignupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());

        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        userViewModel.setAuthenticationError(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        textInputEmail = view.findViewById(R.id.textInputEmail);
        textInputPassword = view.findViewById(R.id.textInputPassword);

        view.findViewById(R.id.signupButton).setOnClickListener(v -> {
            String email = textInputEmail.getText().toString().trim();
            String password = textInputPassword.getText().toString().trim();

            if (isEmailOk(email) & isPasswordOk(password)) {
                //binding.progressBar.setVisibility(View.VISIBLE);
                if (!userViewModel.isAuthenticationError()) {
                    userViewModel.getUserMutableLiveData(email, password, false).observe(
                            getViewLifecycleOwner(), result -> {
                                if (result.isSuccess()) {
                                    User user = ((Result.UserSuccess) result).getData();
                                    //saveLoginData(email, password, user.getIdToken());
                                    userViewModel.setAuthenticationError(false);
                                    Navigation.findNavController(view).navigate(
                                            R.id.action_signupFragment_to_pickCountryFragment);
                                } else {
                                    userViewModel.setAuthenticationError(true);
                                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                            getErrorMessage(((Result.Error) result).getMessage()),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    userViewModel.getUser(email, password, false);
                }
                //binding.progressBar.setVisibility(View.GONE);
            } else {
                userViewModel.setAuthenticationError(true);
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        R.string.error_email_login, Snackbar.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    private String getErrorMessage(String message) {
        switch(message) {
            case WEAK_PASSWORD_ERROR:
                return requireActivity().getString(R.string.error_password_login);
            case USER_COLLISION_ERROR:
                return requireActivity().getString(R.string.error_collision_user);
            default:
                return requireActivity().getString(R.string.error_unexpected);
        }
    }

    /**
     * Checks if the email address has a correct format.
     * @param email The email address to be validated
     * @return true if the email address is valid, false otherwise
     */
    private boolean isEmailOk(String email) {
        // Check if the email is valid through the use of this library:
        // https://commons.apache.org/proper/commons-validator/
        if (!EmailValidator.getInstance().isValid((email))) {
            textInputEmail.setError(getString(R.string.error_email_login));
            return false;
        } else {
            textInputEmail.setError(null);
            return true;
        }
    }

    /**
     * Checks if the password is not empty.
     * @param password The password to be checked
     * @return True if the password has at least 6 characters, false otherwise
     */
    private boolean isPasswordOk(String password) {
        // Check if the password length is correct
        if (password.isEmpty() || password.length() < Constants.MINIMUM_LENGTH_PASSWORD) {
            textInputPassword.setError(getString(R.string.error_password_login));
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }

}