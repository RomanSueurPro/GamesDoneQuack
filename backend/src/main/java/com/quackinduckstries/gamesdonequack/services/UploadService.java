package com.quackinduckstries.gamesdonequack.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.quackinduckstries.gamesdonequack.Dtos.UserProfileDto;
import com.quackinduckstries.gamesdonequack.controllers.HomeController;
import com.quackinduckstries.gamesdonequack.entities.User;
import com.quackinduckstries.gamesdonequack.repositories.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class UploadService {

    private final HomeController homeController;
    private final UserRepository userRepository;

	//deployment profiles considerations
	@Value("${app.upload.avatar-directory}")
	private String avatarDirectory;

    UploadService(HomeController homeController, UserRepository userRepository) {
        this.homeController = homeController;
        this.userRepository = userRepository;
    }

	@PostConstruct
	public void initializeUploadDirectory() throws IOException {
	    Files.createDirectories(Paths.get(avatarDirectory));
	}
	//deployment profiles considerations
	
	
	
	public String saveUserAvatar(MultipartFile file, UserProfileDto userProfileDto) {
		
		String extension = switch (file.getContentType()) {
	    case "image/jpeg" -> ".jpg";
	    case "image/png" -> ".png";
	    case "image/webp" -> ".webp";
	    case "image/svg" -> ".svg";
	    default -> throw new IllegalArgumentException("Unsupported image format");
	};
		String avatarName = userProfileDto.getUsername()+"_avatar" + extension;
		Path destination = Paths.get(avatarDirectory)
	            .resolve(avatarName);

	    try {
	    		if(userProfileDto.getAvatarFileName() != null) {
	    			File previousAvatar = new File(destination.toString());
	    			previousAvatar.delete();
	    		}
			Files.copy(file.getInputStream(), destination);
			updateUserAvatarStatus(userProfileDto, avatarName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return destination.toString();
	}
	
	@Transactional
	private void updateUserAvatarStatus(UserProfileDto userProfile, String avatarName) {
		Optional<User> userOption = userRepository.findById(userProfile.getId());
		if(userOption.isPresent()) {
			User user = userOption.get();
			user.setAvatarFileName(avatarName);
			userRepository.flush();
		}
	}
	
	public Resource getAvatar(String avatarName) throws IOException{
		
		Path filePath = Paths.get(avatarDirectory).resolve(avatarName).normalize();
		
		Resource resource = new UrlResource(filePath.toUri());
		
		if(!resource.exists() || !resource.isReadable()){
			throw new FileNotFoundException("Could not find avatar");
		}
		return resource;
	}
	
}
