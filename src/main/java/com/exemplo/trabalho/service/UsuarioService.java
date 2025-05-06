package com.exemplo.trabalho.service;

import com.exemplo.trabalho.model.Usuario;
import com.exemplo.trabalho.repository.UsuarioRepository;
// Importe UserDetails, UserDetailsService, UsernameNotFoundException do Spring Security
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList; // Para roles do UserDetails
import java.util.Optional;

@Service
public class UsuarioService /* implements UserDetailsService */ { // Descomente o implements para Spring Security

    private final UsuarioRepository usuarioRepository;
    // private final PasswordEncoder passwordEncoder; // Para Spring Security

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository /*, PasswordEncoder passwordEncoder */) {
        this.usuarioRepository = usuarioRepository;
        // this.passwordEncoder = passwordEncoder;
    }

    public Usuario salvarUsuario(Usuario usuario) {
        // Lógica para hashear a senha ANTES de salvar, quando integrar Spring Security
        // usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    /* // Método necessário para Spring Security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        // Aqui você converteria seu objeto Usuario para um objeto UserDetails
        // Exemplo simples:
        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                new ArrayList<>() // Lista de GrantedAuthority (roles)
        );
    }
    */
}