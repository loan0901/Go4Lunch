package com.example.myapplication;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

import com.example.myapplication.Model.Restaurant;
import com.example.myapplication.Model.User;
import com.example.myapplication.Repository.FirestoreRepositoryInterface;
import com.example.myapplication.Repository.MockFirestoreRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

// Unit test class for MockFirestoreRepository, testing various methods related to user and restaurant data management.
@RunWith(MockitoJUnitRunner.class)
public class MockFirestoreRepositoryTest {

    // Instance of the repository interface
    private FirestoreRepositoryInterface mockRepository;

    // Mock listeners for user and restaurant retrieval
    @Mock
    private FirestoreRepositoryInterface.OnUsersRetrievedListener mockUsersListener;

    @Mock
    private FirestoreRepositoryInterface.OnRestaurantsRetrievedListener mockRestaurantsListener;

    // Set up method to initialize mocks
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockRepository = new MockFirestoreRepository();
    }

    // Test to check user creation when user does not exist
    @Test
    public void testCheckAndCreateUser_userDoesNotExist() {
        // Arrange: Set up test data
        String uid = "testUid";
        String userName = "testUser";
        String selectedRestaurantId = "restaurant1";
        String selectedRestaurantName = "Test Restaurant";
        List<String> favoriteRestaurants = new ArrayList<>();
        String photoUrl = "photoUrl";

        // Act: Call the method to be tested
        mockRepository.checkAndCreateUser(uid, userName, selectedRestaurantId, selectedRestaurantName, favoriteRestaurants, photoUrl);

        // Assert: Verify user is created
        mockRepository.getAllUsers(new FirestoreRepositoryInterface.OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(List<User> userList) {
                assertEquals(1, userList.size());
                User user = userList.get(0);
                assertEquals(uid, user.getUserId());
                assertEquals(userName, user.getUserName());
                assertEquals(selectedRestaurantId, user.getSelectedRestaurantId());
                assertEquals(selectedRestaurantName, user.getSelectedRestaurantName());
                assertEquals(favoriteRestaurants, user.getFavoriteRestaurants());
                assertEquals(photoUrl, user.getPhotoUrl());
            }

            @Override
            public void onError(Exception e) {
                fail("Error retrieving users");
            }
        });
    }

    // Test to check user creation when user already exists
    @Test
    public void testCheckAndCreateUser_userExists() {
        // Arrange: Set up test data
        String uid = "testUid";
        String userName = "testUser";
        String selectedRestaurantId = "restaurant1";
        String selectedRestaurantName = "Test Restaurant";
        List<String> favoriteRestaurants = new ArrayList<>();
        String photoUrl = "photoUrl";

        mockRepository.checkAndCreateUser(uid, userName, selectedRestaurantId, selectedRestaurantName, favoriteRestaurants, photoUrl);

        // Act: Call the method again to simulate user already exists
        mockRepository.checkAndCreateUser(uid, userName, selectedRestaurantId, selectedRestaurantName, favoriteRestaurants, photoUrl);

        // Assert: Verify that no duplicate user is created
        mockRepository.getAllUsers(new FirestoreRepositoryInterface.OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(List<User> userList) {
                assertEquals(1, userList.size());
            }

            @Override
            public void onError(Exception e) {
                fail("Error retrieving users");
            }
        });
    }

    // Test to retrieve all users
    @Test
    public void testGetAllUsers() {
        // Arrange: Set up test data for multiple users
        String uid1 = "testUid1";
        String userName1 = "testUser1";
        String selectedRestaurantId1 = "restaurant1";
        String selectedRestaurantName1 = "Test Restaurant 1";
        List<String> favoriteRestaurants1 = new ArrayList<>();
        String photoUrl1 = "photoUrl1";

        String uid2 = "testUid2";
        String userName2 = "testUser2";
        String selectedRestaurantId2 = "restaurant2";
        String selectedRestaurantName2 = "Test Restaurant 2";
        List<String> favoriteRestaurants2 = new ArrayList<>();
        String photoUrl2 = "photoUrl2";

        mockRepository.checkAndCreateUser(uid1, userName1, selectedRestaurantId1, selectedRestaurantName1, favoriteRestaurants1, photoUrl1);
        mockRepository.checkAndCreateUser(uid2, userName2, selectedRestaurantId2, selectedRestaurantName2, favoriteRestaurants2, photoUrl2);

        // Act: Retrieve all users
        mockRepository.getAllUsers(new FirestoreRepositoryInterface.OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(List<User> userList) {
                // Assert: Verify the number of users retrieved
                assertEquals(2, userList.size());
            }

            @Override
            public void onError(Exception e) {
                fail("Error retrieving users");
            }
        });
    }

    // Test to listen for user updates
    @Test
    public void testListenForUsersUpdates() {
        // Arrange: Set up listener
        mockRepository.listenForUsersUpdates(mockUsersListener);

        // Act: Create a user
        String uid = "testUid";
        String userName = "testUser";
        String selectedRestaurantId = "restaurant1";
        String selectedRestaurantName = "Test Restaurant";
        List<String> favoriteRestaurants = new ArrayList<>();
        String photoUrl = "photoUrl";

        mockRepository.checkAndCreateUser(uid, userName, selectedRestaurantId, selectedRestaurantName, favoriteRestaurants, photoUrl);

        // Assert: Verify listener is notified
        verify(mockUsersListener).onUsersRetrieved(anyList());
    }

    // Test to check restaurant creation when restaurant does not exist
    @Test
    public void testCheckOrCreateRestaurant_restaurantDoesNotExist() {
        // Arrange: Set up test data
        String restaurantId = "restaurant1";
        int likeCount = 0;
        List<String> userIdSelected = new ArrayList<>();

        // Act: Call the method to be tested
        mockRepository.checkOrCreateRestaurant(restaurantId, likeCount, userIdSelected);

        // Assert: Verify restaurant is created
        mockRepository.getAllRestaurants(new FirestoreRepositoryInterface.OnRestaurantsRetrievedListener() {
            @Override
            public void onRestaurantsRetrieved(List<Restaurant> restaurantList) {
                assertEquals(1, restaurantList.size());
                Restaurant restaurant = restaurantList.get(0);
                assertEquals(restaurantId, restaurant.getRestaurantId());
                assertEquals(likeCount, restaurant.getLikeCount());
                assertEquals(userIdSelected, restaurant.getUserIdSelected());
            }

            @Override
            public void onError(Exception e) {
                fail("Error retrieving restaurants");
            }
        });
    }

    // Test to check restaurant creation when restaurant already exists
    @Test
    public void testCheckOrCreateRestaurant_restaurantExists() {
        // Arrange: Set up test data
        String restaurantId = "restaurant1";
        int likeCount = 0;
        List<String> userIdSelected = new ArrayList<>();

        mockRepository.checkOrCreateRestaurant(restaurantId, likeCount, userIdSelected);

        // Act: Call the method again to simulate restaurant already exists
        mockRepository.checkOrCreateRestaurant(restaurantId, likeCount, userIdSelected);

        // Assert: Verify that no duplicate restaurant is created
        mockRepository.getAllRestaurants(new FirestoreRepositoryInterface.OnRestaurantsRetrievedListener() {
            @Override
            public void onRestaurantsRetrieved(List<Restaurant> restaurantList) {
                assertEquals(1, restaurantList.size());
            }

            @Override
            public void onError(Exception e) {
                fail("Error retrieving restaurants");
            }
        });
    }

    // Test to add a favorite restaurant
    @Test
    public void testToggleFavoriteRestaurant_addFavorite() {
        // Arrange: Set up test data
        String uid = "testUid";
        String userName = "testUser";
        String selectedRestaurantId = "restaurant1";
        String selectedRestaurantName = "Test Restaurant";
        List<String> favoriteRestaurants = new ArrayList<>();
        String photoUrl = "photoUrl";

        mockRepository.checkAndCreateUser(uid, userName, selectedRestaurantId, selectedRestaurantName, favoriteRestaurants, photoUrl);

        // Act: Toggle favorite restaurant
        mockRepository.toggleFavoriteRestaurant(uid, "restaurant2");

        // Assert: Verify restaurant is added to favorites
        mockRepository.getAllUsers(new FirestoreRepositoryInterface.OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(List<User> userList) {
                User user = userList.get(0);
                assertTrue(user.getFavoriteRestaurants().contains("restaurant2"));
            }

            @Override
            public void onError(Exception e) {
                fail("Error retrieving users");
            }
        });
    }

    // Test to remove a favorite restaurant
    @Test
    public void testToggleFavoriteRestaurant_removeFavorite() {
        // Arrange: Set up test data
        String uid = "testUid";
        String userName = "testUser";
        String selectedRestaurantId = "restaurant1";
        String selectedRestaurantName = "Test Restaurant";
        List<String> favoriteRestaurants = new ArrayList<>();
        favoriteRestaurants.add("restaurant2");
        String photoUrl = "photoUrl";

        mockRepository.checkAndCreateUser(uid, userName, selectedRestaurantId, selectedRestaurantName, favoriteRestaurants, photoUrl);

        // Act: Toggle favorite restaurant to remove it
        mockRepository.toggleFavoriteRestaurant(uid, "restaurant2");

        // Assert: Verify restaurant is removed from favorites
        mockRepository.getAllUsers(new FirestoreRepositoryInterface.OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(List<User> userList) {
                User user = userList.get(0);
                assertFalse(user.getFavoriteRestaurants().contains("restaurant2"));
            }

            @Override
            public void onError(Exception e) {
                fail("Error retrieving users");
            }
        });
    }

    // Test to update selected restaurant for a user
    @Test
    public void testUpdateSelectedRestaurant() {
        // Arrange: Set up test data
        String uid = "testUid";
        String userName = "testUser";
        String selectedRestaurantId = "restaurant1";
        String selectedRestaurantName = "Test Restaurant";
        List<String> favoriteRestaurants = new ArrayList<>();
        String photoUrl = "photoUrl";

        mockRepository.checkAndCreateUser(uid, userName, selectedRestaurantId, selectedRestaurantName, favoriteRestaurants, photoUrl);
        mockRepository.checkOrCreateRestaurant(selectedRestaurantId, 0, new ArrayList<>());

        // Act: Update selected restaurant
        mockRepository.updateSelectedRestaurant(uid, "restaurant2", "New Restaurant");

        // Assert: Verify selected restaurant is updated
        mockRepository.getAllUsers(new FirestoreRepositoryInterface.OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(List<User> userList) {
                User user = userList.get(0);
                assertEquals("restaurant2", user.getSelectedRestaurantId());
                assertEquals("New Restaurant", user.getSelectedRestaurantName());
            }

            @Override
            public void onError(Exception e) {
                fail("Error retrieving users");
            }
        });
    }
}
