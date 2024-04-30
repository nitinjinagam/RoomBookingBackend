package com.example.demo.Controller;

import com.example.demo.DTO.ErrorDTO;
import com.example.demo.Repository.BookingRepository;
import com.example.demo.Repository.RoomRepository;
import com.example.demo.DTO.RoomDTO;
import com.example.demo.Service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.Model.Room;
import com.example.demo.Request.AddRoomRequest;
import com.example.demo.Request.EditRoomRequest;
import com.example.demo.Request.DeleteRoomRequest;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService roomService;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public RoomController(RoomService roomService, RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomService = roomService;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @PostMapping
    public ResponseEntity<?> addRoom(@RequestBody AddRoomRequest addRoomRequest) {

        if (roomService.findByRoomName(addRoomRequest.getRoomName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO("Room already exists"));
        }
        else if (addRoomRequest.getRoomCapacity() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("Invalid capacity"));
        }
        Room newRoom = new Room(addRoomRequest.getRoomName(), addRoomRequest.getRoomCapacity());
        roomService.createRoom(newRoom);

        return ResponseEntity.status(HttpStatus.CREATED).body("Room created successfully");
    }

    @PatchMapping
    public ResponseEntity<?> editRoom(@RequestBody EditRoomRequest editRoomRequest) {
//        Room room = roomService.getRoomByID(editRoomRequest.getRoomID());

        if (roomService.getRoomByID(editRoomRequest.getRoomID())==null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO("Room does not exists"));
        }

        if(roomService.findByRoomName(editRoomRequest.getRoomName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorDTO("Room with given name already exists"));
        }

        if (editRoomRequest.getRoomCapacity() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("Invalid capacity"));
        }

        Room room = roomService.getRoomByID(editRoomRequest.getRoomID());
        room.setRoomName(editRoomRequest.getRoomName());
        room.setRoomCapacity(editRoomRequest.getRoomCapacity());
        roomService.createRoom(room);


        return ResponseEntity.ok("Room edited successfully");
    }

    @DeleteMapping
    public ResponseEntity<?> deleteRoom(@RequestBody DeleteRoomRequest deleteRoomRequest) {
        boolean deleted = roomService.deleteRoom(deleteRoomRequest.getRoomID());

        if (deleted) {
            return ResponseEntity.ok("Room deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDTO("Room does not exist"));
        }
    }
    public static void insertionSort(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllRoomsBasedOnCapacity(@RequestParam(value = "capacity", required = false , defaultValue = "0") int roomCapacity) {
        if(roomCapacity < 0)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDTO("Invalid parameters"));
        List<Room> rooms = roomService.getAllRoomsWithGreaterOrEqualCapacity(roomCapacity);
        List<RoomDTO> response = roomService.fromRoomList(rooms,bookingRepository);
        return ResponseEntity.ok(response);
    }

    public static long fibonacci(long n) {
        if (n <= 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    public static void mergeSort(int[] arr) {
        if (arr.length > 1) {
            int mid = arr.length / 2;
            int[] left = Arrays.copyOfRange(arr, 0, mid);
            int[] right = Arrays.copyOfRange(arr, mid, arr.length);

            mergeSort(left);
            mergeSort(right);

            merge(arr, left, right);
        }
    }

    private static void merge(int[] arr, int[] left, int[] right) {
        int i = 0, j = 0, k = 0;
        while (i < left.length && j < right.length) {
            if (left[i] <= right[j]) {
                arr[k++] = left[i++];
            } else {
                arr[k++] = right[j++];
            }
        }
        while (i < left.length) {
            arr[k++] = left[i++];
        }
        while (j < right.length) {
            arr[k++] = right[j++];
        }
    }
    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivot = partition(arr, low, high);
            quickSort(arr, low, pivot - 1);
            quickSort(arr, pivot + 1, high);
        }
    }

    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static int binarySearch(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }

    public static int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }

    public static int knapsack(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= capacity; j++) {
                if (weights[i - 1] <= j) {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i - 1][j - weights[i - 1]] + values[i - 1]);
                } else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }

        return dp[n][capacity];
    }

    public static String longestPalindrome(String s) {
        int n = s.length();
        boolean[][] dp = new boolean[n][n];
        int maxLength = 1;
        int start = 0;

        for (int i = 0; i < n; i++) {
            dp[i][i] = true;
        }

        for (int i = 0; i < n - 1; i++) {
            if (s.charAt(i) == s.charAt(i + 1)) {
                dp[i][i + 1] = true;
                start = i;
                maxLength = 2;
            }
        }

        for (int len = 3; len <= n; len++) {
            for (int i = 0; i < n - len + 1; i++) {
                int j = i + len - 1;
                if (s.charAt(i) == s.charAt(j) && dp[i + 1][j - 1]) {
                    dp[i][j] = true;
                    start = i;
                    maxLength = len;
                }
            }
        }

        return s.substring(start, start + maxLength);
    }
}