package org.example.expert.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.ImageSaveResponse;
import org.example.expert.domain.user.dto.response.ImageSearchResponse;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.Image;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.ImageRepository;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final ImageS3Service imageS3Service;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        return new UserResponse(user.getId(), user.getEmail(), user.getNickname());
    }

    @Transactional
    public void changePassword(long userId, UserChangePasswordRequest userChangePasswordRequest) {
        validateNewPassword(userChangePasswordRequest);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
    }

    private static void validateNewPassword(UserChangePasswordRequest userChangePasswordRequest) {
        if (userChangePasswordRequest.getNewPassword().length() < 8 ||
                !userChangePasswordRequest.getNewPassword().matches(".*\\d.*") ||
                !userChangePasswordRequest.getNewPassword().matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }

    @Transactional
    public ImageSaveResponse saveImage(Long userId, String key) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User not found"));

        if (imageRepository.countByUserId(userId) >= 1) {
            throw new InvalidRequestException("프로필 이미지는 한개만 등록 가능합니다");
        }

        Image image = Image.register(userId, user.getEmail(), key);
        Image savedImage = imageRepository.save(image);

        return new ImageSaveResponse(
                savedImage.getId()
                , user.getEmail()
                , savedImage.getImageKey()
        );
    }

    @Transactional(readOnly = true)
    public ImageSearchResponse searchImage(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidRequestException("User not found"));
        Image image = imageRepository.findByUserId(userId).orElseThrow(() -> new InvalidRequestException("Image not found"));
        URL downloadUrl = imageS3Service.getDownloadUrl(image.getImageKey());

        return new ImageSearchResponse(
                downloadUrl
        );
    }
}
