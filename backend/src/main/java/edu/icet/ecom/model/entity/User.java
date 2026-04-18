package edu.icet.ecom.model.entity;

import edu.icet.ecom.model.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String userName;
    private String password;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserStatus role;

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings;

    // =====================================================================
    // SPRING SECURITY METHODS (Translating our data for Spring)
    // =====================================================================

    // Tells Spring what role this user has (e.g., "ADMIN" or "CUSTOMER")
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security strictly expects roles to start with "ROLE_"
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    // Tells Spring which field we use to log in (We will use Email!)
    @Override
    public String getUsername() {
        return email;
    }

    // We can just return true for these to say the account is perfectly active
    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

}
