package com.amigoscode.awsimageupload.datastore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.amigoscode.awsimageupload.profile.UserProfile;

@Repository
public class FakeUserProfileDataStore {

	private static final List<UserProfile> USER_PROFILES = new ArrayList<>();
	
	static {
		USER_PROFILES.add(new UserProfile(UUID.fromString("459e4374-45e4-406a-bfd4-eed0a4404116"), "janetjones", null));
		USER_PROFILES.add(new UserProfile(UUID.fromString("e7280d27-9972-4c07-bde1-b5b7b618afb3"), "antoniojunior", null));
	}
	
	public List<UserProfile> getUserProfiles() {
		return USER_PROFILES;
	}
}
