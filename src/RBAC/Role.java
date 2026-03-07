/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RBAC;
    import java.util.Set;

/**
 *
 * @author trisha
 */
public class Role {
    private String name;           private Set<Permission> permissions;

    public Role(String name, Set<Permission> permissions) {
        this.name = name;
        this.permissions = permissions;
    }

    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    public String getName() {
        return name;
    }
}
    
