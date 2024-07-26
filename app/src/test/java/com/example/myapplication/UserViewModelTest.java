package com.example.myapplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.example.myapplication.Model.User;
import com.example.myapplication.Repository.FirestoreRepository;
import com.example.myapplication.viewModel.UserViewModel;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class UserViewModelTest {

    @Mock
    private FirestoreRepository firestoreRepository;

    @InjectMocks
    private UserViewModel userViewModel;

    @Mock
    private Observer<List<User>> userListObserver;

    @Mock
    private Observer<List<User>> filteredUserObserver;

    @Mock
    private Observer<LatLng> currentUserLatLngObserver;

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userViewModel = new UserViewModel(firestoreRepository);
        userViewModel.getUserList().observeForever(userListObserver);
        userViewModel.getFilteredUsers().observeForever(filteredUserObserver);
        userViewModel.getCurrentUserLatLng().observeForever(currentUserLatLngObserver);
    }

    @Test
    public void testCheckAndCreateUser() {
        String uid = "user123";
        String userName = "John Doe";
        String selectedRestaurantId = "restaurant123";
        String selectedRestaurantName = "Restaurant Name";
        List<String> favoriteRestaurants = Arrays.asList("fav1", "fav2");
        String photoUrl = "http://example.com/photo.jpg";

        userViewModel.checkAndCreateUser(uid, userName, selectedRestaurantId, selectedRestaurantName, favoriteRestaurants, photoUrl);

        verify(firestoreRepository).checkAndCreateUser(uid, userName, selectedRestaurantId, selectedRestaurantName, favoriteRestaurants, photoUrl);
    }

    @Test
    public void testUpdateUserList() {
        List<User> mockUsers = Arrays.asList(new User(), new User());

        doAnswer(invocation -> {
            FirestoreRepository.OnUsersRetrievedListener callback = invocation.getArgument(0);
            callback.onUsersRetrieved(mockUsers);
            return null;
        }).when(firestoreRepository).getAllUsers(any(FirestoreRepository.OnUsersRetrievedListener.class));

        userViewModel.updateUserList();

        verify(userListObserver).onChanged(mockUsers);
        verify(firestoreRepository).listenForUsersUpdates(any(FirestoreRepository.OnUsersRetrievedListener.class));
    }

    @Test
    public void testListenForUsersUpdates() {
        List<User> mockUsers = Arrays.asList(new User(), new User());

        doAnswer(invocation -> {
            FirestoreRepository.OnUsersRetrievedListener callback = invocation.getArgument(0);
            callback.onUsersRetrieved(mockUsers);
            return null;
        }).when(firestoreRepository).listenForUsersUpdates(any(FirestoreRepository.OnUsersRetrievedListener.class));

        userViewModel.listenForUsersUpdates();

        verify(userListObserver).onChanged(mockUsers);
    }

    @Test
    public void testRemoveListeners() {
        userViewModel.removeListeners();

        verify(firestoreRepository).removeUsersListener();
    }

    @Test
    public void testGetUsersByRestaurantName() {
        User user1 = new User();
        user1.setSelectedRestaurantName("Restaurant One");
        User user2 = new User();
        user2.setSelectedRestaurantName("Restaurant Two");
        List<User> mockUsers = Arrays.asList(user1, user2);

        userViewModel.setUserList(mockUsers);
        userViewModel.getUsersByRestaurantName("One");

        verify(filteredUserObserver).onChanged(Arrays.asList(user1));
    }

    @Test
    public void testGetUserById() {
        User user1 = new User();
        User user2 = new User();
        user1.setUserId("1");
        user2.setUserId("2");
        List<User> mockUsers = Arrays.asList(user1, user2);

        userViewModel.setUserList(mockUsers);

        assertEquals(user1, userViewModel.getUserById("1"));
        assertNull(userViewModel.getUserById("3"));
    }

    @Test
    public void testSetCurrentUserLatLng() {
        LatLng latLng = new LatLng(10.0, 20.0);

        userViewModel.setCurrentUserLatLng(latLng);

        verify(currentUserLatLngObserver).onChanged(latLng);
    }
}
