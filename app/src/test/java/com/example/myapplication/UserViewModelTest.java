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

// Unit test class for UserViewModel, testing various scenarios for user operations.
@RunWith(MockitoJUnitRunner.class)
public class UserViewModelTest {

    // Mock object for FirestoreRepository
    @Mock
    private FirestoreRepository firestoreRepository;

    // Inject mocks into UserViewModel
    @InjectMocks
    private UserViewModel userViewModel;

    // Mock observers for LiveData
    @Mock
    private Observer<List<User>> userListObserver;

    @Mock
    private Observer<List<User>> filteredUserObserver;

    @Mock
    private Observer<LatLng> currentUserLatLngObserver;

    // Rule to allow LiveData to execute synchronously
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    // Set up method to initialize mocks and the view model
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userViewModel = new UserViewModel(firestoreRepository);
        userViewModel.getUserList().observeForever(userListObserver);
        userViewModel.getFilteredUsers().observeForever(filteredUserObserver);
        userViewModel.getCurrentUserLatLng().observeForever(currentUserLatLngObserver);
    }

    // Test case for checking and creating a user
    @Test
    public void testCheckAndCreateUser() {
        // Arrange: Set up test data
        String uid = "user123";
        String userName = "John Doe";
        String selectedRestaurantId = "restaurant123";
        String selectedRestaurantName = "Restaurant Name";
        List<String> favoriteRestaurants = Arrays.asList("fav1", "fav2");
        String photoUrl = "http://example.com/photo.jpg";

        // Act: Call the method to be tested
        userViewModel.checkAndCreateUser(uid, userName, selectedRestaurantId, selectedRestaurantName, favoriteRestaurants, photoUrl);

        // Assert: Verify that the repository method is called with the correct parameters
        verify(firestoreRepository).checkAndCreateUser(uid, userName, selectedRestaurantId, selectedRestaurantName, favoriteRestaurants, photoUrl);
    }

    // Test case for updating the user list
    @Test
    public void testUpdateUserList() {
        // Arrange: Set up mock data
        List<User> mockUsers = Arrays.asList(new User(), new User());

        // Simulate the callback when the repository calls back
        doAnswer(invocation -> {
            FirestoreRepository.OnUsersRetrievedListener callback = invocation.getArgument(0);
            callback.onUsersRetrieved(mockUsers);
            return null;
        }).when(firestoreRepository).getAllUsers(any(FirestoreRepository.OnUsersRetrievedListener.class));

        // Act: Call the method to be tested
        userViewModel.updateUserList();

        // Assert: Verify that the observer is called with the correct data
        verify(userListObserver).onChanged(mockUsers);
        verify(firestoreRepository).listenForUsersUpdates(any(FirestoreRepository.OnUsersRetrievedListener.class));
    }

    // Test case for listening to user updates
    @Test
    public void testListenForUsersUpdates() {
        // Arrange: Set up mock data
        List<User> mockUsers = Arrays.asList(new User(), new User());

        // Simulate the callback when the repository calls back
        doAnswer(invocation -> {
            FirestoreRepository.OnUsersRetrievedListener callback = invocation.getArgument(0);
            callback.onUsersRetrieved(mockUsers);
            return null;
        }).when(firestoreRepository).listenForUsersUpdates(any(FirestoreRepository.OnUsersRetrievedListener.class));

        // Act: Call the method to be tested
        userViewModel.listenForUsersUpdates();

        // Assert: Verify that the observer is called with the correct data
        verify(userListObserver).onChanged(mockUsers);
    }

    // Test case for removing listeners
    @Test
    public void testRemoveListeners() {
        // Act: Call the method to be tested
        userViewModel.removeListeners();

        // Assert: Verify that the repository method to remove listeners is called
        verify(firestoreRepository).removeUsersListener();
    }

    // Test case for getting users by restaurant name
    @Test
    public void testGetUsersByRestaurantName() {
        // Arrange: Set up test data
        User user1 = new User();
        user1.setSelectedRestaurantName("Restaurant One");
        User user2 = new User();
        user2.setSelectedRestaurantName("Restaurant Two");
        List<User> mockUsers = Arrays.asList(user1, user2);

        // Act: Set the user list in the view model and filter by restaurant name
        userViewModel.setUserList(mockUsers);
        userViewModel.getUsersByRestaurantName("One");

        // Assert: Verify that the filtered user observer is called with the correct data
        verify(filteredUserObserver).onChanged(Arrays.asList(user1));
    }

    // Test case for getting a user by ID
    @Test
    public void testGetUserById() {
        // Arrange: Set up test data
        User user1 = new User();
        User user2 = new User();
        user1.setUserId("1");
        user2.setUserId("2");
        List<User> mockUsers = Arrays.asList(user1, user2);

        // Act: Set the user list in the view model
        userViewModel.setUserList(mockUsers);

        // Assert: Verify that the correct user is returned by ID
        assertEquals(user1, userViewModel.getUserById("1"));
        assertNull(userViewModel.getUserById("3"));
    }

    // Test case for setting the current user's location
    @Test
    public void testSetCurrentUserLatLng() {
        // Arrange: Set up test data
        LatLng latLng = new LatLng(10.0, 20.0);

        // Act: Call the method to be tested
        userViewModel.setCurrentUserLatLng(latLng);

        // Assert: Verify that the observer is called with the correct data
        verify(currentUserLatLngObserver).onChanged(latLng);
    }
}