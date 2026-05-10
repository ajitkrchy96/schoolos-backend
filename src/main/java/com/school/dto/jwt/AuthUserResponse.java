package com.school.dto.jwt;

import com.school.model.AppUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserResponse {
    private Long id;
    private String username;
    private String role;
    private Long schoolId;
    private String fullName;
    private String email;

    public static AuthUserResponse fromAppUser(AppUser user) {
        String username = user.getUsername() != null ? user.getUsername() : "admin";
        String role = user.getRole() != null ? user.getRole() : "ADMIN";
        Long schoolId = null;

        if (user.getSchool() != null && user.getSchool().getId() != null) {
            schoolId = user.getSchool().getId();
        }

        if (schoolId == null) {
            schoolId = 1L;
        }

        String fullName = username.equals("admin") ? "School Admin" : username;
        String email = username.equals("admin") ? "admin@schoolos.com" : username + "@schoolos.com";

        return new AuthUserResponse(user.getId(), username, role, schoolId, fullName, email);
    }
}
