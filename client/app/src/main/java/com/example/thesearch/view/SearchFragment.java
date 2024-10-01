package com.example.thesearch.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.thesearch.R;
import com.example.thesearch.databinding.FragmentSearchBinding;
import com.example.thesearch.model.User;
import com.example.thesearch.viewmodel.SearchViewModel;
import com.example.thesearch.viewmodel.UserProfileViewModel;

import java.util.List;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private SearchViewModel searchViewModel;
    private AutoCompleteTextView searchField;
    private ListView resultsListView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        searchField = binding.searchField;
        resultsListView = binding.resultsListView;

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No implementation needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    searchViewModel.searchUsers(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No implementation needed
            }
        });

        searchViewModel.getSearchResults().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if (users != null) {
                    ArrayAdapter<User> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, users);
                    resultsListView.setAdapter(adapter);
                } else {
                    resultsListView.setAdapter(null);
                }
            }
        });

        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = (User) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), "Selected: " + selectedUser.getEmail(), Toast.LENGTH_SHORT).show();

                // Set the selected user and display UserProfileFragment
                UserProfileViewModel userProfileViewModel = new ViewModelProvider(requireActivity()).get(UserProfileViewModel.class);
                userProfileViewModel.setUserProfile(selectedUser);

                // Use NavController to navigate to UserProfileFragment
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.action_searchFragment_to_userProfileFragment);
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
