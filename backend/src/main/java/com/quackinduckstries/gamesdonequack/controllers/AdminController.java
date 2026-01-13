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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.quackinduckstries.gamesdonequack.Dtos.PermissionDto;
import com.quackinduckstries.gamesdonequack.Dtos.RoleDto;
import com.quackinduckstries.gamesdonequack.Dtos.UserDto;
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
	public ResponseEntity<?> createRole(@RequestParam("name") String name, @RequestParam("permissions") List<String> permissions) {
		
		RoleDto role = adminRoleService.createRole(name, permissions);
		
		return ResponseEntity.ok(Map.of("message", "Role " + role.getName() + " was successfully created"));
	}
	
	
	@PostMapping("/deletepermission")
	public ResponseEntity<?> deletePermission(@RequestParam("id") Long id) {
		
		PermissionDto permission = adminPermissionService.deletePermission(id);
		
		return ResponseEntity.ok(Map.of("message", "Permission " + permission.getName() + " was successfully deleted."));
	}
	
	
	@PostMapping("/createpermission")
	public ResponseEntity<?> createPermission(@RequestParam("name") String name, @RequestParam("idRoles") List<Long> idRoles){
		
	        PermissionDto permission = adminPermissionService.createPermission(name, idRoles);

	        return ResponseEntity.ok(Map.of("message", "Permission " + permission.getName() + " was successfully created."));
	}	
	
	@PatchMapping("/updatepermission")
	public ResponseEntity<?> updatePermission(@RequestParam("id") long id, @RequestParam("name") String name) {
		
		PermissionDto permissionToUpdate = adminPermissionService.updatePermission(id, name);
		
		
		return ResponseEntity.ok(Map.of("message", "Renaming to " + permissionToUpdate.getName() + " went fine"));
	}
	
	@PatchMapping("/updaterole")
	public ResponseEntity<?> updateRole(@RequestParam("id") long id, @RequestParam("name") String name, @RequestParam("permissions") List<String> permissions, @RequestParam("isNewDefaultRole") boolean isNewDefaultRole) {
		
		RoleDto roleToUpdate = adminRoleService.updateRole(id, name, permissions, isNewDefaultRole);
		
		return ResponseEntity.ok(Map.of("message", "Updating " + roleToUpdate.getName() + " went fine"));
	}
	
	@GetMapping("/fetchpermissions")
	public ResponseEntity<?> fetchAllPermissions() {
		
		List<PermissionDto> permissions = new ArrayList<>();
		permissions = adminPermissionService.fetchAllPermissions();
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode json = mapper.createObjectNode();
		for(var perm : permissions) {
			json.arrayNode().addPOJO(perm);
		}
		
		return ResponseEntity.ok(permissions);
	}
}
