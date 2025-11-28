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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quackinduckstries.gamesdonequack.Dtos.UserDto;
import com.quackinduckstries.gamesdonequack.entities.Permission;
import com.quackinduckstries.gamesdonequack.entities.Role;
import com.quackinduckstries.gamesdonequack.entities.User;
import com.quackinduckstries.gamesdonequack.repositories.PermissionRepository;
import com.quackinduckstries.gamesdonequack.repositories.RoleRepository;
import com.quackinduckstries.gamesdonequack.repositories.UserRepository;
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
		
		StringBuilder builder = new StringBuilder();
		builder.append("User ")
			.append(userToBeDeleted.getUsername())
			.append(" was succeffully deleted");
		String message = builder.toString();
		
		return ResponseEntity.ok(Map.of("message", message));
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
		for(String newPermission : newPermissions) {
			Permission permission = adminPermissionService.createPermission(newPermission);
			allPermissions.add(permission);
		}
		
		for(String existingPermission : existingPermissions) {
			Permission permission = adminPermissionService.getPermissionByName(existingPermission);
			allPermissions.add(permission);
		}
		Role role = new Role(name, allPermissions);
		adminRoleService.save(role);
		
		return ResponseEntity.ok(Map.of("message", "Role " + role.getName() + " was successfully created"));
	}
	
	@PostMapping("/deletepermission")
	public ResponseEntity<?> deletePermission(@RequestParam("id") Long id) {
		
		Permission permission = adminPermissionService.deletePermission(id);
		
		StringBuilder builder = new StringBuilder();
		builder.append("Permission ")
			.append(permission.getName())
			.append(" was successfully deleted.");
			
		String message = builder.toString();
		
		return ResponseEntity.ok(Map.of("message", message));
	}
	
}
