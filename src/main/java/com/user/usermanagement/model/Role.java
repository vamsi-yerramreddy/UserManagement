package com.user.usermanagement.model;

import com.fasterxml.jackson.databind.ser.Serializers;
import jakarta.persistence.Entity;

@Entity
public class Role extends BaseModel {
    private String roleName;

        public Role() {
        }

        public Role(String roleName) {
            this.roleName = roleName;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

}
