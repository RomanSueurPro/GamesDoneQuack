package com.quackinduckstries.gamesdonequack.controllers;


import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.quackinduckstries.gamesdonequack.Dtos.UserProfileDto;
import com.quackinduckstries.gamesdonequack.config.UserSecurity;
import com.quackinduckstries.gamesdonequack.services.UploadService;
import com.quackinduckstries.gamesdonequack.services.UserService;

@RequestMapping("/users")
@RestController
public class UserController {

    
	private final UserService userService;
	private final UploadService uploadService;
	private final UserSecurity userSecurity;
	
	public UserController(UserService userService, UploadService uploadService, UserSecurity userSecurity) {
        this.userService = userService;
        this.uploadService = uploadService;
        this.userSecurity = userSecurity;
    }    
	
	@PostMapping("/check-username-availability")
	public ResponseEntity<Boolean> checkUsernameAvailability(@RequestBody String name){
		
		if(userService.checkUsernameExistence(name)) {
			return ResponseEntity.ok(false);
		}
		
		return ResponseEntity.ok(true);
	}
	
	@PostMapping("/check-email-availability")
	public ResponseEntity<Boolean> checkEmailAvailability(@RequestBody String email){
		
		if(userService.checkEmailExistence(email)) {
			return ResponseEntity.ok(false);
		}
		
		return ResponseEntity.ok(true);
	}
	
	@PostMapping("/getuserprofilebyid")
	public ResponseEntity<?> getUserProfile(@RequestBody long id){
		return ResponseEntity.ok(userService.getUserProfile(id));
	}
	
	@PostMapping("/getuserprofilebyusername")
	public ResponseEntity<?> getUserProfile(@RequestBody String username){
		return ResponseEntity.ok(userService.getUserProfile(username));
	}
	
	@PatchMapping(value = "/uploadAvatar",
		    consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadAvatar(@RequestPart("file") MultipartFile file, @RequestPart("profile") UserProfileDto profile){
		
		String path = this.uploadService.saveUserAvatar(file, profile);
		
		return ResponseEntity.ok(Map.of("message", path));
	}
	
	@GetMapping("/avatars/{avatarName}")
	public ResponseEntity<Resource> getAvatar(@PathVariable("avatarName") String avatarName) throws IOException{
		
		Resource resource = uploadService.getAvatar(avatarName);
		
		return ResponseEntity.ok()
				.cacheControl(CacheControl.noCache())
				.contentType(MediaType.IMAGE_JPEG)
				.body(resource);
	}
	
	
	@PreAuthorize("hasRole('ADMIN') or @userSecurity.isOwner(#profileDto.id, authentication)")
	@PatchMapping(value = "/editprofile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> editProfile(
			@RequestPart(value = "file", required = false) MultipartFile file,@P("profileDto")
			@RequestPart("profile") UserProfileDto profileDto
			) {
		
	    // Update profile
		if(file != null) {
			uploadService.saveUserAvatar(file, profileDto);
		}
		userService.updateProfile(profileDto);
		
	    return ResponseEntity.ok().build();
	}
}
