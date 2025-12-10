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

import com.quackinduckstries.gamesdonequack.Dtos.PermissionDto;
import com.quackinduckstries.gamesdonequack.Dtos.RoleDto;
import com.quackinduckstries.gamesdonequack.Dtos.UserDto;
import com.quackinduckstries.gamesdonequack.entities.Permission;
import com.quackinduckstries.gamesdonequack.entities.Role;
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
	
	
	@PatchMapping("/updateuserrole")
	public ResponseEntity<?> updateUserRole(@RequestParam("idUser") long idUser, @RequestParam("idRole")long idRole) {
		
		UserDto userToUpdate = userService.updateUserRole(idUser, idRole);
		
		return ResponseEntity.ok(Map.of("message", "User " + userToUpdate.getUsername() + " was successfully granted " + userToUpdate.getRole().getName() + " role."));
	}
	
	
	@PostMapping("/createrole")
	public ResponseEntity<?> createRole(@RequestParam("name") String name, @RequestParam("existingPermissions") List<String> existingPermissions, @RequestParam("newPermissions") List<String> newPermissions) {
		
		Role role = adminRoleService.createRole(name, existingPermissions, newPermissions);
		
		return ResponseEntity.ok(Map.of("message", "Role " + role.getName() + " was successfully created"));
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
	
	@PatchMapping("/updatepermission")
	public ResponseEntity<?> updatePermission(@RequestParam("id") long id, @RequestParam("name") String name) {
		
		PermissionDto permissionToUpdate = adminPermissionService.updatePermission(id, name);
		
		
		return ResponseEntity.ok(Map.of("message", "Renaming to " + permissionToUpdate.getName() + " went fine"));
	}
	
	@PatchMapping("/updaterole")
	public ResponseEntity<?> updateRole(@RequestParam("id") long id, @RequestParam("name") String name, @RequestParam("permissions") List<String> permissions) {
		
		RoleDto roleToUpdate = adminRoleService.updateRole(id, name, permissions);
		
		return ResponseEntity.ok(Map.of("message", "Updating " + roleToUpdate.getName() + " went fine"));
	}
}
