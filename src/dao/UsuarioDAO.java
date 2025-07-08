package dao;

import model.*;
import java.sql.*;

public class UsuarioDAO {
    private AdministradorDAO administradorDAO = new AdministradorDAO();
    private ParticipanteDAO participanteDAO = new ParticipanteDAO();

    public Usuario login(String usuario, String senha) throws SQLException {
        // Tenta como administrador primeiro
        Administrador admin = administradorDAO.login(usuario, senha);
        if (admin != null) return admin;

        // Se não, tenta como participante
        Participante participante = participanteDAO.login(usuario, senha);
        return participante;
    }
}