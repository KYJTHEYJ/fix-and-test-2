package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.ImageSaveResponse;
import org.example.expert.domain.user.dto.response.ImageSearchResponse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.service.ImageS3Service;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ImageS3Service imageS3Service;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/users")
    public void changePassword(@AuthenticationPrincipal Long userId, @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(userId, userChangePasswordRequest);
    }

    @PostMapping("/{userId}/profile-image")
    public ResponseEntity<ImageSaveResponse> saveImage (
            @PathVariable Long userId
            , @RequestParam("image") MultipartFile image) {
        String key = imageS3Service.getUploadKey(image);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveImage(userId, key));
    }

    @GetMapping("/{userId}/profile-image")
    public ResponseEntity<ImageSearchResponse> searchImage(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.searchImage(userId));
    }
}
