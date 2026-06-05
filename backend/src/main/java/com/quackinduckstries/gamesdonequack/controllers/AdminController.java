package com.quackinduckstries.gamesdonequack.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.quackinduckstries.gamesdonequack.Dtos.PermissionDto;
import com.quackinduckstries.gamesdonequack.Dtos.PermissionWithoutRoleDto;

import com.quackinduckstries.gamesdonequack.Dtos.RoleCompleteDto;
import com.quackinduckstries.gamesdonequack.Dtos.RoleNoRelationsDto;
import com.quackinduckstries.gamesdonequack.Dtos.RoleNoUserDto;
import com.quackinduckstries.gamesdonequack.Dtos.UserDto;
import com.quackinduckstries.gamesdonequack.services.AdminPermissionService;
import com.quackinduckstries.gamesdonequack.services.AdminRoleService;
import com.quackinduckstries.gamesdonequack.services.UserService;



@RequestMapping("/admin")
@PreAuthorize("hasRole(@adminRoleNameFinderService.getAdminRoleName())")	
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
	public ResponseEntity<?> createRole(@RequestBody RoleNoUserDto roleToCreate) {
		
		RoleCompleteDto role = adminRoleService.createRole(roleToCreate);
		return ResponseEntity.ok(Map.of("message", "Role " + role.getName() + " was successfully created", "id", role.getId()));
	}
	
	
	@DeleteMapping("/deletepermission")
	public ResponseEntity<?> deletePermission(@RequestBody PermissionDto permissionToDelete) {
		
		String permissionName = adminPermissionService.deletePermission(permissionToDelete);
		
		return ResponseEntity.ok(Map.of("message", "Permission " + permissionName + " was successfully deleted."));
	}
	
	
	@PostMapping("/createpermission")
	public ResponseEntity<?> createPermission(@RequestBody PermissionDto permissionToCreate){
		
	        PermissionDto permission = adminPermissionService.createPermission(permissionToCreate);

	        return ResponseEntity.ok(Map.of("message", "Permission " + permission.getName() + " was successfully created.", "id", permission.getId()));
	}	
	
	
	@PatchMapping("/updatepermission")
	public ResponseEntity<?> updatePermission(@RequestBody PermissionDto permissionToUpdate) {
		
		PermissionDto permission = adminPermissionService.updatePermission(permissionToUpdate);
		
		return ResponseEntity.ok(Map.of("message", "Update of permission " + permission.getName() + " went fine"));
	}
	
	
	@PatchMapping("/updaterole")
	public ResponseEntity<?> updateRole(@RequestBody RoleNoUserDto roleToUpdate) {

		adminRoleService.updateRole(roleToUpdate);
		
		return ResponseEntity.ok(Map.of("message", "Update of role " + roleToUpdate.getName() + " went fine"));
	}
	
	
	@GetMapping("/fetchallpermissions")
	public ResponseEntity<?> fetchAllPermissions() {
		
		List<PermissionDto> permissions = new ArrayList<>();
		permissions = adminPermissionService.fetchAllPermissions();

		return ResponseEntity.ok(permissions);
	}
	
	
	@GetMapping("/fetchallpermissionsnorolefield")
	public ResponseEntity<?> fetchAllPermissionsNoRoleField() {
		
		List<PermissionWithoutRoleDto> permissions = new ArrayList<>();
		permissions = adminPermissionService.fetchAllPermissionsNoRoleField();

		return ResponseEntity.ok(permissions);
	}
	
	
	@GetMapping("/fetchallroles")
	public ResponseEntity<?> fetchAllRoles() {
		
		List<RoleNoUserDto> roles = new ArrayList<>();
		roles = adminRoleService.fetchAllRoles();
		
		return ResponseEntity.ok(roles);
	}
	
	
	@GetMapping("/fetchallrolesnopermissionfield")
	public ResponseEntity<?> fetchAllRolesNoPermissionField() {
		
		List<RoleNoRelationsDto> roles = new ArrayList<>();
		roles = adminRoleService.fetchAllRolesNoPermissionField();
		
		return ResponseEntity.ok(roles);
	}
	
	
	@DeleteMapping("/deleterole")
	public ResponseEntity<?> deleteRole(@RequestBody RoleCompleteDto roleToDelete){
		
		String roleName = adminRoleService.deleteRole(roleToDelete.getId());
		return ResponseEntity.ok(Map.of("message", "Role " + roleName + " was deleted successfully"));
	}
}
