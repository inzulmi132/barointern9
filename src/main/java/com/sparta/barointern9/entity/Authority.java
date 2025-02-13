package com.sparta.barointern9.entity;

import com.sparta.barointern9.enums.AuthorityName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "authorities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthorityName authorityName;
}
