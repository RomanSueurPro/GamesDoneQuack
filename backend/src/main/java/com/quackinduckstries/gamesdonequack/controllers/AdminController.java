package com.quackinduckstries.gamesdonequack.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quackinduckstries.gamesdonequack.Dtos.UserDto;
import com.quackinduckstries.gamesdonequack.entities.Permission;
import com.quackinduckstries.gamesdonequack.entities.Role;
import com.quackinduckstries.gamesdonequack.exceptions.AlreadyExistingRoleNameException;
import com.quackinduckstries.gamesdonequack.exceptions.ExistingPermissionDoesNotExistException;
import com.quackinduckstries.gamesdonequack.exceptions.NewPermissionAlreadyExistsException;
import com.quackinduckstries.gamesdonequack.services.AdminPermissionService;
import com.quackinduckstries.gamesdonequack.services.AdminRoleService;
import com.quackinduckstries.gamesdonequack.services.UserService;


@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")	
@RestController
public class AdminController {

	private final AdminRoleService adminRoleService;
	private final UserService userService;
	private final AdminPermissionService adminPermissionService;
	
	public AdminController(AdminRoleService adminRoleService, UserService userService, AdminPermissionService adminPermissionService) {
		this.userService = userService;
		this.adminRoleService = adminRoleService;
		this.adminPermissionService = adminPermissionService;
	}
	
	
	@GetMapping("/dashboard")
	public ResponseEntity<?> accessDashboard() {
		
		return ResponseEntity.ok(Map.of("message", "You are an admin congratz"));
	}
	
	
	@PostMapping("/deleteuser")
	public ResponseEntity<?> deleteUser(@RequestParam("id")long id) {
		
		UserDto userToBeDeleted = userService.deleteUserById(id);
		
		return ResponseEntity.ok(Map.of("message", "User " + userToBeDeleted.getUsername() + " was succeffully deleted"));
	}
	
	
	@PatchMapping("/updateUserRole")
	public ResponseEntity<?> updateUserRole(@RequestParam("idUser") long idUser, @RequestParam("idRole")long idRole) {
		
		UserDto userToUpdate = userService.updateUserRole(idUser, idRole);
		
		StringBuilder builder = new StringBuilder();
		builder.append("User ")
			.append(userToUpdate.getUsername())
			.append(" was successfully granted ")
			.append(userToUpdate.getRole().getName())
			.append(" role.");
		String message = builder.toString();
		
		return ResponseEntity.ok(Map.of("message", message));
	}
	
	
	@PostMapping("/createRole")
	public ResponseEntity<?> createRole(@RequestParam("name") String name, @RequestParam("existingPermissions") List<String> existingPermissions, @RequestParam("newPermissions") List<String> newPermissions) {
		
		List<Permission> allPermissions = new ArrayList<Permission>();
		List<String> toAddPermissionsName = new ArrayList<String>();
		List<String> errorMessages = new ArrayList<String>(); 
		boolean errorFlag = false;
		
		for(String newPermission : newPermissions) {
			try {
				if(!adminPermissionService.existsByName(newPermission)) {
					toAddPermissionsName.add(newPermission);
				}
				else {
					throw new NewPermissionAlreadyExistsException("New permission " + newPermission + " already exists");
				}
				
			}catch(NewPermissionAlreadyExistsException e) {
				e.printStackTrace();
				errorFlag = true;
				errorMessages.add(e.getMessage());
			}
		}
		
		for(String existingPermission : existingPermissions) {
			try {
				if(adminPermissionService.existsByName(existingPermission)) {
					toAddPermissionsName.add(existingPermission);
				}
				else {
					throw new ExistingPermissionDoesNotExistException("Existing permission " + existingPermission + " does not exist");
				}
				
			}catch(ExistingPermissionDoesNotExistException e) {
				e.printStackTrace();
				errorFlag = true;
				errorMessages.add(e.getMessage());
			}
		}
		
		try {
			if(adminRoleService.existsByName(name)) {
				throw new AlreadyExistingRoleNameException("Role " + name + " already exists");
			}
			
		}catch(AlreadyExistingRoleNameException e) {
			e.printStackTrace();
			errorFlag = true;
			errorMessages.add(e.getMessage());
		}
		
		if(errorFlag) {
			return ResponseEntity.ok(Map.of("error", errorMessages));
		}
		else {
			for (var permissionName : newPermissions) {
				allPermissions.add(adminPermissionService.createPermission(permissionName));
			}

			for (var permissionName : existingPermissions) {
				allPermissions.add(adminPermissionService.getPermissionByName(permissionName));
			}

			Role role = new Role(name, allPermissions);
			adminRoleService.save(role);
			
			return ResponseEntity.ok(Map.of("message", "Role " + role.getName() + " was successfully created"));
		}
	}
	
	
	@PostMapping("/deletepermission")
	public ResponseEntity<?> deletePermission(@RequestParam("id") Long id) {
		
		Permission permission = adminPermissionService.deletePermission(id);
		
		return ResponseEntity.ok(Map.of("message", "Permission " + permission.getName() + " was successfully deleted."));
	}
	
	
	@PostMapping("/createpermission")
	public ResponseEntity<?> createPermission(@RequestParam("name") String name){
		
	        Permission permission = adminPermissionService.createPermission(name);

	        return ResponseEntity.ok(Map.of("message", "Permission " + permission.getName() + " was successfully created."));
	}	
}
