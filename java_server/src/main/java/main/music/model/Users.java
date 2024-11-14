package main.music.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.graphql.ConditionalOnGraphQlSchema;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    private String role;
}
