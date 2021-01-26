package com.amigoscode.awsimageupload.profile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amigoscode.awsimageupload.bucket.BucketName;
import com.amigoscode.awsimageupload.filestore.FileStore;

@Service
public class UserProfileService {
	
	private final UserProfileDataAccessService userProfileDataAccessService;
	private final FileStore fileStore;
	
	@Autowired
	public UserProfileService(UserProfileDataAccessService userProfileDataAccessService, FileStore fileStore) {
		this.userProfileDataAccessService = userProfileDataAccessService;
		this.fileStore = fileStore;
	}
	
	
	List<UserProfile> getUserProfiles() {
		return userProfileDataAccessService.getUserProfiles();
	}

	public void uploadUserProfileImage(String userProfileId, MultipartFile file) {
		// 1. Check if image is not empty
		isFileEmpty(file);
		
		// 2. If file is an image
		isImage(file);
		
		// 3. Check whether does the user exist in database
		UserProfile user = getUserProfileOrThrow(userProfileId);
		
		// 4. if so, grab some metadata from file if any
		Map<String, String> metadata = extractMetadata(file);
		
		// 5. Store the image in s3 and update database (userProfileImageLink) with s3 image link
		String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), user.getUserProfileId());
		String filename = String.format("%s-%s", file.getName(), UUID.randomUUID());
		try {
			fileStore.save(path, filename, Optional.of(metadata), file.getInputStream());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private void isFileEmpty(MultipartFile file) {
		if (file.isEmpty()) {
			throw new IllegalStateException("Cannot upload empty file [" + file.getSize() + "]");
		}
	}
	
	private void isImage(MultipartFile file) {
		if (!Arrays.asList(ContentType.IMAGE_JPEG.getMimeType(), ContentType.IMAGE_PNG.getMimeType(), ContentType.IMAGE_GIF.getMimeType()).contains(file.getContentType())) {
			throw new IllegalStateException("File must be an image [" + file.getContentType() + "]");
		}
	}
	
	private UserProfile getUserProfileOrThrow(String userProfileId) {
		UserProfile user = userProfileDataAccessService
			.getUserProfiles()
			.stream()
			.filter(userProfile -> userProfile.getUserProfileId().equals(UUID.fromString(userProfileId)))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException(String.format("User profile %s not found", userProfileId)));
		return user;
	}
	
	private Map<String, String> extractMetadata(MultipartFile file) {
		Map<String, String> metadata = new HashMap<>();
		metadata.put("Content-Type", file.getContentType());
		metadata.put("Content-Length", String.valueOf(file.getSize()));
		return metadata;
	}
}
