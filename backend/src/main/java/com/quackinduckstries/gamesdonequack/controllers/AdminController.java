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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quackinduckstries.gamesdonequack.Dtos.PermissionAdminRoleListDto;
import com.quackinduckstries.gamesdonequack.Dtos.PermissionDto;
import com.quackinduckstries.gamesdonequack.Dtos.RoleAdminListDto;
import com.quackinduckstries.gamesdonequack.Dtos.RoleCompleteDto;
import com.quackinduckstries.gamesdonequack.Dtos.UserDto;
import com.quackinduckstries.gamesdonequack.services.AdminPermissionService;
import com.quackinduckstries.gamesdonequack.services.AdminRoleService;
import com.quackinduckstries.gamesdonequack.services.UserService;



@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")	
@RestController
public class AdminController {

    private final HomeController homeController;

	private final AdminRoleService adminRoleService;
	private final UserService userService;
	private final AdminPermissionService adminPermissionService;
	
	public AdminController(AdminRoleService adminRoleService, UserService userService, AdminPermissionService adminPermissionService, HomeController homeController) {
		this.userService = userService;
		this.adminRoleService = adminRoleService;
		this.adminPermissionService = adminPermissionService;
		this.homeController = homeController;
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
		
		RoleCompleteDto role = adminRoleService.createRole(name, permissions);
		
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
	public ResponseEntity<?> updatePermission(@RequestParam("id") long id, @RequestParam("name") String name, @RequestParam("idRoles") List<Long> idRoles) {
		
		PermissionDto permissionToUpdate = adminPermissionService.updatePermission(id, name, idRoles);
		
		
		return ResponseEntity.ok(Map.of("message", "Update of permission " + permissionToUpdate.getName() + " went fine"));
	}
	
	@PatchMapping("/updaterole")
	public ResponseEntity<?> updateRole(@RequestParam("id") long id, @RequestParam("name") String name, @RequestParam("permissions") List<String> permissions, @RequestParam("isNewDefaultRole") boolean isNewDefaultRole) {
		
		RoleCompleteDto roleToUpdate = adminRoleService.updateRole(id, name, permissions, isNewDefaultRole);
		
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
		
		List<PermissionAdminRoleListDto> permissions = new ArrayList<>();
		permissions = adminPermissionService.fetchAllPermissionsNoRoleField();

		return ResponseEntity.ok(permissions);
	}
	
	@GetMapping("/fetchallroles")
	public ResponseEntity<?> fetchAllRoles() {
		
		List<RoleAdminListDto> roles = new ArrayList<>();
		roles = adminRoleService.fetchAllRoles();
		
		return ResponseEntity.ok(roles);
	}
	
	@DeleteMapping("deleterole")
	public ResponseEntity<?> deleteRole(@RequestParam("id") long id){
		String roleName = adminRoleService.deleteRole(id);
		return ResponseEntity.ok("Role " + roleName + " was deleted successfully");
	}
}
